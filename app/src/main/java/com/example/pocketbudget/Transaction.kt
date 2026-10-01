package com.example.pocketbudget

data class Transaction(
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val firestoreId: String = ""
)