package com.example.qkart_bhavishya

data class MenuItem(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val isAvailable: Boolean = true
)