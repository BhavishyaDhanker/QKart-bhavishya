package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ManageMenuAdminActivity : AppCompatActivity() {

    private lateinit var helper: FirestoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_menu_admin)

        helper = FirestoreHelper()

        val btnLiveOrders = findViewById<TextView>(R.id.btnLiveOrders)
        btnLiveOrders.setOnClickListener {
            val intent = Intent(this, LiveOrdersAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 2. Button to trigger the Add Dialog
        val btnUpdateMenu = findViewById<androidx.cardview.widget.CardView>(R.id.btnUpdateMenu)
        btnUpdateMenu.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        // Create the dialog builder
        val builder = AlertDialog.Builder(this)

        // Inflate your custom layout
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_menu_item, null)

        // Find the views inside the dialog layout
        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice)
        val etDesc = dialogView.findViewById<EditText>(R.id.etItemDesc)
        val etCategory = dialogView.findViewById<EditText>(R.id.etItemCategory)

        builder.setView(dialogView)
        builder.setPositiveButton("Add") { _, _ ->

            val name = etName.text.toString()
            val price = etPrice.text.toString()
            val desc = etDesc.text.toString()
            val category = etCategory.text.toString()

            if (name.isNotEmpty() && price.isNotEmpty()) {
                val newItem = MenuItem(
                    name = name,
                    price = price,
                    description = desc,
                    category = category,
                    imageUrl = "" // For now, we leave this empty or use a placeholder
                )

                // Use the helper to save to Firestore
                helper.addMenuItem(newItem) { success ->
                    if (success) {
                        Toast.makeText(this, "Dish Added!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill Name and Price", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}