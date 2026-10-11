#include "presenter.h"
#include "../enhanced.h"
#include <jni.h>
#include <android/native_window_jni.h>
#include <vulkan/vulkan_android.h>
#include <atomic>
#include <mutex>
#include <cstdio>
namespace {
std::mutex surfaceLock;
ANativeWindow* window=nullptr;
mmz::VulkanPresenter presenter;
std::atomic<int> request{0},effective{-1};
bool ready=false,failed=false;
void reset(){presenter.close();ready=false;}
}
extern "C" JNIEXPORT void JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_surface(JNIEnv* env,jclass,jobject surface){
 std::lock_guard<std::mutex> guard(surfaceLock);reset();if(window)ANativeWindow_release(window);window=surface?ANativeWindow_fromSurface(env,surface):nullptr;if(window){failed=false;effective=-1;}
}
extern "C" JNIEXPORT void JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_backend(JNIEnv*,jclass,jint choice){
 std::lock_guard<std::mutex> guard(surfaceLock);if(request.exchange(choice)!=choice){reset();failed=false;effective=choice==2?0:-1;}
}
extern "C" JNIEXPORT jint JNICALL Java_com_silasquercloud_megamanzeronative_VideoSettings_activeBackend(JNIEnv*,jclass){return effective.load();}
bool mmz_vulkan_present(const unsigned char* rgb,int w,int h){
 std::lock_guard<std::mutex> guard(surfaceLock);
 if(request==2||failed||!window)return false;
 if(!ready){ready=presenter.open({VK_KHR_SURFACE_EXTENSION_NAME,VK_KHR_ANDROID_SURFACE_EXTENSION_NAME},[](VkInstance instance,VkSurfaceKHR* surface){VkAndroidSurfaceCreateInfoKHR ci{VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR};ci.window=window;return vkCreateAndroidSurfaceKHR(instance,&ci,nullptr,surface);});
  if(!ready){std::fprintf(stderr,"MMZ_VK_FALLBACK initialization failed; SDL active\n");reset();failed=true;effective=-2;return false;}}
 int dw=presenter.width(),dh=presenter.height(),width=dw*62/100,height=width*2/3,x=0,y=0;if(height>dh*9/10){height=dh*9/10;width=height*3/2;}x=(dw-width)/2;y=(dh-height)/2;mmz_adjust_layout(dw,dh,x,y,width,height);
 int scale=mmz_scale();int result=presenter.present(rgb,w,h,scale,mmz_linear(),{x,y,width,height});
 if(result<0){std::fprintf(stderr,"MMZ_VK_FALLBACK presentation failed; SDL active\n");reset();failed=true;effective=-2;return false;}
 if(result==1){effective=1;mmz_video_dimensions(w*scale,h*scale,width,height);mmz_gpu_metrics(presenter.waitMs,presenter.uploadMs,presenter.submitMs);mmz_end_present();}
 return true; // Includes bounded acquire timeout: do not submit/count a duplicate via SDL.
}
void mmz_vulkan_close(){std::lock_guard<std::mutex> guard(surfaceLock);reset();}
