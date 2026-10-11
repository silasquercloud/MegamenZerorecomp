#pragma once
#include <cstdio>
#include <cstdint>
#include <atomic>
inline void mmz_boot_event(const char* step, const char* detail) {
    std::fprintf(stderr, "%s — %s\n", step, detail);
    std::fflush(stderr);
}
bool mmz_presented();
inline std::atomic<bool> mmz_frame_logged{false};
inline void mmz_boot_reset(){mmz_frame_logged=false;}
inline void mmz_first_frame(std::uint64_t frame) {
    if (frame && mmz_presented() && !mmz_frame_logged.exchange(true)) {
        std::fprintf(stderr, "MMZ_BOOT_09 — Primeiro framebuffer GBA apresentado; frame=%llu\n", (unsigned long long)frame);
        if (FILE* f = std::fopen("first-frame.txt", "w")) { std::fprintf(f,"%llu\n",(unsigned long long)frame);std::fclose(f); }
    }
    // MMZ_BOOT_10 is intentionally reserved for verified interactive gameplay.
    // A rendered title/intro alone must never be reported as gameplay.
}
