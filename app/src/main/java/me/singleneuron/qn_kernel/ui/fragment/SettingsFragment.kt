/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

@file:Suppress("DEPRECATION")

package me.singleneuron.qn_kernel.ui.fragment

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import me.singleneuron.qn_kernel.ui.activity.NewSettingsActivity
import me.singleneuron.qn_kernel.ui.qq_item.LargeSubtitle
import me.singleneuron.qn_kernel.ui.qq_item.ListItemButton
import me.singleneuron.qn_kernel.ui.qq_item.ListItemSwitch
import org.ferredoxin.ferredoxinui.common.base.*

class SettingsFragment : Fragment(), LifecycleOwner {

    private lateinit var uiScreen: UiScreen

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        val ll = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        addViewInUiGroup(uiScreen, ll)

        return ll
    }

    fun setUiScreen(uiScreen: UiScreen): SettingsFragment {
        this.uiScreen = uiScreen
        return this
    }

    private fun addViewInUiGroup(uiGroup: UiGroup, viewGroup: ViewGroup) {
        //Utils.logd("Adding: " + uiGroup.name + " = " + uiGroup.contains.toString())
        for (uiDescription in uiGroup.contains.values) {
            //Utils.logd("Adding: $uiDescription")
            when {
                uiDescription is UiCategory && uiDescription.contains.isNotEmpty() -> {
                    viewGroup.addView(LargeSubtitle(activity).apply {
                        title = uiDescription.name
                    })
                    addViewInUiGroup(uiDescription, viewGroup)
                }
                uiDescription is UiScreen -> {
                    viewGroup.addView(ListItemButton(activity).apply {
                        title = uiDescription.nameProvider.getValue(activity)
                        summary = uiDescription.summaryProvider.getValue(activity)
                        setOnClickListener {
                            (activity as NewSettingsActivity).changeFragment(uiDescription)
                        }
                    })
                }
                uiDescription is UiPreference -> {
                    when (uiDescription) {
                        is UiSwitchPreference -> {
                            val switch = ListItemSwitch(activity).apply {
                                title = uiDescription.titleProvider.getValue(activity)
                                summary = uiDescription.summaryProvider.getValue(activity)
                                isChecked = uiDescription.value.value ?: false
                                isEnabled = uiDescription.valid
                                onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, value ->
                                    uiDescription.value.value = value
                                }
                            }
                            observeStateFlow(uiDescription.value) {
                                if (it == null) return@observeStateFlow
                                switch.isChecked = it
                            }
                            viewGroup.addView(switch)
                        }
                        is UiChangeablePreference<*> -> {
                            val view = ListItemButton(activity).apply {
                                title = uiDescription.titleProvider.getValue(activity)
                                summary = uiDescription.summaryProvider.getValue(activity)
                                setOnClickListener(getOnClickListener(uiDescription.onClickListener, uiDescription.title))
                            }
                            viewGroup.addView(view)
                            observeStateFlow(uiDescription.value) {
                                view.value = it.toString()
                            }
                        }
                        else -> {
                            viewGroup.addView(ListItemButton(activity).apply {
                                title = uiDescription.titleProvider.getValue(activity)
                                summary = uiDescription.summaryProvider.getValue(activity)
                                setOnClickListener(getOnClickListener(uiDescription.onClickListener, uiDescription.title))
                            })
                        }
                    }
                }
            }
        }
    }

    private fun getOnClickListener(
        listener: (Activity) -> Boolean,
        title: String
    ): View.OnClickListener {
        return when (listener) {
            is ClickToNewSetting -> {
                View.OnClickListener {
                    (activity!! as NewSettingsActivity).changeFragment(listener.uiScreen)
                }
            }
            is ClickToNewPages -> {
                View.OnClickListener {
                    (activity!! as NewSettingsActivity).changeFragment(listener.viewMap, title)
                }

            }
            else -> {
                View.OnClickListener {
                    listener.invoke(activity!!)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}
