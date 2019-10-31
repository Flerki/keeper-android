package com.amairoiv.keeper.android.dto

data class CreatePlace(
    val userId: String,
    val placeName: String,
    val parentPlaceId: String?
)