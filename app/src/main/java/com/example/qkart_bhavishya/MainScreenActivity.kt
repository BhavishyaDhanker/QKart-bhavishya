package com.example.qkart_bhavishya

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainScreenActivity : AppCompatActivity() {

    private lateinit var foodAdapter: StudentMenuAdapter
    private lateinit var catAdapter: CategoryAdapter
    private val helper = FirestoreHelper()
    private var fullMenuList = listOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // 1. Setup Welcome Message
       // val tvWelcome = findViewById<TextView>(R.id.tvWelcomeUser)
        // tvWelcome.text = "Welcome, Student" // You can replace with actual name later

        // Inside MainScreenActivity onCreate
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)

        // 2.1. Set the layout manager to HORIZONTAL
            rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 2.2. Define your list of categories
            val categories = listOf("All", "Snacks", "Drinks", "Meals", "Desserts")

        // 2.3. Set the adapter
            val catAdapter = CategoryAdapter(categories) { selectedCategory ->
                filterMenu(selectedCategory)
            }
            rvCategories.adapter = catAdapter

        // 3. Setup Food RecyclerView
        val rvMenu = findViewById<RecyclerView>(R.id.rvStudentMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        foodAdapter = StudentMenuAdapter(emptyList()) { item ->
            // Logic for adding to cart goes here!
            Toast.makeText(this, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        rvMenu.adapter = foodAdapter

        // 4. Fetch Data
        helper.getMenu { items ->
            fullMenuList = items
            filterMenu("All") // Show all items initially
        }
    }

    private fun filterMenu(category: String) {
        val filteredList = if (category == "All") {
            fullMenuList // fullMenuList is the original list from Firestore
        } else {
            fullMenuList.filter { it.category.equals(category, ignoreCase = true) }
        }
        foodAdapter.updateList(filteredList)
    }
}