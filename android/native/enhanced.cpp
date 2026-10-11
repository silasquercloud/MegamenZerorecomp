#include "enhanced.h"
#include "effects.h"
#include <vector>
#include <array>
#include <jni.h>
#include <atomic>
#include <algorithm>
#include <chrono>
#include <mutex>
#include <cstdio>
#include <ctime>
#include <cmath>
namespace {
std::atomic<unsigned> revision{1};
std::atomic<double> lastPresented{0};
double interpreterMs=0;unsigned interpreterCalls=0;
std::atomic<bool> reset{false};
std::atomic<int> cap{0},scale{0},aspect{0},linear{0};
std::mutex lock;
double stats[26]{};
std::atomic<int> effect{0};
std::atomic<unsigned> touch{0};
std::array<double,240> frameTimes{};unsigned timeCount=0,timeIndex=0;
using Clock=std::chrono::steady_clock;
double now(){return std::chrono::duration<double>(Clock::now().time_since_epoch()).count();}
double cpu(){timespec t{};clock_gettime(CLOCK_THREAD_CPUTIME_ID,&t);return t.tv_sec+t.tv_nsec*1e-9;}
double next=0,start=0,last=0,begin=0,lastcpu=0,maxms=0,renderSum=0,cpuSum=0;
unsigned presents=0,sim=0; unsigned long long lastFrame=0;
SDL_Texture* upscale=nullptr;SDL_Renderer* owner=nullptr;int textureScale=0,previous=-1;
}
void mmz_simulation_frame(unsigned long long frame){if(frame>lastFrame)sim+=static_cast<unsigned>(frame-lastFrame);lastFrame=frame;}
bool mmz_begin_present(){
 double t=now();int c=cap.load();
 if(reset.exchange(false)){start=last=next=0;presents=sim=0;maxms=renderSum=cpuSum=0;timeCount=timeIndex=0;}
 if(c!=previous){next=0;previous=c;std::fprintf(stderr,"MMZ_VIDEO cap_requested=%d effective_max=%.4f simulation_pacer=unchanged\n",c,c==30?30.:59.7275);}
 // Guest FramePacer owns normal cadence. Avoid a second near-60 Hz gate.
 if(c==30){if(t<next)return false;next=t+1./30.;}
 begin=t;return true;
}
void mmz_adjust_layout(int w,int h,int& x,int& y,int& width,int& height){
 int a=aspect.load();if(a==0)return; // automatic preserves upstream control-safe original layout
 double ratio=1.5;switch(a){case 2:ratio=4./3.;break;case 3:ratio=16./9.;break;case 4:ratio=2.;break;case 5:ratio=20./9.;break;case 6:width=w;height=h;x=y=0;return;}
 width=std::min(w,static_cast<int>(h*ratio));height=std::min(h,static_cast<int>(width/ratio));x=(w-width)/2;y=(h-height)/2;
}
void mmz_close_video(){if(upscale)SDL_DestroyTexture(upscale);upscale=nullptr;owner=nullptr;textureScale=0;}
void mmz_render_image(SDL_Renderer* renderer,SDL_Texture* source,const SDL_Rect& dst,int bw,int bh,bool filter){
 int s=scale.load();if(s==0)s=1; // automatic favors minimal GPU work; display still scales to dst
 SDL_SetTextureScaleMode(source,filter?SDL_ScaleModeLinear:SDL_ScaleModeNearest);
 if(owner!=renderer || textureScale!=s){mmz_close_video();owner=renderer;textureScale=s;
  if(s>1)upscale=SDL_CreateTexture(renderer,SDL_PIXELFORMAT_RGBA8888,SDL_TEXTUREACCESS_TARGET,bw*s,bh*s);
  std::fprintf(stderr,"MMZ_VIDEO framebuffer=%dx%d upscale_requested=%d target=%dx%d filter=%s output=%dx%d SDL_error=%s\n",bw,bh,s,bw*s,bh*s,filter?"bilinear":"nearest",dst.w,dst.h,upscale||s==1?"none":SDL_GetError());
 }
 bool ok=false;
 if(upscale && SDL_SetRenderTarget(renderer,upscale)==0){
  bool copied=SDL_RenderCopy(renderer,source,nullptr,nullptr)==0;
  bool restored=SDL_SetRenderTarget(renderer,nullptr)==0;
  SDL_SetTextureScaleMode(upscale,filter?SDL_ScaleModeLinear:SDL_ScaleModeNearest);
  if(copied&&restored)ok=SDL_RenderCopy(renderer,upscale,nullptr,&dst)==0;
 }
 if(!ok){SDL_SetRenderTarget(renderer,nullptr);SDL_RenderCopy(renderer,source,nullptr,&dst);}
 std::lock_guard<std::mutex> guard(lock);stats[8]=ok?bw*s:bw;stats[9]=ok?bh*s:bh;stats[10]=dst.w;stats[11]=dst.h;
}
void mmz_end_present(){
 double t=now(),c=cpu();lastPresented=t;if(start==0){start=t;last=t;lastcpu=c;return;}
 double ms=(t-last)*1000.;frameTimes[timeIndex++%frameTimes.size()]=ms;timeCount=std::min(timeCount+1,unsigned(frameTimes.size()));last=t;maxms=std::max(maxms,ms);renderSum+=(t-begin)*1000.;cpuSum+=(c-lastcpu)*1000.;lastcpu=c;++presents;
 double span=t-start;if(span<.5)return;
 auto sorted=frameTimes;std::sort(sorted.begin(),sorted.begin()+timeCount);
 {std::lock_guard<std::mutex> guard(lock);stats[12]=sorted[std::min(timeCount-1,unsigned(timeCount*.95))];stats[13]=sorted[std::min(timeCount-1,unsigned(timeCount*.99))];stats[0]=presents/span;stats[1]=span*1000./presents;stats[2]=sim/span;stats[3]=maxms;stats[4]=cpuSum/presents;stats[5]=renderSum/presents;stats[6]=presents;stats[7]=cap.load()==30?30:59.7275;}
 static double logged=0;if(t-logged>=5){std::fprintf(stderr,"MMZ_PHASE guest_ms=%.3f ppu_ms=%.3f audio_ms=%.3f input_ms=%.3f pacer_ms=%.3f interp_total=%.0f misses=%.0f p95=%.3f p99=%.3f acquire_ms=%.3f upload_ms=%.3f submit_ms=%.3f effect_ms=%.3f\n",stats[17],stats[18],stats[19],stats[20],stats[21],stats[22],stats[23],stats[12],stats[13],stats[14],stats[15],stats[16],stats[24]);std::fprintf(stderr,"MMZ_PERF render_fps=%.2f simulation_fps=%.2f frame_ms=%.2f peak_ms=%.2f cpu_ms_per_present=%.2f render_submit_vsync_ms=%.2f\n",presents/span,sim/span,span*1000./presents,maxms,cpuSum/presents,renderSum/presents);std::fflush(stderr);logged=t;}
 start=t;presents=sim=0;maxms=renderSum=cpuSum=0;
}
extern "C" JNIEXPORT void JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(JNIEnv*,jclass,jint c,jint s,jint a,jboolean f){cap=c;scale=std::clamp(static_cast<int>(s),0,8);aspect=std::clamp(static_cast<int>(a),0,6);linear=f;revision.fetch_add(1);reset=true;}
extern "C" JNIEXPORT jdoubleArray JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_stats(JNIEnv* env,jclass){auto array=env->NewDoubleArray(26);if(array){std::lock_guard<std::mutex> guard(lock);double snapshot[26];std::copy(stats,stats+26,snapshot);if(now()-lastPresented.load()>1.5){snapshot[0]=0;snapshot[2]=0;}env->SetDoubleArrayRegion(array,0,26,snapshot);}return array;}

