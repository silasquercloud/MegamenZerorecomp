#include "mobile_platform.h"
#include <string>
#include <vector>

// The reviewed upstream entry retains the game's native dispatch and hooks.
int mmz_game_main(int argc, char** argv);

static int android_game_main(int argc, char** argv) {
    std::vector<std::string> args(argv, argv + argc);
    gbarecomp::MobileProcessOptions options;
    options.game_config = "variants/megaman_zero/game.toml";
    options.program_name = "./MegaManZeroNative";
    gbarecomp::mobile_prepare_process(args, options);
    std::vector<char*> pointers;
    for (auto& arg : args) pointers.push_back(arg.data());
    return mmz_game_main(static_cast<int>(pointers.size()), pointers.data());
}

extern "C" __attribute__((visibility("default"))) int SDL_main(int argc, char** argv) {
    return gbarecomp::mobile_run_with_stack(android_game_main, argc, argv);
}
