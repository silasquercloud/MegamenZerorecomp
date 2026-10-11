#include "enhanced.h"
#include "vulkan/presenter.h"
#include <SDL_vulkan.h>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <string>
namespace {mmz::VulkanPresenter vk;SDL_Window* window=nullptr;bool failed=false;}
bool mmz_vulkan_present(const unsigned char* rgb,int w,int h){
 const char* api=std::getenv("MMZ_TEST_API");if(!api||std::string(api)!="vulkan"||failed)return false;
 if(!window){window=SDL_CreateWindow("MMZ recompilado • Vulkan Linux test",0,0,640,480,SDL_WINDOW_VULKAN|SDL_WINDOW_RESIZABLE);if(!window){fprintf(stderr,"Vulkan SDL window failed %s\n",SDL_GetError());failed=true;return false;}
 unsigned count=0;SDL_Vulkan_GetInstanceExtensions(window,&count,nullptr);std::vector<const char*> ext(count);SDL_Vulkan_GetInstanceExtensions(window,&count,ext.data());if(!vk.open(ext,[](VkInstance i,VkSurfaceKHR* s){return SDL_Vulkan_CreateSurface(window,i,s)?VK_SUCCESS:VK_ERROR_INITIALIZATION_FAILED;})){failed=true;return false;}}
 int dw=vk.width(),dh=vk.height(),width=dw,height=dw*2/3,x=0,y=(dh-height)/2;mmz_adjust_layout(dw,dh,x,y,width,height);
 int r=vk.present(rgb,w,h,mmz_scale(),mmz_linear(),{x,y,width,height});if(r<0){failed=true;return false;}if(r){mmz_video_dimensions(w,h,width,height);mmz_gpu_metrics(vk.waitMs,vk.uploadMs,vk.submitMs);mmz_end_present();}return true;
}
void mmz_vulkan_close(){vk.close();if(window)SDL_DestroyWindow(window);window=nullptr;}
