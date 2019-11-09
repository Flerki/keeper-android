package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import java.util.*

class MoveItemActivity : AppCompatActivity() {

    private lateinit var itemForMove: List<Item>

    private var currentPlaceStack: Stack<Place?> = Stack()
    private var placeListsStack: Stack<ListView> = Stack()

    private lateinit var backButton: Button
    private lateinit var moveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_move)

        itemForMove = (intent.getSerializableExtra("ITEMS") as Array<Item>).toList()

        show()
        refreshLocation()
    }

    private fun show() {
        val lLayout = findViewById<LinearLayout>(R.id.layout)

        backButton = findViewById(R.id.backBtn)
        backButton.isEnabled = false

        moveButton = findViewById(R.id.moveBtn)
        moveButton.isEnabled = false

        currentPlaceStack.push(null)
        val root = PlaceService.getRoot()
        addListViewForPlaces(root, lLayout)
    }

    private fun addListViewForPlaces(places: MutableList<Place>, layout: LinearLayout) {
        val view = ListView(this)
        placeListsStack.push(view)

        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val adapter = PlaceAdapter(this, places)
        view.adapter = adapter

        view.setOnItemClickListener { _, _, position, _ ->
            view.visibility = View.GONE
            val choosenPlace = places[position]
            currentPlaceStack.push(choosenPlace)

            val children = choosenPlace.children
            addListViewForPlaces(children, layout)

            backButton.isEnabled = true
            moveButton.isEnabled = true

            refreshLocation()
        }

        layout.addView(view)
    }

    fun move(view: View) {
        val currentPlace = currentPlaceStack.pop()
        itemForMove.map { item -> ItemService.move(item, currentPlace!!) }
        finish()
    }

    fun back(view: View) {
        currentPlaceStack.pop()
        placeListsStack.pop().visibility = View.GONE
        placeListsStack.peek().visibility = View.VISIBLE

        if (placeListsStack.size == 1) {
            backButton.isEnabled = false
            moveButton.isEnabled = false
        }

        refreshLocation()
    }

    private fun refreshLocation() {
        val locationWhereToMoveText = findViewById<TextView>(R.id.locationWhereToMove)

        val place = currentPlaceStack.peek()
        if (place != null) {
            val location = PlaceService.getLocation(place.id)
            locationWhereToMoveText.text = "Переместить в " + location.joinToString(" -> ") { it.name } + " ?"
        } else {
            locationWhereToMoveText.text = "Место для перемещения не выбрано"
        }
    }

    fun cancel(view: View) {
        finish()
    }
}
