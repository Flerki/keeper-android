package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class PlaceInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)
        showPlaceInfo()
    }

    private fun showPlaceInfo() {
        val placeInfo = intent.extras?.get("PLACE") as Place
        findViewById<TextView>(R.id.placeName).text = placeInfo.name

        val location = (intent.extras?.getSerializable("LOCATION")  as Array<String>?)!!

        findViewById<TextView>(R.id.placeLocation).text = location.copyOf(location.size - 1).joinToString(" -> ")

    }

    fun deletePlace(view: View) {
        val placeInfo = intent.extras?.get("PLACE") as Place
        PlaceService.deletePlace(placeInfo.id)
        finish()
    }
}
