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

    // This is keeping track of which category is "Active"
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

        // 1. Tell the view if it is selected or not
        holder.itemView.isSelected = (selectedPosition == position)

        if (selectedPosition == position) {

            holder.tvCategory.setTextColor(Color.WHITE)
        } else {

            holder.tvCategory.setTextColor(Color.parseColor("#800000"))
        }

        // Updating UI to show the new selection
        holder.itemView.setOnClickListener {

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