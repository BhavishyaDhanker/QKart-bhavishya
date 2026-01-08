package com.example.qkart_bhavishya

object CartManager {
    // using mapOf because it creates a map like it links the item (CartItem) with a key (string)
    private val selectedItems = mutableMapOf<String, CartItem>()


    // Returns the number of distinct items in the cart
    fun getCartSize(): Int {
        return selectedItems.size
    }

    fun addItem(item: MenuItem) {
        val id = item.id ?: return
        val price = item.price.toDoubleOrNull() ?: 0.0

        if (selectedItems.containsKey(id)) {
            // Item exists, increase quantity
            val existingItem = selectedItems[id]!!
            selectedItems[id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            selectedItems[id] = CartItem(
                itemId = id,
                name = item.name,
                imageUrl = item.imageUrl, // This fetches the Cloudinary link from Firestore
                quantity = 1,
                price = price
            )
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

    fun addCartItemDirectly(item: CartItem) {
        val id = item.itemId
        if (selectedItems.containsKey(id)) {
            val existing = selectedItems[id]!!
            selectedItems[id] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            selectedItems[id] = item
        }
    }
}