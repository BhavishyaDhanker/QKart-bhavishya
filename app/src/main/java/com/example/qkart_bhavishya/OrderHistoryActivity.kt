package com.example.qkart_bhavishya

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var adapter: StudentOrderAdapter
    private val helper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val rv = findViewById<RecyclerView>(R.id.rvOrderHistory)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = StudentOrderAdapter(emptyList())
        rv.adapter = adapter

        findViewById<ImageView>(R.id.btnBackHistory).setOnClickListener { finish() }

        loadMyOrders()
    }

    private fun loadMyOrders() {
        // Get the roll number from SharedPreferences
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val myRollNo = prefs.getString("rollNo", "") ?: ""

        // Filter Firestore by rollNo
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("rollNo", myRollNo)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) return@addSnapshotListener

                val orderList = value.toObjects(OrderModel::class.java)
                // Sort by timestamp so newest are on top
                adapter.updateList(orderList.sortedByDescending { it.timestamp })
            }
    }
}