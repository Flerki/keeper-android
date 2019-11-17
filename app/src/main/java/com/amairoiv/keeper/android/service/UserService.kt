package com.amairoiv.keeper.android.service

import com.amairoiv.keeper.android.model.Element
import com.amairoiv.keeper.android.model.Item
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


object UserService {

    private const val BASE_URL = "10.0.2.2"
    private const val BASE_PORT = "8080"

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    private var userId: String? = null
    private val gson = Gson()

    fun register(email: String, password: String): String {
        val json = "{\"email\":\"$email\", \"password\":\"$password\"}"
        val body = json.toRequestBody(JSON)
        val url = "http://$BASE_URL:$BASE_PORT/users"
        val request: Request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        client.newCall(request).execute()

        return auth(email, password)!!
    }

    fun auth(email: String, password: String): String? {
        val json = "{\"email\":\"$email\", \"password\":\"$password\"}"
        val body = json.toRequestBody(JSON)
        val url = "http://$BASE_URL:$BASE_PORT/users"
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return result
    }

    fun addPlace(placeId: String){
        val url = "http://$BASE_URL:$BASE_PORT/users/$userId/places/$placeId"
        val request: Request = Request.Builder()
            .url(url)
            .put("".toRequestBody())
            .build()
        client.newCall(request).execute()
    }

    fun deletePlace(placeId: String){
        val url = "http://$BASE_URL:$BASE_PORT/users/$userId/places/$placeId"
        val request: Request = Request.Builder()
            .url(url)
            .delete("".toRequestBody())
            .build()
        client.newCall(request).execute()

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

    fun addToRecent(item: Item) {
        val url = "http://$BASE_URL:$BASE_PORT/users/${userId}/items/recent/${item.id}"
        val request: Request = Request.Builder()
            .url(url)
            .put("".toRequestBody())
            .build()
        client.newCall(request).execute()
    }

    fun getRecentItems(): MutableList<Item> {
        val url = "http://$BASE_URL:$BASE_PORT/users/${userId}/items/recent"
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return gson.fromJson(result, Array<Item>::class.java).toMutableList()
    }

    fun getAllElements(): MutableList<Element> {
        val url = "http://$BASE_URL:$BASE_PORT/users/${userId}/elements"
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return gson.fromJson(result, Array<Element>::class.java).toMutableList()
    }
}