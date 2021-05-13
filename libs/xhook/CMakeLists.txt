cmake_minimum_required(VERSION 3.8.0)

set(can_use_assembler TRUE)
enable_language(ASM)

project(xhook)

add_library(xhook STATIC
        xHook/libxhook/jni/xh_core.c
        xHook/libxhook/jni/xh_elf.c
        xHook/libxhook/jni/xh_log.c
        xHook/libxhook/jni/xh_util.c
        xHook/libxhook/jni/xh_version.c
        xHook/libxhook/jni/xhook.c
        )

set_target_properties(xhook PROPERTIES
        CXX_STANDARD 17
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
        )

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        ANDROID_LIBS
        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

target_link_libraries(xhook ${ANDROID_LIBS})
