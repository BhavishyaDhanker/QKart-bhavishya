package com.example.qkart_bhavishya

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class CartAdapter(private var cartList: List<CartItem>, private val onCartChanged: () -> Unit) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvCartItemName)
        val tvPrice = view.findViewById<TextView>(R.id.tvCartItemPrice)
        val tvQtyValue = view.findViewById<TextView>(R.id.tvCartItemQtyValue)
        val ivCartImage = view.findViewById<ShapeableImageView>(R.id.imgCartItem)
        val btnPlus = view.findViewById<TextView>(R.id.btnPlus)
        val btnMinus = view.findViewById<TextView>(R.id.btnMinus)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "₹${item.price * item.quantity}"
        holder.tvQtyValue.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.burger)
            .into(holder.ivCartImage)

        holder.btnPlus.setOnClickListener { CartManager.incrementItem(item.itemId); onCartChanged() }
        holder.btnMinus.setOnClickListener { CartManager.decrementItem(item.itemId); onCartChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CartViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
    )

    override fun getItemCount() = cartList.size
    fun updateData(newList: List<CartItem>) { cartList = newList; notifyDataSetChanged() }
}