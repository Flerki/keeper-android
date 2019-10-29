package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class DisplayPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_place)

        if (intent.extras?.get("PLACE_ID") == null) {
            hideInfoAndItemsButtons()
        }
        showPlacesView()
    }

    override fun onResume() {
        super.onResume()
        val placeId = intent.extras?.getString("PLACE_ID")
        if (placeId != null && !PlaceService.exists(placeId)) {
            finish()
        }
    }

    private fun hideInfoAndItemsButtons() {
        findViewById<Button>(R.id.infoPlaceBtn).visibility = View.GONE
        findViewById<Button>(R.id.itemsBtn).visibility = View.GONE
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
            intent.putExtra("PLACE", nextPlaces)
            intent.putExtra("PLACE_ID", nextPlaces.id)
            intent.putExtra("PLACES", nextPlaces.children.toTypedArray())

            var location = this.intent.extras?.getSerializable("LOCATION") as Array<String>?
            if (location == null) {
                location = arrayOf()
            }
            location += nextPlaces.name

            intent.putExtra("LOCATION", location)

            startActivity(intent)
        }
    }

    fun showItems(view: View) {
        val placeId = intent.getStringExtra("PLACE_ID")
        val items = PlaceService.getItemForPlace(placeId!!)

        val itemsIntent = Intent(this, ItemsActivity::class.java)
        itemsIntent.putExtra("ITEMS", items.toTypedArray())
        startActivity(itemsIntent)
    }

    fun showPlaceInfo(view: View) {
        val place = intent.getSerializableExtra("PLACE")
        val location = intent.getSerializableExtra("LOCATION")

        val placeInfoIntent = Intent(this, PlaceInfoActivity::class.java)
        placeInfoIntent.putExtra("PLACE", place)
        placeInfoIntent.putExtra("LOCATION", location)
        startActivity(placeInfoIntent)
    }
}
