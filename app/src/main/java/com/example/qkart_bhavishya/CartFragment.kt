package com.example.qkart_bhavishya

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Locale

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: AppCompatButton
    private lateinit var tvEmptyCartMsg: LinearLayout
    private lateinit var cartAdapter: CartAdapter

    private var selectedPickupTime: String = "ASAP"
    private val helper = FirestoreHelper()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCart = view.findViewById(R.id.rvCartItems)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder)
        tvEmptyCartMsg = view.findViewById(R.id.tvEmptyCartMsg)

        setupRecyclerView()
        updateUI()

        btnPlaceOrder.setOnClickListener {
            val items = CartManager.getCartList()
            if (items.isNotEmpty()) {
                placeFinalOrder(items)
            } else {
                Toast.makeText(requireContext(), "Add items to your cart first!", Toast.LENGTH_SHORT).show()
            }
        }

        // Time Picker
        val tvSelectedTime = view.findViewById<TextView>(R.id.tvSelectedTime)
        val btnSelectTime = view.findViewById<TextView>(R.id.btnSelectTime)

        btnSelectTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                val sdf = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
                selectedPickupTime = sdf.format(cal.time)
                tvSelectedTime.text = selectedPickupTime
            }

            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun setupRecyclerView() {
        rvCart.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(CartManager.getCartList()) {
            updateUI()
            (activity as? MainActivity)?.updateCartBadge()
        }
        rvCart.adapter = cartAdapter
    }

    private fun updateUI() {
        val total = CartManager.getTotalPrice()
        tvTotalAmount.text = "₹ $total"

        if (CartManager.getCartList().isEmpty()) {
            tvEmptyCartMsg.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
            btnPlaceOrder.isEnabled = false
            btnPlaceOrder.alpha = 0.5f
        } else {
            tvEmptyCartMsg.visibility = View.GONE
            rvCart.visibility = View.VISIBLE
            btnPlaceOrder.isEnabled = true
            btnPlaceOrder.alpha = 1.0f
        }
        cartAdapter.updateData(CartManager.getCartList())
    }

    private fun placeFinalOrder(items: List<CartItem>) {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val rollNo = sharedPref.getString("rollNo", "Unknown") ?: "Unknown"
        val name = sharedPref.getString("userName", "Student") ?: "Student"

        val newOrder = OrderModel(
            username = name,
            rollNo = rollNo,
            items = items,
            totalAmount = CartManager.getTotalPrice(),
            status = "Pending",
            pickupTime = selectedPickupTime,
            timestamp = System.currentTimeMillis()
        )

        helper.placeOrder(newOrder) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Order Sent!", Toast.LENGTH_LONG).show()
                CartManager.clearCart()
                (activity as? MainActivity)?.updateCartBadge()

                // Go back to Home
                (activity as? MainActivity)?.bottomNav?.selectedItemId = R.id.nav_home
            } else {
                Toast.makeText(requireContext(), "Failed to send order.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}