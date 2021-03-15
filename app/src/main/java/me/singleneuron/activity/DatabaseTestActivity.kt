package me.singleneuron.activity

import android.os.Bundle
import android.widget.TextView
import me.singleneuron.qn_kernel.database.DatabaseContainer
import me.singleneuron.qn_kernel.database.entity.Test
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.AppCompatTransferActivity
import nil.nadph.qnotified.databinding.ActivityDatabaseTestBinding
import java.text.DateFormat
import java.util.*

class DatabaseTestActivity : AppCompatTransferActivity() {

    private lateinit var binding: ActivityDatabaseTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_DayNight)
        super.onCreate(savedInstanceState)
        title = "反馈"
        binding = ActivityDatabaseTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.insert.setOnClickListener {
            val testData =
                Test(binding.keyEditText.text.toString(), binding.valueEditText.text.toString())
            DatabaseContainer.db.testDao().insertTest(testData)
        }
        binding.update.setOnClickListener {
            val testData =
                Test(binding.keyEditText.text.toString(), binding.valueEditText.text.toString())
            DatabaseContainer.db.testDao().updateTest(testData)
        }
        binding.query.setOnClickListener {
            val result =
                DatabaseContainer.db.testDao().findTest(binding.keyEditText.text.toString())
            binding.result += result.toString()
        }
        binding.delete.setOnClickListener {
            val testData =
                Test(binding.keyEditText.text.toString(), binding.valueEditText.text.toString())
            DatabaseContainer.db.testDao().deleteTest(testData)
        }
        binding.queryAll.setOnClickListener {
            val result = DatabaseContainer.db.testDao().findAll()
            binding.result += result.joinToString { it.toString() }
        }
        binding.observe.setOnClickListener {
            DatabaseContainer.db.testDao().findLiveDataTest(binding.keyEditText.text.toString())
                .observe(this, {
                    binding.result += it.toString()
                })
        }
    }

}

private infix operator fun TextView.plusAssign(string: String) {
    this.text = '[' + DateFormat.getTimeInstance()
        .format(Date()) + "] " + string + '\n' + this.text.toString()
}
