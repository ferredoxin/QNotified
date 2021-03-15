/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package nil.nadph.qnotified.base.internal

import nil.nadph.qnotified.base.AbsFunctionItem
import nil.nadph.qnotified.base.AbsHookTask
import nil.nadph.qnotified.base.ErrorStatus
import nil.nadph.qnotified.mvc.base.AbsConfigSection
import nil.nadph.qnotified.step.Step

abstract class Abs2SingleHookFuncItemProxyImpl : IFunctionItemInterface {

    //compile-time constant properties
    abstract val functionName: String
    abstract val description: CharSequence?
    abstract val keyName: String
    abstract val targetProcess: Int
    abstract val extraSearchKeywords: Array<String>?
    abstract val requiredHooks: Array<AbsHookTask>
    abstract val compatibleVersions: String?
    abstract val isRuntimeHookSupported: Boolean
    abstract val isTodo: Boolean
    abstract val hasEnableState: Boolean
    abstract val uniqueIdentifier: String
    abstract val isShowMainSwitch: Boolean
    abstract val hasConfigSection: Boolean
    abstract val preparations: Array<Step>

    //volatile properties
    abstract var isCompatible: Boolean
    abstract var summaryText: CharSequence?
    abstract var functionStatus: ErrorStatus

    abstract var isEnabled: Boolean

    abstract fun execute(): ErrorStatus
    abstract fun isExecuted(): Boolean
    abstract fun createConfigSection(): AbsConfigSection?

    @Throws(Throwable::class)
    abstract fun initOnce(): Boolean;

    private val adapter: Abs2SingleHookFuncProxy2ItemAdapter by lazy {
        return@lazy Abs2SingleHookFuncProxy2ItemAdapter(this)
    }

    override fun asFunctionItem(): AbsFunctionItem = adapter;
}

private class Abs2SingleHookFuncProxy2ItemAdapter(val h: Abs2SingleHookFuncItemProxyImpl) :
    AbsFunctionItem, AbsHookTask {
    override fun getName(): String = h.functionName
    override fun getDescription(): CharSequence? = h.description
    override fun getExtraSearchKeywords(): Array<String>? = h.extraSearchKeywords
    override fun isCompatible(): Boolean = h.isCompatible
    override fun getCompatibleVersions(): String? = h.compatibleVersions
    override fun getRequiredHooks(): Array<AbsHookTask> = h.requiredHooks
    override fun isRuntimeHookSupported(): Boolean = h.isRuntimeHookSupported
    override fun isTodo(): Boolean = h.isTodo
    override fun isNeedEarlyInit(): Boolean = false
    override fun hasEnableState(): Boolean = h.hasEnableState
    override fun setEnabled(enabled: Boolean) {
        h.isEnabled = enabled
    }

    override fun isEnabled(): Boolean = h.isEnabled
    override fun getUniqueIdentifier(): String = h.uniqueIdentifier
    override fun isShowMainSwitch(): Boolean = h.isShowMainSwitch
    override fun getSummaryText(): CharSequence? = h.summaryText
    override fun hasConfigSection(): Boolean = h.hasConfigSection
    override fun createConfigSection(): AbsConfigSection? = h.createConfigSection()
    override fun getFunctionStatus(): ErrorStatus = h.functionStatus

    override fun getTaskStatus(): ErrorStatus {
        return h.functionStatus
    }

    override fun execute(): ErrorStatus = h.execute()
    override fun isExecuted(): Boolean = h.isExecuted()
    override fun getPreparations(): Array<Step> = h.preparations
    override fun getTargetProcess(): Int = h.targetProcess
    override fun toString() = "$h(proxy)"

}
