/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.step;

import nil.nadph.qnotified.util.DexKit;

public class DexDeobfStep implements Step {
    private final int id;

    public DexDeobfStep(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean step() {
        return DexKit.prepareFor(id);
    }

    @Override
    public boolean isDone() {
        return DexKit.checkFor(id);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getDescription() {
        if (id / 10000 == 0) {
            return "定位被混淆类: " + DexKit.c(id);
        } else {
            return "定位被混淆方法: " + DexKit.c(id);
        }
    }

    @Override
    public int compareTo(Step o) {
        return this.getPriority() - o.getPriority();
    }
}
