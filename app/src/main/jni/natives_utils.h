//
// Created by kinit on 2020/8/10.
//

#ifndef NATIVES_NATIVES_UTILS_H
#define NATIVES_NATIVES_UTILS_H

#include <stdint.h>

#define EXPORT extern "C"
//#define null nullptr
typedef unsigned char uchar;

//Android is little endian, use pointer
inline uint32_t readLe32(uint8_t *buf, int index) {
    return *((uint32_t *) (buf + index));
}

inline uint32_t readLe16(uint8_t *buf, int off) {
    return *((uint16_t *) (buf + off));
}

inline int min(int a, int b) {
    return a > b ? b : a;
}

inline int max(int a, int b) {
    return a < b ? b : a;
}

#endif //NATIVES_NATIVES_UTILS_H
