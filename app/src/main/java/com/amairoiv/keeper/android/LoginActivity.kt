package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import com.amairoiv.keeper.android.service.UserService

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val SDK_INT = android.os.Build.VERSION.SDK_INT
                if (SDK_INT > 8) {
                    val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                }
        setContentView(R.layout.activity_login)
    }

    fun authenticate(view: View) {
        val userEmailEditText = findViewById<EditText>(R.id.userEmail)

        val email = userEmailEditText.text.toString()
        val password = findViewById<EditText>(R.id.userPasswordAuth).text.toString()

        val userId = UserService.auth(email, password)

        if (userId != null){
            UserService.setUser(userId)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun showRegisterActivity(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

        finish()
    }
}
