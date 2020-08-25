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