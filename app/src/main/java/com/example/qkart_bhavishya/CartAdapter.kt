package com.example.qkart_bhavishya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private var cartList: List<CartItem>,
                  private val onCartChanged: () -> Unit) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvQty: TextView = view.findViewById(R.id.tvCartItemQty)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val btnPlus: TextView = view.findViewById(R.id.btnPlus)
        val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        val tvQtyValue: TextView = view.findViewById(R.id.tvCartItemQtyValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        holder.tvName.text = item.name
        holder.tvQty.text = "x${item.quantity}"

        // Calculate subtotal for this item row
        val subTotal = item.price * item.quantity
        holder.tvPrice.text = "₹ $subTotal"

        holder.btnPlus.setOnClickListener {
            CartManager.incrementItem(item.itemId)
            notifyItemChanged(position)
            onCartChanged() // A callback to update the Total Price in Activity
        }

        holder.btnMinus.setOnClickListener {
            CartManager.decrementItem(item.itemId)
            if (item.quantity <= 1) {
                // If it was the last one, it gets removed from the list
                notifyDataSetChanged()
            } else {
                notifyItemChanged(position)
            }
            onCartChanged()
        }
    }

    override fun getItemCount() = cartList.size

    fun updateData(newList: List<CartItem>) {
        this.cartList = newList
        notifyDataSetChanged()
    }
}