#include "presenter.h"
#include "enhanced.h"
#include <SDL.h>
#include <SDL_vulkan.h>
#include <vector>
#include <array>
#include <algorithm>
#include <chrono>
#include <thread>
#include <cassert>
#include <cstdio>
#include <ctime>
extern "C" void Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(void*,void*,int,int,int,unsigned char);
double cpu(){timespec t{};clock_gettime(CLOCK_THREAD_CPUTIME_ID,&t);return t.tv_sec*1000.+t.tv_nsec/1e6;}
int main(){assert(SDL_Init(SDL_INIT_VIDEO)==0);for(int api=0;api<2;api++)for(int scale: {1,4,8})for(bool filter:{false,true}){
 SDL_Window* window=SDL_CreateWindow("Equal synthetic frame workload",0,0,640,480,api?SDL_WINDOW_VULKAN:0);assert(window);SDL_Renderer* renderer=nullptr;SDL_Texture* texture=nullptr;mmz::VulkanPresenter vk;
 if(api){unsigned n=0;SDL_Vulkan_GetInstanceExtensions(window,&n,nullptr);std::vector<const char*> ex(n);SDL_Vulkan_GetInstanceExtensions(window,&n,ex.data());assert(vk.open(ex,[&](VkInstance i,VkSurfaceKHR*s){return SDL_Vulkan_CreateSurface(window,i,s)?VK_SUCCESS:VK_ERROR_INITIALIZATION_FAILED;}));}
 else{renderer=SDL_CreateRenderer(window,-1,0);assert(renderer);SDL_RendererInfo info{};SDL_GetRendererInfo(renderer,&info);printf("SDL_DRIVER %s\n",info.name);texture=SDL_CreateTexture(renderer,SDL_PIXELFORMAT_RGB24,SDL_TEXTUREACCESS_STREAMING,240,160);assert(texture);Java_com_silasquercloud_megamanzeronative_VideoSettings_apply(nullptr,nullptr,60,scale,1,filter);}
 std::vector<unsigned char> rgb(240*160*3);std::vector<double> cost;auto next=std::chrono::steady_clock::now();double cpuStart=cpu();int presents=0;
 for(int frame=0;frame<120;frame++){if(frame==30)cpuStart=cpu();for(int p=0;p<240*160;p++){rgb[p*3]=(p+frame)%255;rgb[p*3+1]=(p/240+frame)%255;rgb[p*3+2]=frame;}
 auto begin=std::chrono::steady_clock::now();SDL_PumpEvents();if(api){int result=vk.present(rgb.data(),240,160,scale,filter,{0,26,640,426});assert(result>=0);presents+=result;}else{SDL_UpdateTexture(texture,nullptr,rgb.data(),720);SDL_SetRenderDrawColor(renderer,0,0,0,255);SDL_RenderClear(renderer);mmz_render_image(renderer,texture,{0,26,640,426},240,160,filter);SDL_RenderPresent(renderer);presents++;}
 if(frame>=30)cost.push_back(std::chrono::duration<double,std::milli>(std::chrono::steady_clock::now()-begin).count());next+=std::chrono::microseconds(16743);std::this_thread::sleep_until(next);}
 double cpuTotal=cpu()-cpuStart,mean=0;for(double x:cost)mean+=x;std::sort(cost.begin(),cost.end());printf("COMPARE api=%s scale=%d linear=%d presents=%d mean_ms=%.3f p95_ms=%.3f p99_ms=%.3f thread_cpu_ms=%.3f\n",api?"Vulkan":"SDL",scale,filter,presents-30,mean/cost.size(),cost[85],cost[89],cpuTotal/90);
 if(api)vk.close();else{mmz_close_video();SDL_DestroyTexture(texture);SDL_DestroyRenderer(renderer);}SDL_DestroyWindow(window);
 }SDL_Quit();}
