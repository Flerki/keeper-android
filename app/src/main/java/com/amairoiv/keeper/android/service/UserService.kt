package com.amairoiv.keeper.android.service

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


object UserService {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    private var userId: String? = null

    fun register(email: String): String {
        val json = "{\"email\":\"$email\"}"
        val body = json.toRequestBody(JSON)
        val url = "http://10.0.2.2:8080/users"
        val request: Request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        client.newCall(request).execute()

        return auth(email)!!
    }

    fun auth(email: String): String? {
        val json = "{\"email\":\"$email\"}"
        val body = json.toRequestBody(JSON)
        val url = "http://10.0.2.2:8080/users"
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return result
    }

    fun setUser(userId: String) {
        this.userId = userId
    }

    fun getUser(): String {
        return userId!!
    }

    fun logout() {
        userId = null
    }
}