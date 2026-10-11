#include "presenter.h"
#include <SDL.h>
#include <SDL_vulkan.h>
#include <cassert>
#include <cstdio>
#include <chrono>
#include <vector>
int main(){
 assert(SDL_Init(SDL_INIT_VIDEO)==0);auto* window=SDL_CreateWindow("MMZ Vulkan real WSI test",0,0,640,480,SDL_WINDOW_VULKAN|SDL_WINDOW_RESIZABLE);if(!window){fprintf(stderr,"%s\n",SDL_GetError());return 1;}
 unsigned n=0;assert(SDL_Vulkan_GetInstanceExtensions(window,&n,nullptr));std::vector<const char*> extensions(n);assert(SDL_Vulkan_GetInstanceExtensions(window,&n,extensions.data()));
 mmz::VulkanPresenter vk;assert(vk.open(extensions,[&](VkInstance i,VkSurfaceKHR* s){return SDL_Vulkan_CreateSurface(window,i,s)?VK_SUCCESS:VK_ERROR_INITIALIZATION_FAILED;}));
 std::vector<unsigned char> pixels(240*160*3);for(int y=0;y<160;y++)for(int x=0;x<240;x++){int p=(y*240+x)*3;pixels[p]=x;pixels[p+1]=y;pixels[p+2]=(x+y)%255;}
 for(int scale=1;scale<=8;scale++)for(bool filter:{false,true}){auto begin=std::chrono::steady_clock::now();int success=0;for(int i=0;i<12;i++){SDL_PumpEvents();int result=vk.present(pixels.data(),240,160,scale,filter,{20,30,vk.width()-40,vk.height()-60});assert(result>=0);success+=result;}assert(success>=10);double ms=std::chrono::duration<double,std::milli>(std::chrono::steady_clock::now()-begin).count()/12;printf("VK_BENCH scale=%d filter=%d submitted=%d mean_ms=%.3f wait_ms=%.3f upload_ms=%.3f submit_ms=%.3f\n",scale,filter,success,ms,vk.waitMs,vk.uploadMs,vk.submitMs);}
 SDL_SetWindowSize(window,800,500);SDL_Delay(100);SDL_PumpEvents();for(int i=0;i<8;i++)assert(vk.present(pixels.data(),240,160,1,false,{0,0,500,400})>=0);
 vk.close();assert(vk.open(extensions,[&](VkInstance i,VkSurfaceKHR* s){return SDL_Vulkan_CreateSurface(window,i,s)?VK_SUCCESS:VK_ERROR_INITIALIZATION_FAILED;}));assert(vk.present(pixels.data(),240,160,1,true,{0,0,600,400})==1);vk.close();SDL_DestroyWindow(window);SDL_Quit();puts("PASS Vulkan instance/device/surface/swapchain, 192 presents, 1–8x filters, resize, teardown/restart");
}
