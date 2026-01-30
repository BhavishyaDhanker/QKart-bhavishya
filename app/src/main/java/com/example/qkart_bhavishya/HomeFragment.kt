package com.example.qkart_bhavishya

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var foodAdapter: StudentMenuAdapter
    private val helper = FirestoreHelper()
    private var fullMenuList = listOf<MenuItem>()
    private var currentCategory = "All"
    private var currentSearchText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Toolbar
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.inflateMenu(R.menu.menu_main_search)

        val searchItem = topAppBar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search food..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText ?: ""
                applyFilters()
                return true
            }
        })

        // Setup Categories
        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val categories = listOf("All", "Snacks", "Drinks", "Meals", "Desserts")

        rvCategories.adapter = CategoryAdapter(categories) { category ->
            currentCategory = category
            applyFilters()
        }

        // Setup Menu
        val rvMenu = view.findViewById<RecyclerView>(R.id.rvStudentMenu)
        rvMenu.layoutManager = LinearLayoutManager(requireContext())
        foodAdapter = StudentMenuAdapter(emptyList()) { item ->
            CartManager.addItem(item)
            Toast.makeText(requireContext(), "${item.name} added", Toast.LENGTH_SHORT).show()
            // require context is used because fragments don't have context of there own so it asks the host activity for context.

            // Update the badge in the Parent Activity
            (activity as? MainActivity)?.updateCartBadge()
        }
        rvMenu.adapter = foodAdapter

        // Load Data
        helper.getMenu { items ->
            fullMenuList = items
            applyFilters()
        }
    }

    private fun applyFilters() {
        val filteredList = fullMenuList.filter { item ->
            val matchesCategory = (currentCategory == "All" || item.category.equals(currentCategory, ignoreCase = true))
            val matchesSearch = item.name.contains(currentSearchText, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        foodAdapter.updateList(filteredList)
    }
}