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
