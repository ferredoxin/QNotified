//
// Created by kinit on 5/6/21.
//

#include <android/log.h>
#include "NativeMainHook.h"
#include "NativeHookEntry.h"

static bool nt_h_inited = false;

bool NativeHook_initOnce() {
    if (nt_h_inited) {
        return true;
    }
    NativeHookHandle *h = GetOrInitNativeHookHandle();
    if (h == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "QNdump",
                            "GetOrInitNativeHookHandle() is null\n");
        return false;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "QNdump",
                        "hookProc=%p\n", h->hookFunction);
    // TODO: hook open(/proc/self/maps)
    // TODO: hook stat
    nt_h_inited = true;
    return true;
}

bool NativeHook_isInited() {
    return nt_h_inited;
}

void handleLoadLibrary(const char *name, void *handle) {
    if (!nt_h_inited) {
        return;
    }
    // TODO: implement this func
}
