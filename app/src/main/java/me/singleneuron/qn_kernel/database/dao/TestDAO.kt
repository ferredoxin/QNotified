package me.singleneuron.qn_kernel.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.singleneuron.qn_kernel.database.entity.Test

@Dao
interface TestDAO {

    @Query("SELECT * FROM test")
    fun findAll(): Array<Test>

    @Query("SELECT * FROM test WHERE mainKey LIKE :key")
    fun findTest(key: String): Test

    @Query("SELECT * FROM test WHERE mainKey LIKE :key")
    fun findLiveDataTest(key: String): LiveData<Test>

    @Update
    fun updateTest(vararg tests: Test)

    @Delete
    fun deleteTest(vararg tests: Test)

    @Insert
    fun insertTest(test: Test)

}
