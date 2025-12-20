package com.example.qkart_bhavishya

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClicked: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // This keeps track of which pill is "Active" (Default is 0 for "All")
    private var selectedPosition = 0

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategoryName)
        val card: CardView = view.findViewById(R.id.categoryCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvCategory.text = category

        // --- Visual Feedback Logic ---
        if (selectedPosition == position) {
            // Selected: Maroon background, White text
            holder.card.setCardBackgroundColor(Color.parseColor("#800000"))
            holder.tvCategory.setTextColor(Color.WHITE)
        } else {
            // Not Selected: Light background, Maroon text
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF7FF"))
            holder.tvCategory.setTextColor(Color.parseColor("#800000"))
        }

        holder.itemView.setOnClickListener {
            // Update the UI to show the new selection
            val lastPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(lastPosition)
            notifyItemChanged(selectedPosition)

            // Trigger the filter in the Activity
            onCategoryClicked(category)
        }
    }

    override fun getItemCount() = categories.size
}