cmake_minimum_required(VERSION 3.15)
project(natives)

set(CMAKE_CXX_STANDARD 14)

set(TOOLS_PATH /opt/android-sdk/ndk/android-ndk-r10c)

SET(CMAKE_C_COMPILER ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc)
SET(CMAKE_CXX_COMPILER ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-g++)
set(CMAKE_ASM_COMPILER ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc)
set(CMAKE_AR ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-ar)
set(CMAKE_OBJCOPY ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-objcopy)
set(CMAKE_OBJDUMP ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-objdump)
set(SIZE ${TOOLS_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-size)

SET(CMAKE_ASM_FLAGS "${CMAKE_ASM_FLAGS} -fPIC -fpic")

include_directories("/usr/include")
include_directories("/usr/lib/jvm/java-11-openjdk-amd64/include")
include_directories("/usr/lib/jvm/java-11-openjdk-amd64/include/linux")

include_directories("${TOOLS_PATH}/platforms/android-14/arch-arm/usr/include")
include_directories("${TOOLS_PATH}/sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi/include")
include_directories("${TOOLS_PATH}/sources/cxx-stl/gnu-libstdc++/4.9/include")

SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-invalid-offsetof -Wno-pmf-conversions -nostdlib")
#-DANDROID_STL=gnustl_shared
link_directories(${TOOLS_PATH}/platforms/android-14/arch-arm/usr/lib)

add_library(natives SHARED Natives.cpp)
target_link_libraries(natives c dl)
