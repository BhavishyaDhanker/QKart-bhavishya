package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class AdminMenuAdapter(
    private var menuList: List<MenuItem>,
    private val helper: FirestoreHelper
) : RecyclerView.Adapter<AdminMenuAdapter.AdminViewHolder>() {

    class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.food_title)
        val tvPrice: TextView = view.findViewById(R.id.food_price)
        val tvDesc: TextView = view.findViewById(R.id.food_description)
        val swStatus: SwitchCompat = view.findViewById(R.id.switchAvailability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val item = menuList[position]

        holder.tvName.text = item.name
        holder.tvPrice.text = "₹ ${item.price}"
        holder.tvDesc.text = item.description



        // 1. Remove any old listener so it doesn't fire while setting initial state
        holder.swStatus.setOnCheckedChangeListener(null)

        // 2. Set the switch position from the data
        holder.swStatus.isChecked = item.isAvailable

        // 3. Set the NEW listener
        holder.swStatus.setOnCheckedChangeListener { _, isChecked ->
            val itemId = item.id

            if (itemId != null) {
                // Update local data immediately to prevent the "bounce"
                item.isAvailable = isChecked

                helper.updateMenuAvailability(itemId, isChecked) { success ->
                    if (success) {
                        Toast.makeText(holder.itemView.context, "Updated: ${item.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        // If it fails in the cloud, revert the local UI
                        item.isAvailable = !isChecked
                        holder.swStatus.isChecked = !isChecked
                        Toast.makeText(holder.itemView.context, "Failed to update database", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(holder.itemView.context, "Error: Item ID missing", Toast.LENGTH_SHORT).show()
                holder.swStatus.isChecked = !isChecked // Revert UI
            }
        }
    }

    override fun getItemCount() = menuList.size

    fun updateList(newList: List<MenuItem>) {
        menuList = newList
        notifyDataSetChanged()
    }
}