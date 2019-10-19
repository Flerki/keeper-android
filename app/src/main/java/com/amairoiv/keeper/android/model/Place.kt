package com.amairoiv.keeper.android.model

data class Place(val id: String, val name: String, val children: List<Place>){
}