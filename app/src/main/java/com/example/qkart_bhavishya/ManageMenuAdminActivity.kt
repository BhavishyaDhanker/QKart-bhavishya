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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

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

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu_item, null)

        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice)
        val etDesc = dialogView.findViewById<EditText>(R.id.etItemDesc)
        val etCategory = dialogView.findViewById<EditText>(R.id.etItemCategory)
        dialogImageView = dialogView.findViewById(R.id.ivItemImage)
        val btnSelect = dialogView.findViewById<TextView>(R.id.btnSelectImage)

        imageUri = null
        btnSelect.setOnClickListener { getImage.launch("image/*") }

        builder.setView(dialogView)
        builder.setPositiveButton("Add") { _, _ ->
            val name = etName.text.toString()
            val price = etPrice.text.toString()
            if (name.isNotEmpty() && price.isNotEmpty() && imageUri != null) {
                uploadImageAndSave(name, price, etDesc.text.toString(), etCategory.text.toString())
            } else {
                Toast.makeText(this, "Fill all fields and select image", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null).show()
    }

    private fun uploadImageAndSave(name: String, price: String, desc: String, cat: String) {
        val pd = android.app.ProgressDialog(this)
            .apply { setMessage("Uploading..."); setCancelable(false); show() }

        val fileName = "menu_${System.currentTimeMillis()}.jpg"
        val ref = FirebaseStorage.getInstance().reference.child("menu_images/$fileName")

        ref.putFile(imageUri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                // This passes the task to the next step only after upload is fully done
                ref.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val item = MenuItem(
                        name = name,
                        price = price,
                        description = desc,
                        category = cat,
                        imageUrl = downloadUri.toString()
                    )

                    helper.addMenuItem(item) { success ->
                        pd.dismiss()
                        if (success) {
                            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    pd.dismiss()
                    val error = task.exception?.localizedMessage ?: "Unknown Error"
                    Toast.makeText(this, "Upload Failed: $error", Toast.LENGTH_LONG).show()
                    android.util.Log.e("UPLOAD_ERROR", "Error: $error")
                }
            }
    }
        private fun showLogoutDialog() {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to log out from the Admin panel?")
            builder.setIcon(R.drawable.outline_exit_24) // Ensure you have an exit icon in drawable

            // 1. If the user clicks "Yes"
            builder.setPositiveButton("Yes") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }

            // 2. If the user clicks "No"
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        private fun performLogout() {
            // 1. Sign out from Firebase Authentication
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

            // 2. Clear local SharedPreferences to remove saved rollNo/Name
            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // 3. Navigate back to the Sign-In screen
            val intent = Intent(this, SignIn1Activity::class.java)

            // 4. CRITICAL: Clear the activity stack so the user can't press "Back" to return to Admin
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()

            Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()
        }
}