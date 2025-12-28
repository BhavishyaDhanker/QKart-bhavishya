package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class StudentOrderAdapter(private var orders: List<OrderModel>) :
    RecyclerView.Adapter<StudentOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItems: TextView = view.findViewById(R.id.txtOrderItems)
        val tvTotal: TextView = view.findViewById(R.id.txtTotalAmount)
        val tvStatus: TextView = view.findViewById(R.id.txtStatus)
        val tvTime: TextView = view.findViewById(R.id.txtOrderDate)
        val counter: TextView = view.findViewById(R.id.counter)
        val btnReorder: TextView = view.findViewById(R.id.reorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvItems.text = order.items.joinToString { "${it.quantity}x ${it.name}" }
        holder.tvTotal.text = "₹${order.totalAmount}"
        holder.tvStatus.text = order.status

        // Simple timestamp to readable text (e.g., Dec 28, 08:00 PM)
        val date = java.text.DateFormat.getDateTimeInstance().format(java.util.Date(order.timestamp))
        holder.tvTime.text = date


        if (order.status == "Ready") {
            holder.counter.visibility = View.VISIBLE
        } else {
            holder.counter.visibility = View.GONE
        }

        //  Logic for "Reorder" Button (Visible only when COMPLETED)
        if (order.status == "Completed") {
            holder.btnReorder.visibility = View.VISIBLE
        }else{
            holder.btnReorder.visibility = View.GONE
        }

        // Inside StudentOrderAdapter.kt (onBindViewHolder)

        holder.btnReorder.setOnClickListener {
            val context = holder.itemView.context
            val helper = FirestoreHelper()

            //  Show Confirmation Dialog
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Reorder Items")
                .setMessage("Place this order again immediately?")
                .setPositiveButton("Order Now") { _, _ ->


                    val freshOrder = order.copy(
                        orderId = "", // FirestoreHelper will generate a new unique ID
                        status = "Pending",
                        timestamp = System.currentTimeMillis()
                    )

                    //placing order
                    helper.placeOrder(freshOrder) { success ->
                        if (success) {
                            Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to reorder. Check connection.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        // Apply your custom drawables
        val statusBg = when (order.status) {
            "Pending" -> R.drawable.status_bg_pending
            "Preparing" -> R.drawable.status_bg_preparing
            "Ready" -> R.drawable.status_bg_ready
            "Completed" -> R.drawable.status_bg_completed
            else -> R.drawable.status_bg_pending
        }
        holder.tvStatus.setBackgroundResource(statusBg)


    }

    override fun getItemCount() = orders.size

    fun updateList(newList: List<OrderModel>) {
        orders = newList
        notifyDataSetChanged()
    }
}