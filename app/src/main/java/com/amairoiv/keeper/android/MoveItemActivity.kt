package com.amairoiv.keeper.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import java.util.*

class MoveItemActivity : AppCompatActivity() {

    private lateinit var itemForMove: Item

    private var currentPlaceStack: Stack<Place?> = Stack()
    private var placeListsStack: Stack<ListView> = Stack()

    private lateinit var backButton: Button
    private lateinit var moveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_move)

        itemForMove = intent.getSerializableExtra("ITEM") as Item

        show()
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
            val chosenPlace = places[position]
            currentPlaceStack.push(chosenPlace)

            val children = chosenPlace.children
            addListViewForPlaces(children, layout)

            backButton.isEnabled = true
            moveButton.isEnabled = true
        }

        layout.addView(view)
    }

    fun move(view: View) {
        val currentPlace = currentPlaceStack.pop()
        ItemService.move(itemForMove, currentPlace!!)
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
    }

    fun cancel(view: View) {
        finish()
    }
}
