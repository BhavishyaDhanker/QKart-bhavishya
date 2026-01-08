package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainScreenActivity : AppCompatActivity() {

    private lateinit var foodAdapter: StudentMenuAdapter
    private val helper = FirestoreHelper()
    private var fullMenuList = listOf<MenuItem>()
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // Setup Bottom Nav
        bottomNav = findViewById(R.id.bottomNavigationView)
        setupBottomNav()
        updateCartBadge() // Initial Badge Check

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
            updateCartBadge() // Update badge immediately when adding item
            Toast.makeText(this, "${item.name} added", Toast.LENGTH_SHORT).show()
        }
        rvMenu.adapter = foodAdapter

        helper.getMenu { items ->
            fullMenuList = items
            filterMenu("All")
        }
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // We are already on Home, maybe scroll to top?
                    findViewById<RecyclerView>(R.id.rvStudentMenu).smoothScrollToPosition(0)
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, ActivityCart::class.java))
                    false // Return false so the icon doesn't stay selected when we come back
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    false
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure "Home" is selected when we return to this screen
        bottomNav.selectedItemId = R.id.nav_home
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val cartSize = CartManager.getCartSize()
        val badge = bottomNav.getOrCreateBadge(R.id.nav_cart) // Built-in Badge!

        if (cartSize > 0) {
            badge.isVisible = true
            badge.number = cartSize
            badge.backgroundColor = getColor(R.color.white) // Optional: customize color
            badge.badgeTextColor = getColor(R.color.maroon)
        } else {
            badge.isVisible = false
        }
    }

    private fun filterMenu(category: String) {
        val filtered = if (category == "All") fullMenuList else fullMenuList.filter { it.category.equals(category, true) }
        foodAdapter.updateList(filtered)
    }
}