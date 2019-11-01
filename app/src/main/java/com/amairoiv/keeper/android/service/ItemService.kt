package com.amairoiv.keeper.android.service

import okhttp3.OkHttpClient
import okhttp3.Request

object ItemService {
    private val client = OkHttpClient()

    fun deleteItem(itemId: String) {
        val url = "http://10.0.2.2:8080/items/$itemId"
        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).execute()
    }
}