package com.amairoiv.keeper.android.model

import java.io.Serializable

data class Item(val id: String, var name: String, var placeId: String) : Serializable
