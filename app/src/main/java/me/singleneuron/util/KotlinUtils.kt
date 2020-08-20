package me.singleneuron.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit

class KotlinUtils {

    companion object {
        fun ViewGroup.addViewConditionally(view: View, condition: ()->Boolean) {
            if (condition()) {
                this.addView(view)
            }
        }

        fun ViewGroup.addViewConditionally(context: Context, title: String, desc:String, hook:BaseDelayableConditionalHookAdapter) {
            addViewConditionally(newListItemHookSwitchInit(context,title,desc,hook),hook.condition)
        }
    }

}