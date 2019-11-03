package com.amairoiv.keeper.android.service

import com.amairoiv.keeper.android.dto.CreateItem
import com.amairoiv.keeper.android.dto.UpdateItem
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

object ItemService {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    fun get(itemId: String): Item {
        val url = "http://10.0.2.2:8080/items/$itemId"
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        return gson.fromJson(result, Item::class.java)
    }
    fun deleteItem(itemId: String) {
        val url = "http://10.0.2.2:8080/items/$itemId"
        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).execute()
    }

    fun rename(item: Item, newName: String) {
        item.name = newName

        val dto = UpdateItem(newName, item.placeId)
        val url = "http://10.0.2.2:8080/items/${item.id}"
        val request: Request = Request.Builder()
            .url(url)
            .put(gson.toJson(dto).toRequestBody(JSON))
            .build()

        client.newCall(request).execute()
    }

    fun move(item: Item, newPlace: Place) {
        if (item.placeId == newPlace.id) {
            return
        }

        item.placeId = newPlace.id

        val dto = UpdateItem(item.name, item.placeId)
        val url = "http://10.0.2.2:8080/items/${item.id}"
        val request: Request = Request.Builder()
            .url(url)
            .put(gson.toJson(dto).toRequestBody(JSON))
            .build()

        client.newCall(request).execute()
    }

    fun createItem(dto: CreateItem) {
        val url = "http://10.0.2.2:8080/items"
        val request: Request = Request.Builder()
            .url(url)
            .post(gson.toJson(dto).toRequestBody(JSON))
            .build()

        client.newCall(request).execute()
    }
}