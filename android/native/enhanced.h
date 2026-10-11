#pragma once
#include <SDL.h>
void mmz_simulation_frame(unsigned long long frame);
bool mmz_begin_present();
void mmz_adjust_layout(int w,int h,int& x,int& y,int& width,int& height);
void mmz_render_image(SDL_Renderer*,SDL_Texture*,const SDL_Rect&,int,int,bool);
void mmz_end_present();
void mmz_close_video();

void mmz_stats(double* output);
bool mmz_filter(bool current);

int mmz_scale();bool mmz_linear();
void mmz_video_dimensions(int,int,int,int);
void mmz_gpu_metrics(double,double,double);
bool mmz_vulkan_present(const unsigned char*,int,int);
void mmz_vulkan_close();
const unsigned char* mmz_effects(const unsigned char*,int,int);
unsigned mmz_touch_mask();
void mmz_phase(double,double,double,double,double,double,unsigned long long,unsigned);

void mmz_reset_session();
double mmz_clock_ms();
void mmz_interpreter_cost(double started);
struct MmzInterpreterScope {double started=mmz_clock_ms();~MmzInterpreterScope(){mmz_interpreter_cost(started);}};
