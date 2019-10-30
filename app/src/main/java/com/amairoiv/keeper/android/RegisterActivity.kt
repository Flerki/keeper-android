package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import com.amairoiv.keeper.android.service.UserService

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        setContentView(R.layout.activity_register)
    }

    fun register(view: View) {
        val userEmailEditText = findViewById<EditText>(R.id.registerEmail)
        val password = findViewById<EditText>(R.id.userPasswordRegister).text.toString()

        val email = userEmailEditText.text.toString()
        val userId = UserService.register(email, password)

        UserService.setUser(userId)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showAuthenticationActivity(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        finish()
    }
}
