package com.example.qkart_bhavishya

object CartManager {
    // using mapOf because it creates a map like it links the item (CartItem) with a key (string)
    private val selectedItems = mutableMapOf<String, CartItem>()

    fun addItem(item: MenuItem) {
        val id = item.id ?: return
        val price = item.price.toDoubleOrNull() ?: 0.0

        if (selectedItems.containsKey(id)) {
            // Item exists  increase quantity
            val existingItem = selectedItems[id]!!
            selectedItems[id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // New item, add to map
            selectedItems[id] = CartItem(id, item.name, "https://share.google/dka4pCVsUMVmyWXxa", 1, price= price)
        }
    }

    fun getCartList(): List<CartItem> = selectedItems.values.toList()

    fun getTotalPrice(): Double = selectedItems.values.sumOf { it.price * it.quantity }

    fun clearCart() {
        selectedItems.clear()
    }

    fun incrementItem(itemId: String) {
        selectedItems[itemId]?.let {
            selectedItems[itemId] = it.copy(quantity = it.quantity + 1)
        }
    }

    fun decrementItem(itemId: String) {
        selectedItems[itemId]?.let {
            if (it.quantity > 1) {
                selectedItems[itemId] = it.copy(quantity = it.quantity - 1)
            } else {
                selectedItems.remove(itemId)
            }
        }
    }
}