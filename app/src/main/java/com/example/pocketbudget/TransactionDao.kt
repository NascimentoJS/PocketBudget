package com.example.pocketbudget

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}

