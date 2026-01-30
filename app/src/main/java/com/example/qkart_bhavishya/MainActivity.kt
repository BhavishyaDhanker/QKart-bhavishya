package com.example.qkart_bhavishya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)

        bottomNav = findViewById(R.id.bottomNavigationView)

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_cart -> loadFragment(CartFragment())
                R.id.nav_history -> loadFragment(HistoryFragment())
                R.id.nav_account -> loadFragment(AccountFragment())
                else -> false
            }
            true
        }

        // Initial badge update
        updateCartBadge()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun updateCartBadge() {
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