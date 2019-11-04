package com.amairoiv.keeper.android.service

import com.amairoiv.keeper.android.dto.CreatePlace
import com.amairoiv.keeper.android.dto.UpdatePlace
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

object PlaceService {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

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

    fun update(place: Place) {
        val dto = UpdatePlace(place.name, place.parentId)

        val url = "http://10.0.2.2:8080/places/${place.id}"
        val request: Request = Request.Builder()
            .url(url)
            .put(gson.toJson(dto).toRequestBody(JSON))
            .build()
        client.newCall(request).execute()

        val placeModel = findById(place.id)
        placeModel?.name = place.name
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

    fun createPlace(dto: CreatePlace) {
        val url = "http://10.0.2.2:8080/places"
        val request: Request = Request.Builder()
            .url(url)
            .post(gson.toJson(dto).toRequestBody(JSON))
            .build()

        val placeId = client.newCall(request).execute()
            .body!!.string()

        val place = Place(placeId, dto.placeName, ArrayList(), dto.parentPlaceId)

        if (place.parentId != null) {
            val parentPlace = findById(place.parentId!!)
            parentPlace?.children?.add(place)
        } else {
            places.add(place)
        }
    }

    fun updateLocation(placeForMove: Place, newParent: Place?) {
        val newParentId = newParent?.id
        val oldParentId = placeForMove.parentId
        if (oldParentId == newParentId) {
            return
        }

        val dto = UpdatePlace(placeForMove.name, newParentId)

        val url = "http://10.0.2.2:8080/places/${placeForMove.id}"
        val request: Request = Request.Builder()
            .url(url)
            .put(gson.toJson(dto).toRequestBody(JSON))
            .build()
        client.newCall(request).execute()

        if (oldParentId == null) {
            places.removeIf { it.id == placeForMove.id }
        } else {
            findById(oldParentId)?.children?.removeIf { it.id == placeForMove.id }
        }

        if (newParentId == null) {
            places.add(placeForMove)
        } else {
            findById(newParentId)?.children?.add(placeForMove)
        }

        placeForMove.parentId = newParentId
    }

    fun getLocation(placeId: String): List<Place> {
        var currentPlace = findById(placeId)!!

        val result = ArrayList<Place>()
        result.add(currentPlace)

        var currentPlaceId: String?
        while (currentPlace.parentId != null){
            currentPlaceId = currentPlace.parentId
            currentPlace = findById(currentPlaceId!!)!!
            result.add(currentPlace)
        }

        result.reverse()
        return result
    }
}