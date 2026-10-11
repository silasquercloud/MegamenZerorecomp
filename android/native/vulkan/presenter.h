#pragma once
#include <vulkan/vulkan.h>
#include <vector>
#include <functional>
#include <cstdint>
namespace mmz {
struct Rect {int x,y,w,h;};
// Two upload slots; present-complete semaphores belong to swapchain images.
class VulkanPresenter {
public:
 ~VulkanPresenter(){close();}
 bool open(const std::vector<const char*>& extensions, const std::function<VkResult(VkInstance,VkSurfaceKHR*)>& surface);
 // 1 queued present, 0 bounded wait/recreate, -1 hard failure (caller falls back).
 int present(const uint8_t* rgb,int w,int h,int scale,bool linear,Rect dst);
 void close();
 int width() const{return extent.width;} int height() const{return extent.height;}
 double waitMs=0,uploadMs=0,submitMs=0;
 uint64_t presented=0;
private:
 struct Image {VkImage image{};VkDeviceMemory memory{};};
 struct Slot {VkBuffer upload{};VkDeviceMemory memory{};void* mapped{};VkCommandBuffer cmd{};VkFence fence{};VkSemaphore acquired{};Image source,scaled;};
 VkInstance instance{};VkPhysicalDevice physical{};VkDevice device{};VkSurfaceKHR surface{};VkQueue queue{};uint32_t family{};
 VkSwapchainKHR swapchain{};VkExtent2D extent{};VkFormat format{};VkCommandPool pool{};
 std::vector<VkImage> images;std::vector<VkSemaphore> complete;Slot slots[2]{};unsigned current=0;
 int sourceW=0,sourceH=0,factor=0;
 bool makeSwapchain();bool makeUploads(int w,int h,int scale);void clearUploads();void clearSwapchain();
 uint32_t memoryType(uint32_t bits,VkMemoryPropertyFlags flags);
 bool createImage(Image&,int,int);void destroyImage(Image&);
};
}
