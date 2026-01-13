package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainScreenActivity : AppCompatActivity() {

    private lateinit var foodAdapter: StudentMenuAdapter
    private val helper = FirestoreHelper()
    private lateinit var bottomNav: BottomNavigationView

    // DATA STATE
    private var fullMenuList = listOf<MenuItem>() // Holds the original full data
    private var currentCategory = "All"           // Tracks selected category
    private var currentSearchText = ""            // Tracks what user typed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        // 1. Setup Bottom Nav
        bottomNav = findViewById(R.id.bottomNavigationView)
        setupBottomNav()
        updateCartBadge()

        // 2. Setup Top Bar with Search (NEW CODE START)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        // This loads the menu xml we just created into the toolbar
        topAppBar.inflateMenu(R.menu.menu_main_search)

        // Find the search item and set up the listener
        val searchItem = topAppBar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // Optional: Hint text "Search food..."
        searchView.queryHint = "Search food..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update our state variable and refresh the list
                currentSearchText = newText ?: ""
                applyFilters()
                return true
            }
        })
        // (NEW CODE END)

        // 3. Categories Setup
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categories = listOf("All", "Snacks", "Drinks", "Meals", "Desserts")

        rvCategories.adapter = CategoryAdapter(categories) { category ->
            // Update state variable and refresh list
            currentCategory = category
            applyFilters()
        }

        // 4. Menu Setup
        val rvMenu = findViewById<RecyclerView>(R.id.rvStudentMenu)
        rvMenu.layoutManager = LinearLayoutManager(this)
        foodAdapter = StudentMenuAdapter(emptyList()) { item ->
            CartManager.addItem(item)
            updateCartBadge()
            Toast.makeText(this, "${item.name} added", Toast.LENGTH_SHORT).show()
        }
        rvMenu.adapter = foodAdapter

        // 5. Fetch Data
        helper.getMenu { items ->
            fullMenuList = items
            applyFilters() // Load initial data
        }
    }

    // This function combines both the Category and the Search Text
    private fun applyFilters() {
        val filteredList = fullMenuList.filter { item ->
            // Check Category Match
            val matchesCategory = (currentCategory == "All" || item.category.equals(currentCategory, ignoreCase = true))

            // Check Search Text Match
            val matchesSearch = item.name.contains(currentSearchText, ignoreCase = true)

            // Item must match BOTH to show up
            matchesCategory && matchesSearch
        }

        foodAdapter.updateList(filteredList)
    }

    // ... (Bottom Nav and Badge code remains exactly the same as before) ...

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findViewById<RecyclerView>(R.id.rvStudentMenu).smoothScrollToPosition(0)
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, ActivityCart::class.java))
                    false
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
        bottomNav.selectedItemId = R.id.nav_home
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val cartSize = CartManager.getCartSize()
        val badge = bottomNav.getOrCreateBadge(R.id.nav_cart)

        if (cartSize > 0) {
            badge.isVisible = true
            badge.number = cartSize
            badge.backgroundColor = getColor(R.color.white)
            badge.badgeTextColor = getColor(R.color.maroon)
        } else {
            badge.isVisible = false
        }
    }
}