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
