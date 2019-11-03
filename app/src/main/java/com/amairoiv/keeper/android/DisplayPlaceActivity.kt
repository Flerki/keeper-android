package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class DisplayPlaceActivity : AppCompatActivity() {

    private var adapter: PlaceAdapter? = null
    private var placeId: String? = null
    private var place: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_place)

        placeId = intent.extras?.getString("PLACE_ID")

        if (placeId != null){
            place = PlaceService.findById(placeId!!)
        }

        if (place == null) {
            hideInfoAndItemsButtons()
        }
        setSupportActionBar(findViewById(R.id.places_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        showPlacesView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.place_menu, menu)
        if (place == null) {
            menu?.findItem(R.id.place_menu_move)?.isVisible = false
            menu?.findItem(R.id.place_menu_delete)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.place_menu_add -> {
                val intent = Intent(this, CreatePlaceActivity::class.java)
                intent.putExtra("PARENT_PLACE_ID", placeId)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()

        if (placeId != null && !PlaceService.exists(placeId!!)) {
            finish()
        } else {
            hideOrShowNoChildrenText()
            setToolbarTitle()
        }
    }

    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.places_toolbar_title)
        toolbarTitle.text = if (placeId == null) "Места" else place!!.name
    }

    private fun hideOrShowNoChildrenText() {
        val textView = findViewById<TextView>(R.id.noChildrenText)

        if (placeId == null && PlaceService.getRoot().isEmpty()) {
            textView.text = "Нет ни одного места"
            textView.visibility = View.VISIBLE
        } else if (placeId != null && PlaceService.findById(placeId!!)!!.children.isEmpty()) {
            textView.text = "Нет ни одного вложенного места"
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun hideInfoAndItemsButtons() {
        findViewById<Button>(R.id.infoPlaceBtn).visibility = View.GONE
        findViewById<Button>(R.id.itemsBtn).visibility = View.GONE
    }

    private fun showPlacesView() {
        val places: MutableList<Place>

        val listView = findViewById<ListView>(R.id.places)

        if (placeId == null) {
            places = PlaceService.getRoot()
        } else {
            places = place!!.children
        }

        adapter = PlaceAdapter(this, places)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            val nextPlace = places[position]

            val intent = Intent(this, DisplayPlaceActivity::class.java)
            intent.putExtra("PLACE_ID", nextPlace.id)

            var location = this.intent.extras?.getSerializable("LOCATION") as Array<String>?
            if (location == null) {
                location = arrayOf()
            }
            location += nextPlace.name

            intent.putExtra("LOCATION", location)

            startActivity(intent)
        }
    }

    fun showItems(view: View) {

        val itemsIntent = Intent(this, ItemsActivity::class.java)

        itemsIntent.putExtra("PLACE", place)

        val items = PlaceService.getItemForPlace(placeId!!)
        itemsIntent.putExtra("ITEMS", items.toTypedArray())

        val location = this.intent.extras?.getSerializable("LOCATION") as Array<String>?

        itemsIntent.putExtra("LOCATION", location)

        startActivity(itemsIntent)
    }

    fun showPlaceInfo(view: View) {
        val location = intent.getSerializableExtra("LOCATION")

        val placeInfoIntent = Intent(this, PlaceInfoActivity::class.java)
        placeInfoIntent.putExtra("PLACE", place)
        placeInfoIntent.putExtra("LOCATION", location)
        startActivity(placeInfoIntent)
    }
}
