cmake_minimum_required(VERSION 3.10)
project(natives)

set(CMAKE_CXX_STANDARD 17)

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        ANDROID_LIBS
        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror=format-invalid-specifier -Werror=return-type -Wno-invalid-offsetof")
set(CMAKE_LINK_LIBRARY_FLAG " -Wl,-e,__libnatives_main ${CMAKE_LINK_LIBRARY_FLAG}")
#SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -nostdlib")
#-DANDROID_STL=gnustl_shared

add_subdirectory(../../../../libs/mmkv/Android mmkv)
add_subdirectory(../../../../libs/xhook xhook)

include_directories(../../../../libs/xhook/xHook/libxhook/jni)

add_library(natives SHARED libnatives/Natives.cpp libnatives/version.c
        libnatives/NativeHookEntry.cc libnatives/NativeMainHook.cc)

set_target_properties(natives PROPERTIES
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
        )
target_compile_definitions(natives PRIVATE QNOTIFIED_LIBNATIVES_VERSION=\"${QNOTIFIED_VERSION}\")
#add_library(natives STATIC Natives.cpp)
target_link_libraries(natives mmkv xhook c dl ${ANDROID_LIBS})
