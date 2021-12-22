package me.singleneuron.qn_kernel.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import me.singleneuron.qn_kernel.tlb.UiTable
import nil.nadph.qnotified.R
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.TitleAble
import org.ferredoxin.ferredoxinui.qnotified_style.activity.MaiTungTMStyleActivity
import org.ferredoxin.ferredoxinui.qnotified_style.fragment.MaiTungTMSettingFragment

class SettingActivity<T> : MaiTungTMStyleActivity<T>() where T : Fragment, T : TitleAble {

    override val fragment: T = MaiTungTMSettingFragment().setUiScreen(UiTable.second) as T

    private val mLoader by lazy { this::class.java.classLoader }

    override fun getClassLoader() = mLoader!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MaiTungTMUI)
        runCatching {
            AppCompatDelegate.setDefaultNightMode(
                if (ResUtils.isInNightMode()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }.onFailure {
            Utils.log(it)
        }
        super.onCreate(savedInstanceState)
    }
}
