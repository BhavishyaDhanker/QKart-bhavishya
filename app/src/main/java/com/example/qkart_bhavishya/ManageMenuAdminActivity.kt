package com.example.qkart_bhavishya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth

class ManageMenuAdminActivity : AppCompatActivity() {

    private lateinit var helper: FirestoreHelper
    private var imageUri: Uri? = null
    private lateinit var dialogImageView: ImageView

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            dialogImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_menu_admin)
        helper = FirestoreHelper()
        initCloudinary()

        findViewById<TextView>(R.id.btnLiveOrders).setOnClickListener {
            startActivity(Intent(this, LiveOrdersAdminActivity::class.java))
            finish()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnUpdateMenu).setOnClickListener {
            showAddItemDialog()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvmenu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AdminMenuAdapter(emptyList(), helper)
        recyclerView.adapter = adapter
        helper.getMenu { items -> adapter.updateList(items) }

        findViewById<ImageView>(R.id.btnlogout).setOnClickListener { showLogoutDialog() }
    }

    private fun initCloudinary() {
        try {
            val config = mapOf("cloud_name" to "dcpz3cpdj", "secure" to true) // cloud_name
            MediaManager.init(this, config)
        } catch (e: Exception) {}
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu_item, null)
        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice)
        val etDesc = dialogView.findViewById<EditText>(R.id.etItemDesc)
        val etCategory = dialogView.findViewById<EditText>(R.id.etItemCategory)
        dialogImageView = dialogView.findViewById(R.id.ivItemImage)

        dialogView.findViewById<TextView>(R.id.btnSelectImage).setOnClickListener { getImage.launch("image/*") }

        builder.setView(dialogView)
        builder.setPositiveButton("Add") { _, _ ->
            val name = etName.text.toString()
            val price = etPrice.text.toString()
            if (name.isNotEmpty() && price.isNotEmpty() && imageUri != null) {
                uploadToCloudinary(name, price, etDesc.text.toString(), etCategory.text.toString())
            } else {
                Toast.makeText(this, "Fill all fields and select image", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null).show()
    }

    private fun uploadToCloudinary(name: String, price: String, desc: String, cat: String) {
        val pd = android.app.ProgressDialog(this).apply { setMessage("Uploading..."); show() }

        MediaManager.get().upload(imageUri).unsigned("QKart_bhavishya")  // preset name
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, b: Long, t: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"].toString()
                    val item = MenuItem(name=name, price=price, description=desc, category=cat, imageUrl=url)
                    helper.addMenuItem(item) {
                        pd.dismiss()
                        Toast.makeText(this@ManageMenuAdminActivity, "Success!", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    pd.dismiss()
                    Toast.makeText(this@ManageMenuAdminActivity, "Error: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setIcon(R.drawable.outline_exit_24)

        builder.setPositiveButton("Yes") { dialog, _ ->
            performLogout()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        // Clear local user data
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Go back to Login Screen
        val intent = Intent(this, SignIn1Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}