//
// Created by cinit on 2021-05-12.
//

#include "stdio.h"
#include "unistd.h"

#if defined(__aarch64__) || defined(__x86_64__)
const char so_interp[] __attribute__((used, section(".interp")))
    = "/system/bin/linker64";
#elif defined(__i386__) || defined(__arm__)
const char so_interp[] __attribute__((used, section(".interp")))
    = "/system/bin/linker";
#else
#error Unknown Arch
#endif

#ifndef QNOTIFIED_LIBNATIVES_VERSION
#error Please define macro QNOTIFIED_LIBNATIVES_VERSION in CMakeList
#endif

__attribute__((used, noreturn, section(".entry_init")))
void __libnatives_main(void) {
    printf("QNotified libnatives.so version " QNOTIFIED_LIBNATIVES_VERSION ".\n"
           "Copyright (C) 2019-2021 dmca@ioctl.cc\n"
           "This software is distributed in the hope that it will be useful,\n"
           "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
           "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n");
    _exit(0);
}
