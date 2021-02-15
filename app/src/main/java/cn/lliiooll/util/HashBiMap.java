/*
 * QNotified - An Xposed module for QQ/TIM
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

package cn.lliiooll.util;

import java.util.HashMap;

public class HashBiMap<K, V> extends HashMap<K, V> implements BiMap<K, V> {

    /**
     * @return 一个反向map
     */
    @Override
    public BiMap<V, K> getReverseMap() {
        BiMap<V, K> reverse = new HashBiMap<>();
        for (K key : this.keySet()) {
            V value = this.get(key);
            reverse.put(value, key);
        }
        return reverse;
    }
}
