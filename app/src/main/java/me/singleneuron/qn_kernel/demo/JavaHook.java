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

package me.singleneuron.qn_kernel.demo;

import androidx.annotation.NonNull;

import me.singleneuron.qn_kernel.annotation.HookLoadable;
import me.singleneuron.qn_kernel.annotation.ModuleInfo;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.Step;

@HookLoadable
//此注解将在编译时将整个类处理为AnotherJavaHook的形式
@ModuleInfo(name = "A",description = "A's description")
public class JavaHook extends CommonDelayableHook {
    protected JavaHook(@NonNull String keyName, @NonNull Step... preconditions) {
        super(keyName, preconditions);
    }

    @Override
    protected boolean initOnce() {
        return false;
    }
}
