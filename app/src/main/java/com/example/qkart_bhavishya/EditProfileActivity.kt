package com.example.qkart_bhavishya

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etRollNo: EditText // This will be read-only
    private lateinit var btnSave: Button
    private lateinit var helper: FirestoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        helper = FirestoreHelper()
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)


        etName = findViewById(R.id.etEditName)
        etPhone = findViewById(R.id.etEditPhone)
        etRollNo = findViewById(R.id.etEditRollNo)
        btnSave = findViewById(R.id.btnSaveProfile)

        // Loading current data from Local Storage for the user to see
        val currentName = sharedPref.getString("userName", "") ?: ""
        val currentPhone = sharedPref.getString("userPhone", "") ?: ""
        val currentRoll = sharedPref.getString("rollNo", "") ?: ""

        etName.setText(currentName)
        etPhone.setText(currentPhone)

        // Disable Roll No editing
        etRollNo.setText(currentRoll)
        etRollNo.isEnabled = false

        // Save Button Logic
        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()

            if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                saveChanges(newName, newPhone)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChanges(name: String, phone: String) {
        val pd = android.app.ProgressDialog(this)
        pd.setMessage("Updating Profile...")
        pd.setCancelable(false)
        pd.show()

        // Calls the helper function
        helper.updateUserProfile(name, phone) { success ->
            pd.dismiss()
            if (success) {
                // If Cloud update worked, update Local Storage too
                val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("userName", name)
                    putString("userPhone", phone)
                    apply()
                }

                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                finish() // Close activity and go back
            } else {
                Toast.makeText(this, "Update Failed. Check internet.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}