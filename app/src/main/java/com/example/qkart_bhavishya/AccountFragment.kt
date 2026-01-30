package com.example.qkart_bhavishya

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var tvName: TextView
    private lateinit var tvRoll: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvAccountName)
        tvRoll = view.findViewById(R.id.tvAccountRoll)

        loadUserData()

        view.findViewById<TextView>(R.id.btnAccountEditProfile).setOnClickListener {
            // Edit Profile can remain an Activity since it's a separate flow
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        view.findViewById<TextView>(R.id.btnAccountHistory).setOnClickListener {
            // Switch to History Fragment using the Main Activity's BottomNav
            (activity as? MainActivity)?.bottomNav?.selectedItemId = R.id.nav_history
        }

        view.findViewById<TextView>(R.id.btnAccountLogout).setOnClickListener {
            showLogoutDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData() // Refresh data if they came back from EditProfile
    }

    private fun loadUserData() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        tvName.text = sharedPref.getString("userName", "Student")
        tvRoll.text = "Roll No: ${sharedPref.getString("rollNo", "Unknown")}"
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
            CartManager.clearCart()

            val intent = Intent(requireContext(), SignIn1Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}