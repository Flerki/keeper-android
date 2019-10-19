package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class DisplayPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_place)
        showPlacesView()
    }

    private fun showPlacesView() {
        val places = intent.extras?.get("PLACES") as Array<Place>

        val listView = findViewById<ListView>(R.id.places)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, places)
        listView.adapter = arrayAdapter


        listView.setOnItemClickListener { _, _, position, _ ->
            val nextPlaces = places[position]

            val intent = Intent(this, DisplayPlaceActivity::class.java)
            intent.putExtra("PLACES", nextPlaces.children.toTypedArray())
            startActivity(intent)
        }
    }
}
