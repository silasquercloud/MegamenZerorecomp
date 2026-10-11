#pragma once
#include <cstdint>
// Small native framebuffer processing pass shared by both APIs; no invented detail.
inline void mmz_effect(uint8_t* out,const uint8_t* in,int w,int h,int effect){
 for(int y=0;y<h;y++)for(int x=0;x<w;x++)for(int c=0;c<3;c++){
  int offset=(y*w+x)*3;int v=in[offset+c];
  if(effect==1){if(y&1)v=v*70/100;if(x%3!=c)v=v*82/100;} // CRT: scanline + RGB mask
  else if(effect==2){if(y&1)v=v*65/100;} // scanlines
  else if(effect==3){v=v*90/100+12;if(x%3!=c)v=v*88/100;} // LCD subpixel pattern
  else if(effect==4)v=(in[offset]*77+in[offset+1]*150+in[offset+2]*29)>>8;
  out[offset+c]=uint8_t(v>255?255:v);
 }
}
