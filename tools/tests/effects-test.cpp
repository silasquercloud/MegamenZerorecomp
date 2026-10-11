#include "effects.h"
#include <array>
#include <cassert>
#include <cstdio>
int main(){std::array<unsigned char,12*8*3> in,out;in.fill(200);mmz_effect(out.data(),in.data(),12,8,0);assert(out==in);for(int e=1;e<=3;e++){mmz_effect(out.data(),in.data(),12,8,e);assert(out!=in);if(e==1){assert(out[0]>out[1]);assert(out[0]>out[36]);}if(e==2)assert(out[0]>out[36]);}for(unsigned i=0;i<in.size();i++)in[i]=i%255;mmz_effect(out.data(),in.data(),12,8,4);for(unsigned i=0;i<out.size();i+=3)assert(out[i]==out[i+1]&&out[i]==out[i+2]);puts("PASS CRT RGB mask/scanlines, scanline effect, LCD and monochrome pixel output");}
