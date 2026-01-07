package com.example.qkart_bhavishya

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var adapter: StudentOrderAdapter
    private val database by lazy { AppRoomDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val rv = findViewById<RecyclerView>(R.id.rvOrderHistory)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = StudentOrderAdapter(emptyList())
        rv.adapter = adapter

        findViewById<ImageView>(R.id.btnBackHistory).setOnClickListener { finish() }

        observeOrders()
        syncFirestoreToRoom()
    }

    private fun observeOrders() {
        lifecycleScope.launch {
            // Inside observeOrders() in OrderHistoryActivity.kt
            database.orderDao().getAllOrders().collect { entityList ->
                val modelList = entityList.map { entity ->
                    OrderModel(
                        orderId = entity.orderId,
                        rollNo = entity.rollNo,
                        status = entity.status,
                        timestamp = entity.timestamp,
                        totalAmount = entity.totalAmount,
                        // CHANGE: We pass the combined string into 'itemName'
                        // and ensure quantity doesn't cause a "0x" prefix
                        items = listOf(CartItem(
                            name = entity.itemName,
                            quantity = -1 // Use a sentinel value or handle in Adapter(I still don't know
                                            // why this thing prevents the order name from starting with 0x
                        ))
                    )
                }
                adapter.updateList(modelList)
            }
        }
    }

    private fun syncFirestoreToRoom() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val myRollNo = prefs.getString("rollNo", "") ?: ""

        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        firestore.collection("orders")
            .whereEqualTo("rollNo", myRollNo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val entities = querySnapshot.documents.mapNotNull { doc ->
                    // 1. Convert the document to OrderModel first to get the items list
                    val model = doc.toObject(OrderModel::class.java)

                    // 2. Join all item names into a single string (e.g., "2x Burger, 1x Coke")
                    val combinedNames = model?.items?.joinToString { "${it.quantity}x ${it.name}" } ?: "Unknown Order"

                    OrderEntity(
                        orderId = doc.id,
                        itemName = combinedNames, // This fixes the "0x Order" issue
                        rollNo = doc.getString("rollNo") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        status = doc.getString("status") ?: "Pending",
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0
                    )
                }

                lifecycleScope.launch {
                    database.orderDao().insertOrders(entities)
                }
            }
    }
}