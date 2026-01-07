package com.example.qkart_bhavishya

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders_table")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val itemName: String = "",
    val rollNo: String = "",
    val timestamp: Long = 0L,
    val status: String = "Pending",
    val totalAmount: Double = 0.0
)