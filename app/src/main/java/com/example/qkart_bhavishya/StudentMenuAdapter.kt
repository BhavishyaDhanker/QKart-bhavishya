package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class StudentMenuAdapter(
    private var menuList: List<MenuItem>,
    private val onAddClicked: (MenuItem) -> Unit // This tells the Activity when "+" is clicked
) : RecyclerView.Adapter<StudentMenuAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.food_title)
        val tvPrice: TextView = view.findViewById(R.id.food_price)
        val tvDesc: TextView = view.findViewById(R.id.food_description)
        val btnAdd: View = view.findViewById(R.id.add_btn_text)
        val soldOutOverlay: View = view.findViewById(R.id.soldOutOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val item = menuList[position]

        holder.tvName.text = item.name
        holder.tvPrice.text = "₹ ${item.price}"
        holder.tvDesc.text = item.description

        // Check if item is available
        if (item.isAvailable) {
            holder.soldOutOverlay.visibility = View.GONE
            holder.btnAdd.isEnabled = true
            holder.btnAdd.alpha = 1.0f
        } else {
            holder.soldOutOverlay.visibility = View.VISIBLE
            holder.btnAdd.isEnabled = false
            holder.btnAdd.alpha = 0.5f // Make button look disabled
        }

        holder.btnAdd.setOnClickListener {
            onAddClicked(item)
        }
    }

    override fun getItemCount() = menuList.size

    fun updateList(newList: List<MenuItem>) {
        menuList = newList
        notifyDataSetChanged()
    }
}