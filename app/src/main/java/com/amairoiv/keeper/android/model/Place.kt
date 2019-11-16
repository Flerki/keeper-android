package com.amairoiv.keeper.android.model

open class Place(id: String, name: String, val children: MutableList<Place>, var parentId: String?) : BaseElement(id, name)
