#include "mobile_platform.h"
#include <string>
#include <vector>
#include <algorithm>
#include <cstdio>
#include <exception>
#include <SDL.h>
#include <unistd.h>
#include <signal.h>
#include <fcntl.h>

// The reviewed upstream entry retains the game's native dispatch and hooks.
int mmz_game_main(int argc, char** argv);

static int crash_fd = -1;
static struct sigaction previous_handlers[NSIG];
static void crash_handler(int sig) {
    const char message[] = "MMZ_BOOT_FAIL native signal; consult Android tombstone/logcat\n";
    if (crash_fd >= 0) write(crash_fd, message, sizeof(message)-1);
    char code[4] = {char('0'+(sig/10)%10),char('0'+sig%10),'\n',0};
    if (crash_fd >= 0) write(crash_fd, code, 3);
    sigaction(sig, &previous_handlers[sig], nullptr); // retain Android debuggerd/tombstone handler
    raise(sig);
}
static int android_game_main(int argc, char** argv) {
    std::vector<std::string> args(argv, argv + argc);
    gbarecomp::MobileProcessOptions options;
    options.game_config = "variants/megaman_zero/game.toml";
    options.program_name = "./MegaManZeroNative";
    const char* storage = SDL_AndroidGetInternalStoragePath();
    if (!storage || chdir(storage) != 0) {
        std::fprintf(stderr, "MMZ_BOOT_FAIL storage unavailable; SDL error=%s\n", SDL_GetError());
        return 1;
    }
    gbarecomp::mobile_prepare_process(args, options);
    // mobile_prepare_process's flag belongs to game_launcher_preboot, which
    // this Android entry does not call. runtime parse_cli rejects that flag.
    args.erase(std::remove(args.begin(), args.end(), "--no-launcher"), args.end());
    crash_fd = open("boot-state.txt", O_WRONLY | O_CREAT | O_TRUNC, 0600);
    const char pending[] = "MMZ_BOOT_PENDING native entry reached\n";
    if (crash_fd >= 0) write(crash_fd, pending, sizeof(pending)-1);
    struct sigaction action{};
    action.sa_handler = crash_handler; sigemptyset(&action.sa_mask);
    for (int sig : {SIGSEGV, SIGABRT, SIGBUS, SIGILL, SIGFPE}) sigaction(sig, &action, &previous_handlers[sig]);
    std::fprintf(stderr, "MMZ_BOOT_NATIVE thread=%ld cwd/config=%s\n", (long)gettid(), options.game_config);
    std::vector<char*> pointers;
    for (auto& arg : args) pointers.push_back(arg.data());
    int result = 1;
    try { result = mmz_game_main(static_cast<int>(pointers.size()), pointers.data()); }
    catch (const std::exception& e) { std::fprintf(stderr, "MMZ_BOOT_FAIL C++ exception: %s\n", e.what()); }
    catch (...) { std::fprintf(stderr, "MMZ_BOOT_FAIL unknown C++ exception\n"); }
    std::fprintf(stderr, "MMZ_BOOT_RETURN runtime exit code=%d\n", result);
    if (crash_fd >= 0) { char line[80]; int n = std::snprintf(line, sizeof(line), "MMZ_BOOT_RETURN code=%d\n", result); write(crash_fd, line, n); close(crash_fd); crash_fd=-1; }
    return result;
}

extern "C" __attribute__((visibility("default"))) int SDL_main(int argc, char** argv) {
    return gbarecomp::mobile_run_with_stack(android_game_main, argc, argv);
}
