package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class AdminMenuAdapter(
    private var menuList: List<MenuItem>,
    private val helper: FirestoreHelper
) : RecyclerView.Adapter<AdminMenuAdapter.AdminViewHolder>() {

    class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.food_title)
        val tvPrice: TextView = view.findViewById(R.id.food_price)
        val tvDesc: TextView = view.findViewById(R.id.food_description)
        val swStatus: SwitchCompat = view.findViewById(R.id.switchAvailability)
        val ivAdminItemImage : ShapeableImageView = view.findViewById(R.id.ivAdminItemImage)
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

        // --- Integrated Glide Here ---
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.burger) // Your default placeholder
            .error(R.drawable.burger)       // Image to show if link is broken
            .centerCrop()
            .into(holder.ivAdminItemImage)

        holder.swStatus.setOnCheckedChangeListener(null)

        // Set the switch position from the data
        holder.swStatus.isChecked = item.isAvailable

        // Set the listener
        holder.swStatus.setOnCheckedChangeListener { _, isChecked ->
            val itemId = item.id

            if (itemId != null) {
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
                // Use post to avoid listener conflict during manual revert
                holder.swStatus.post { holder.swStatus.isChecked = !isChecked }
            }
        }
    }

    override fun getItemCount() = menuList.size

    fun updateList(newList: List<MenuItem>) {
        menuList = newList
        notifyDataSetChanged()
    }
}