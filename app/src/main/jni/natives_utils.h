/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */


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
