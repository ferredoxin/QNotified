package me.singleneuron.qn_kernel.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Test(
    @PrimaryKey val mainKey: String,
    val value: String
)
