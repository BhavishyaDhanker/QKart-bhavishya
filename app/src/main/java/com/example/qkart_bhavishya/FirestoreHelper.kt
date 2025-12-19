package com.example.qkart_bhavishya

import com.example.wocq_kart.User
import com.example.qkart_bhavishya.MenuItem
import com.example.qkart_bhavishya.OrderModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    fun getMenu(onSuccess: (List<MenuItem>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("menu")
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->
                val menuList = result.toObjects<MenuItem>()
                onSuccess(menuList)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun placeOrder(order: OrderModel, onComplete: (Boolean) -> Unit) {
        db.collection("orders")
            .add(order)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun observeOrders(onUpdate: (List<OrderModel>) -> Unit) {
        db.collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val orders = snapshot.toObjects<OrderModel>()
                onUpdate(orders)
            }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, onComplete: (Boolean) -> Unit) {
        db.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun addMenuItem(item: MenuItem, onComplete: (Boolean) -> Unit) {
        val newDocRef = db.collection("menu").document()
        val itemWithId = item.copy(id = newDocRef.id)
        newDocRef.set(itemWithId)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun updateMenuAvailability(itemId: String, isAvailable: Boolean, onComplete: (Boolean) -> Unit) {
        db.collection("menu").document(itemId)
            .update("isAvailable", isAvailable)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

}