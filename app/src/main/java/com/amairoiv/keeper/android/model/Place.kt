package com.amairoiv.keeper.android.model

import java.io.Serializable

data class Place(val id: String, val name: String, val children: List<Place>): Serializable{
}