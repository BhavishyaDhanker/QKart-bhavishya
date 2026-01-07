package com.example.qkart_bhavishya

data class OrderModel(
    val orderId: String = "",
    val username: String = "",
    val rollNo: String = "",
    val items: List<CartItem> = listOf(),
    val totalAmount: Double = 0.0,
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis(),
    val pickupTime: String = ""
)

data class CartItem(
    val itemId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)