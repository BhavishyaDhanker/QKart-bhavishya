package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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

        val cartBadgeDot = findViewById<View>(R.id.cartBadgeDot)

        if (CartManager.getCartList().isNotEmpty()) {
            cartBadgeDot.visibility = View.VISIBLE
        }

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)

        // Setting the layout manager to HORIZONTAL
            rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // list of categories
            val categories = listOf("All", "Snacks", "Drinks", "Meals", "Desserts")

        // adapter syntax
            val catAdapter = CategoryAdapter(categories) { selectedCategory ->
                filterMenu(selectedCategory)
            }
            rvCategories.adapter = catAdapter

        // Setup of Food RecyclerView
        val rvMenu = findViewById<RecyclerView>(R.id.rvStudentMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        foodAdapter = StudentMenuAdapter(emptyList()) { item ->
            CartManager.addItem(item)
            cartBadgeDot.visibility = View.VISIBLE // to show the badge on top of cart icon
            Toast.makeText(this, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        rvMenu.adapter = foodAdapter

        // Fetching Data from database
        helper.getMenu { items ->
            fullMenuList = items
            filterMenu("All") // Show all items initially
        }


        val btnCart = findViewById<ImageView>(R.id.btnCart)
        btnCart.setOnClickListener {
            val intentCart = Intent(this , ActivityCart::class.java)
            startActivity(intentCart)
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