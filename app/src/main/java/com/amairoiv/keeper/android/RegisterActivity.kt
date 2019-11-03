package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import com.amairoiv.keeper.android.service.UserService

class RegisterActivity : AppCompatActivity() {

    private lateinit var password: EditText
    private lateinit var passwordRepeat: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        setContentView(R.layout.activity_register)

        password = findViewById(R.id.userPasswordRegister)
        passwordRepeat = findViewById(R.id.confirmUserPasswordRegister)

        findViewById<TextView>(R.id.confirmUserPasswordError).isVisible = false

        val passwordIdentityCheck = object :
            TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (password.text.toString() != passwordRepeat.text.toString()) {
                    findViewById<TextView>(R.id.confirmUserPasswordError).isVisible = true
                    findViewById<Button>(R.id.registerButton).isClickable = false
                } else {
                    findViewById<TextView>(R.id.confirmUserPasswordError).isVisible = false
                    findViewById<Button>(R.id.registerButton).isClickable = true
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                findViewById<TextView>(R.id.confirmUserPasswordError).isVisible = false
            }
        }
        password.addTextChangedListener(passwordIdentityCheck)
        passwordRepeat.addTextChangedListener(passwordIdentityCheck)

    }

    fun register(view: View) {
        val userEmailEditText = findViewById<EditText>(R.id.registerEmail)
        val password = this.password.text.toString()

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
