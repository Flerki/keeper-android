package com.amairoiv.keeper.android.service

import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object PlaceService {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getForUser(userId: String): List<Place> {
        val url = "http://10.0.2.2:8080/users/$userId/places"
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return gson.fromJson(result, Array<Place>::class.java).toList()
    }

    fun getItemForPlace(placeId: String): List<Item> {
        val url = "http://10.0.2.2:8080/places/$placeId/items"
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return gson.fromJson(result, Array<Item>::class.java).toList()
    }
}