void mmz_stats(double* output){std::lock_guard<std::mutex> guard(lock);std::copy(stats,stats+12,output);}

bool mmz_filter(bool current){static unsigned applied=0;unsigned r=revision.load();if(r!=applied){applied=r;return linear.load()!=0;}return current;}

int mmz_scale(){return std::max(1,scale.load());}
bool mmz_linear(){return linear.load()!=0;}
void mmz_video_dimensions(int w,int h,int dw,int dh){std::lock_guard<std::mutex> guard(lock);stats[8]=w;stats[9]=h;stats[10]=dw;stats[11]=dh;}
void mmz_gpu_metrics(double wait,double upload,double submit){std::lock_guard<std::mutex> guard(lock);stats[14]=wait;stats[15]=upload;stats[16]=submit;}
const unsigned char* mmz_effects(const unsigned char* rgb,int w,int h){int e=effect.load();if(!e){std::lock_guard<std::mutex> guard(lock);stats[24]=0;return rgb;}double t=now();static std::vector<unsigned char> pixels;pixels.resize(w*h*3);mmz_effect(pixels.data(),rgb,w,h,e);{std::lock_guard<std::mutex> guard(lock);stats[24]=(now()-t)*1000.;}return pixels.data();}
unsigned mmz_touch_mask(){return touch.load();}
void mmz_phase(double guest,double ppu,double audio,double input,double pacer,double present,unsigned long long interpreted,unsigned misses){std::lock_guard<std::mutex> guard(lock);stats[17]=guest;stats[18]=ppu;stats[19]=audio;stats[20]=input;stats[21]=pacer;stats[22]=interpreted;stats[23]=misses;stats[25]=present;}
extern "C" JNIEXPORT void JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_effect(JNIEnv*,jclass,jint e){effect=std::clamp(int(e),0,4);}
extern "C" JNIEXPORT void JNICALL Java_com_silasquercloud_megamanzeronative_TouchOverlay_keys(JNIEnv*,jclass,jint mask){touch=unsigned(mask)&1023;}

bool mmz_presented(){return lastPresented.load()>0;}
void mmz_reset_session(){lastFrame=0;touch=0;interpreterMs=0;interpreterCalls=0;lastPresented=0;reset=true;std::lock_guard<std::mutex> guard(lock);std::fill(stats,stats+26,0.);}
double mmz_clock_ms(){return now()*1000.;}
void mmz_interpreter_cost(double started){interpreterMs+=mmz_clock_ms()-started;++interpreterCalls;if(interpreterCalls<=16 || interpreterCalls%1024==0)std::fprintf(stderr,"MMZ_INTERPRETER dispatch_calls=%u total_bridge_ms=%.6f\n",interpreterCalls,interpreterMs);}
