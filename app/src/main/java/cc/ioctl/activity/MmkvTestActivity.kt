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

package cc.ioctl.activity

import android.os.Bundle
import android.widget.TextView
import com.tencent.mmkv.MMKV
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.AppCompatTransferActivity
import nil.nadph.qnotified.databinding.ActivityDatabaseTestBinding
import java.text.DateFormat
import java.util.*

class MmkvTestActivity : AppCompatTransferActivity() {

    private lateinit var binding: ActivityDatabaseTestBinding
    private lateinit var mmkv: MMKV

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_DayNight)
        super.onCreate(savedInstanceState)
        title = "MMKV Demo"
        mmkv = MMKV.mmkvWithID("test", MMKV.MULTI_PROCESS_MODE)!!
        binding = ActivityDatabaseTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.insert.setOnClickListener {
            mmkv.putString(binding.keyEditText.text.toString(), binding.valueEditText.text.toString())
        }
        binding.update.setOnClickListener {
            mmkv.putString(binding.keyEditText.text.toString(), binding.valueEditText.text.toString())

        }
        binding.query.setOnClickListener {
            binding.result += mmkv.getString(binding.keyEditText.text.toString(), null) ?: "null"
        }
        binding.delete.setOnClickListener {
            mmkv.removeValueForKey(binding.keyEditText.text.toString())
        }
        binding.queryAll.setOnClickListener {
            val result = mmkv.allKeys()
            binding.result += (result ?: arrayOf()).joinToString { it.toString() }
        }
        binding.observe.setOnClickListener {
            binding.result += "Not implemented"
        }
    }
}

private infix operator fun TextView.plusAssign(string: String) {
    this.text = '[' + DateFormat.getTimeInstance()
        .format(Date()) + "] " + string + '\n' + this.text.toString()
}
