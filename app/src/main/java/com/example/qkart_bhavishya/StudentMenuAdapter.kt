package com.example.qkart_bhavishya

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class StudentMenuAdapter(private var menuList: List<MenuItem>, private val onAddClicked: (MenuItem) -> Unit) :
    RecyclerView.Adapter<StudentMenuAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.food_title)
        val tvPrice = view.findViewById<TextView>(R.id.food_price)
        val ivFood = view.findViewById<ShapeableImageView>(R.id.food_image)
        val btnAdd = view.findViewById<View>(R.id.add_btn_text)
        val soldOutOverlay = view.findViewById<View>(R.id.soldOutOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val item = menuList[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "₹${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.burger)
            .into(holder.ivFood)

        holder.soldOutOverlay.visibility = if (item.isAvailable) View.GONE else View.VISIBLE
        holder.btnAdd.isEnabled = item.isAvailable
        holder.btnAdd.setOnClickListener { onAddClicked(item) }
    }

    override fun getItemCount() = menuList.size
    fun updateList(newList: List<MenuItem>) { menuList = newList; notifyDataSetChanged() }
}