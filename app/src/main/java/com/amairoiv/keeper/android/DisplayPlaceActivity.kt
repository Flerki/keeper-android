package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Place

class DisplayPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_place)
        showPlacesView()
    }

    private fun showPlacesView() {
        val places = intent.extras?.get("PLACES") as Array<Place>

        val listView = findViewById<ListView>(R.id.places)

        val placesList = ArrayList(places.toList())

        val adapter = PlaceAdapter(this, placesList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val nextPlaces = places[position]

            val intent = Intent(this, DisplayPlaceActivity::class.java)
            intent.putExtra("PLACES", nextPlaces.children.toTypedArray())
            startActivity(intent)
        }
    }
}
