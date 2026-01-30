package com.example.qkart_bhavishya

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var adapter: StudentOrderAdapter
    // Lazy init database using requireContext()
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvOrderHistory)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = StudentOrderAdapter(emptyList())
        rv.adapter = adapter

        observeOrders()
        syncFirestoreToRoom()
    }

    private fun observeOrders() {
        // Use viewLifecycleOwner.lifecycleScope for Fragments
        viewLifecycleOwner.lifecycleScope.launch {
            database.orderDao().getAllOrders().collect { entityList ->
                val modelList = entityList.map { entity ->
                    OrderModel(
                        orderId = entity.orderId,
                        rollNo = entity.rollNo,
                        status = entity.status,
                        timestamp = entity.timestamp,
                        totalAmount = entity.totalAmount,
                        items = listOf(CartItem(name = entity.itemName, quantity = -1))
                    )
                }
                adapter.updateList(modelList)
            }
        }
    }

    private fun syncFirestoreToRoom() {
        val prefs = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val myRollNo = prefs.getString("rollNo", "") ?: ""

        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        firestore.collection("orders")
            .whereEqualTo("rollNo", myRollNo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val entities = querySnapshot.documents.mapNotNull { doc ->
                    val model = doc.toObject(OrderModel::class.java)
                    val combinedNames = model?.items?.joinToString { "${it.quantity}x ${it.name}" } ?: "Unknown"

                    OrderEntity(
                        orderId = doc.id,
                        itemName = combinedNames,
                        rollNo = doc.getString("rollNo") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        status = doc.getString("status") ?: "Pending",
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0
                    )
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    database.orderDao().insertOrders(entities)
                }
            }
    }
}