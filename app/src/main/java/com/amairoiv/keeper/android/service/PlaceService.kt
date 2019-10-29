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

    private var places: MutableList<Place> = ArrayList()

    fun initializePlaceHierarchyFor(userId: String) {
        val url = "http://10.0.2.2:8080/users/$userId/places"
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body?.string()

        places = gson.fromJson(result, Array<Place>::class.java).toMutableList()
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

    fun getRoot(): MutableList<Place> {
        return places
    }

    fun exists(placeId: String): Boolean {
        return findById(placeId) != null
    }

    fun findById(placeId: String): Place? {
        return findById(places, placeId)
    }

    private fun findById(places: List<Place>, placeId: String): Place? {
        for (place in places) {
            if (place.id == placeId) {
                return place
            }
            val fromChildren = findById(place.children, placeId)
            if (fromChildren != null) {
                return fromChildren
            }
        }
        return null
    }

    fun deletePlace(placeId: String) {
        val url = "http://10.0.2.2:8080/places/$placeId"
        val request: Request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).execute()

        val parentId = findById(placeId)!!.parentId

        if (parentId != null) {
            val parentPlace = findById(parentId)
            parentPlace?.children?.removeIf { it.id == placeId }
        } else {
            places.removeIf { it.id == placeId }
        }
    }
}