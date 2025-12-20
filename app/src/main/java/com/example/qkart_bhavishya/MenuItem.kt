package com.example.qkart_bhavishya

import com.google.firebase.firestore.PropertyName

data class MenuItem(
    var id: String? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val category: String = "",
    // I used these annotations as in firestore it was just not giving the name
    // isAvailable to this feild and was writing it off as available which was
    // the reason for why the firestore was not able to properly work
    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true
)