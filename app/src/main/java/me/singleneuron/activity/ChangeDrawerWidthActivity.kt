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
package me.singleneuron.activity

import android.os.Bundle
import me.singleneuron.hook.ChangeDrawerWidth
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.AppCompatTransferActivity
import nil.nadph.qnotified.databinding.ActivityChangeDrawerWidthBinding
import nil.nadph.qnotified.ui.___WindowIsTranslucent

class ChangeDrawerWidthActivity : AppCompatTransferActivity(), ___WindowIsTranslucent {

    private lateinit var binding: ActivityChangeDrawerWidthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MaterialDialogActivity)
        super.onCreate(savedInstanceState)
        binding = ActivityChangeDrawerWidthBinding.inflate(layoutInflater)
        val slider = binding.slider
        slider.valueFrom = 0f
        slider.valueTo = ChangeDrawerWidth.getMaxWidth(this).toInt().toFloat()
        slider.stepSize = 1f
        binding.textView6.text = ChangeDrawerWidth.width.toString()
        slider.value = ChangeDrawerWidth.width.toFloat()
        slider.addOnChangeListener { _, value, _ ->
            binding.textView6.text = value.toInt().toString()
        }
        binding.button2.setOnClickListener {
            ChangeDrawerWidth.width = slider.value.toInt()
            finish()
        }
        setContentView(binding.root)
    }

}
