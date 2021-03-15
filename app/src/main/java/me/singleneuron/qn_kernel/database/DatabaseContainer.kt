package me.singleneuron.qn_kernel.database

import android.app.AndroidAppHelper
import androidx.room.Room

object DatabaseContainer {

    public val db: AppDatabase by lazy {
        Room.databaseBuilder(
            AndroidAppHelper.currentApplication(),
            AppDatabase::class.java,
            "QNotifiedDatabase"
        )
            .allowMainThreadQueries()
            .enableMultiInstanceInvalidation()
            .build()
    }

}
