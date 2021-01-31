package me.singleneuron.qn_kernel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.singleneuron.qn_kernel.database.dao.TestDAO
import me.singleneuron.qn_kernel.database.entity.Test

@Database(entities = [Test::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testDao(): TestDAO
}
