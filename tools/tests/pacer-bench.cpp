#include "host_platform.h"
#include <chrono>
#include <ctime>
#include <cstdio>
int main(){timespec a{},b{};clock_gettime(CLOCK_THREAD_CPUTIME_ID,&a);auto start=std::chrono::steady_clock::now();gbarecomp::FramePacer pacer;for(int i=0;i<120;i++)pacer.wait_for_next_frame();clock_gettime(CLOCK_THREAD_CPUTIME_ID,&b);double wall=std::chrono::duration<double,std::milli>(std::chrono::steady_clock::now()-start).count();double cpu=(b.tv_sec-a.tv_sec)*1000.+(b.tv_nsec-a.tv_nsec)/1e6;printf("frames=120 elapsed_ms=%.3f pacing_cpu_ms=%.3f cpu_per_frame_ms=%.4f\n",wall,cpu,cpu/120);return wall<1950||wall>2500;}
