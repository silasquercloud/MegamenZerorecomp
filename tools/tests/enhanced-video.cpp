#include "enhanced.h"
#include <jni.h>
#include <cassert>
#include <cstdio>
#include <thread>
#include <chrono>
#include <vector>
extern "C" void Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(JNIEnv*,jclass,jint,jint,jint,jboolean);
int main(){
 SDL_SetHint(SDL_HINT_VIDEODRIVER,"dummy");assert(SDL_Init(SDL_INIT_VIDEO)==0);
 SDL_Surface* surface=SDL_CreateRGBSurfaceWithFormat(0,320,240,32,SDL_PIXELFORMAT_RGBA8888);
 SDL_Renderer* renderer=SDL_CreateSoftwareRenderer(surface);assert(renderer);
 SDL_Texture* image=SDL_CreateTexture(renderer,SDL_PIXELFORMAT_RGB24,SDL_TEXTUREACCESS_STREAMING,240,160);assert(image);
 std::vector<unsigned char> pixels(240*160*3);for(int y=0;y<160;y++)for(int x=0;x<240;x++)pixels[(y*240+x)*3]=(x%2)*255;
 assert(SDL_UpdateTexture(image,nullptr,pixels.data(),720)==0);
 unsigned long long nearestHash=0;
 for(int s=1;s<=8;s++)for(int f=0;f<2;f++){
  Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(nullptr,nullptr,60,s,1,f);
  assert(mmz_filter(!f)==bool(f));assert(mmz_filter(!f)==bool(!f)); // native runtime menu changes remain effective
  SDL_Rect dst{0,0,320,213};
  auto t0=std::chrono::steady_clock::now();
  for(int repeat=0;repeat<10;repeat++)mmz_render_image(renderer,image,dst,240,160,f);
  double submit=std::chrono::duration<double,std::milli>(std::chrono::steady_clock::now()-t0).count()/10;
  double state[12];mmz_stats(state);assert(state[8]==240*s && state[9]==160*s);
  std::printf("BENCH host-software scale=%d filter=%d submit_ms=%.3f\n",s,f,submit);
  SDL_Surface* read=SDL_CreateRGBSurfaceWithFormat(0,320,240,32,SDL_PIXELFORMAT_RGBA8888);assert(SDL_RenderReadPixels(renderer,nullptr,read->format->format,read->pixels,read->pitch)==0);
  unsigned long sum=0;for(int i=0;i<read->pitch*213;i++)sum+=static_cast<unsigned char*>(read->pixels)[i];assert(sum>0);
  unsigned long long hash=1469598103934665603ull;for(int i=0;i<read->pitch*213;i++)hash=(hash^static_cast<unsigned char*>(read->pixels)[i])*1099511628211ull;
  if(!f)nearestHash=hash;else assert(hash!=nearestHash);
  SDL_FreeSurface(read);
  SDL_ScaleMode mode;assert(SDL_GetTextureScaleMode(image,&mode)==0);assert(mode==(f?SDL_ScaleModeLinear:SDL_ScaleModeNearest));
 }
 for(int a=1;a<=6;a++){Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(nullptr,nullptr,60,1,a,false);int x=0,y=0,w=0,h=0;mmz_adjust_layout(1920,1080,x,y,w,h);assert(w>0&&h>0&&w<=1920&&h<=1080);if(a==1)assert(w==1620&&h==1080);if(a==6)assert(w==1920&&h==1080);}
 for(int cap: {30,60,90,120,-1,0}){
  Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(nullptr,nullptr,cap,1,0,false);int presented=0;auto pacedStart=std::chrono::steady_clock::now();
  for(unsigned i=1;i<=60;i++){mmz_simulation_frame(i);if(mmz_begin_present()){SDL_RenderPresent(renderer);mmz_end_present();++presented;}std::this_thread::sleep_for(std::chrono::microseconds(16743));}
  double elapsed=std::chrono::duration<double>(std::chrono::steady_clock::now()-pacedStart).count();
  if(cap==30)assert(presented>0 && presented<=elapsed*30+1);else assert(presented==60);
  double measured[12];mmz_stats(measured);assert(measured[0]>0 && measured[1]>0 && measured[2]>0);
  std::printf("PASS cap=%d simulation_ticks=60 actual_present_calls=%d elapsed_s=%.3f\n",cap,presented,elapsed);
 }
 mmz_close_video();SDL_DestroyTexture(image);SDL_DestroyRenderer(renderer);SDL_FreeSurface(surface);SDL_Quit();
 puts("PASS real SDL software renderer: scales 1–8, nearest/bilinear, aspect ratios, presentation caps preserve guest iterations");
}
