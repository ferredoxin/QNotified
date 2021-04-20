cmake_minimum_required(VERSION 3.10)
project(natives)

set(CMAKE_CXX_STANDARD 14)

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        ANDROID_LIBS
        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror=return-type -Wno-invalid-offsetof")
#SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -nostdlib")
#-DANDROID_STL=gnustl_shared

add_library(natives SHARED Natives.cpp)
#add_library(natives STATIC Natives.cpp)
target_link_libraries(natives c dl ${ANDROID_LIBS})
