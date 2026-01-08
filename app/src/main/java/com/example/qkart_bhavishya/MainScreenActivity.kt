package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainScreenActivity : AppCompatActivity() {

    private lateinit var foodAdapter: StudentMenuAdapter
    private val helper = FirestoreHelper()
    private var fullMenuList = listOf<MenuItem>()
    private lateinit var cartBadgeDot: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        cartBadgeDot = findViewById(R.id.cartBadgeDot)
        updateCartBadge()

        // Categories setup
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categories = listOf("All", "Snacks", "Drinks", "Meals", "Desserts")
        rvCategories.adapter = CategoryAdapter(categories) { filterMenu(it) }

        // Menu setup
        val rvMenu = findViewById<RecyclerView>(R.id.rvStudentMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        foodAdapter = StudentMenuAdapter(emptyList()) { item ->
            CartManager.addItem(item)
            updateCartBadge()
            Toast.makeText(this, "${item.name} added", Toast.LENGTH_SHORT).show()
        }
        rvMenu.adapter = foodAdapter

        helper.getMenu { items ->
            fullMenuList = items
            filterMenu("All")
        }



        // Navigation
        findViewById<ImageView>(R.id.btnCart).setOnClickListener { startActivity(Intent(this, ActivityCart::class.java)) }
        findViewById<ImageView>(R.id.OrderHistory).setOnClickListener { startActivity(Intent(this, OrderHistoryActivity::class.java)) }
        findViewById<ImageView>(R.id.settings).setOnClickListener { startActivity(Intent(this,
            SettingsActivity::class.java)) }
    }

    private fun filterMenu(category: String) {
        val filtered = if (category == "All") fullMenuList else fullMenuList.filter { it.category.equals(category, true) }
        foodAdapter.updateList(filtered)
    }

    override fun onResume() { super.onResume(); updateCartBadge() }

    private fun updateCartBadge() {
        cartBadgeDot.visibility = if (CartManager.getCartList().isNotEmpty()) View.VISIBLE else View.GONE
    }
}