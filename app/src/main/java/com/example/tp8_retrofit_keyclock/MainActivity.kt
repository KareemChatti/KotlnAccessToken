package com.example.tp8_retrofit_keyclock

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                login(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(username: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginService = retrofit.create(LoginInterface::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = loginService.getAccessToken(
                    clientId = "androidapp",
                    grantType = "password",
                    clientSecret = "BgH7Hht5k2Mimn7aqmt76KtxYjRdLyjE", // Example client secret
                    scope = "",
                    username = username,
                    password = password
                )
                if (response.isSuccessful) {
                    val accessToken = response.body()?.accessToken
                    accessToken?.let {
                        fetchCustomerData(it)
                    }
                } else {
                    showError("Login failed: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun fetchCustomerData(token: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8083/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCustomers("Bearer $token")
                if (response.isSuccessful) {
                    // Handle customer data response here
                } else {
                    showError("Failed to fetch customer data: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
