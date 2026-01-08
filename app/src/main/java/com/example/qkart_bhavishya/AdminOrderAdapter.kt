package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class AdminOrderAdapter(
    private var orderList: List<OrderModel>,
    private val helper: FirestoreHelper
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.txtUserName)
        val tvItems: TextView = view.findViewById(R.id.txtOrderItems)
        val tvTotal: TextView = view.findViewById(R.id.txtTotalAmount)
        val tvStatus: TextView = view.findViewById(R.id.txtStatus)
        val btnNext: Button = view.findViewById(R.id.btnNextStatus)

        val tvPickup: TextView = view.findViewById(R.id.tvOrderPickupTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_admin, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.tvUser.text = "${order.username} (${order.rollNo})"
        holder.tvTotal.text = "₹ ${order.totalAmount}"
        holder.tvStatus.text = order.status

        // Binding the Pickup Time
        val timeText = if (order.pickupTime.isNotEmpty()) order.pickupTime else "ASAP"
        holder.tvPickup.text = "Pickup: $timeText"


        // Create the string of items from the list
        val itemSummary = order.items.joinToString { "${it.quantity}x ${it.name}" }
        holder.tvItems.text = itemSummary

        // Apply your custom drawables based on status
        val statusBg = when (order.status) {
            "Pending" -> R.drawable.status_bg_pending
            "Preparing" -> R.drawable.status_bg_preparing
            "Ready" -> R.drawable.status_bg_ready
            "Completed" -> R.drawable.status_bg_completed
            else -> R.drawable.status_bg_pending
        }
        holder.tvStatus.setBackgroundResource(statusBg)

        // Determine what the next button should do
        val nextStatus = when (order.status) {
            "Pending" -> "Preparing"
            "Preparing" -> "Ready"
            "Ready" -> "Completed"
            else -> null
        }

        if (nextStatus != null) {
            holder.btnNext.visibility = View.VISIBLE
            holder.btnNext.text = "Mark $nextStatus"
            holder.btnNext.setOnClickListener {
                helper.updateOrderStatus(order.orderId ?: "", nextStatus) { success ->
                    if (success) {
                        Toast.makeText(holder.itemView.context, "Order is now $nextStatus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            holder.btnNext.visibility = View.GONE
        }
    }

    override fun getItemCount() = orderList.size

    fun updateList(newList: List<OrderModel>) {
        orderList = newList
        notifyDataSetChanged()
    }
}