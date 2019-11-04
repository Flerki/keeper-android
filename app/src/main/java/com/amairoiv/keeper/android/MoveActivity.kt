package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.PlaceAdapter
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService
import java.util.*

class MoveActivity : AppCompatActivity() {

    private lateinit var placeForMove: Place

    private var currentPlaceStack: Stack<Place?> = Stack()
    private var placeListsStack: Stack<ListView> = Stack()

    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_move)

        placeForMove = intent.getSerializableExtra("PLACE") as Place

        show()
    }

    private fun show() {
        val lLayout = findViewById<LinearLayout>(R.id.layout)

        backButton = this.findViewById(R.id.backBtn)
        backButton.isEnabled = false

        currentPlaceStack.push(null)
        val root = PlaceService.getRoot()
        addListViewForPlaces(root, lLayout)
    }

    private fun addListViewForPlaces(places: MutableList<Place>, layout: LinearLayout) {
        val placesCopy = mutableListOf<Place>()
        placesCopy.addAll(places)
        placesCopy.removeIf { it.id == placeForMove.id }

        val view = ListView(this)
        placeListsStack.push(view)

        view.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        val adapter = PlaceAdapter(this, placesCopy)
        view.adapter = adapter

        view.setOnItemClickListener { _, _, position, _ ->
            view.visibility = View.GONE
            val chosenPlace = placesCopy[position]
            currentPlaceStack.push(chosenPlace)

            val children = chosenPlace.children
            addListViewForPlaces(children, layout)

            backButton.isEnabled = true
        }

        layout.addView(view)
    }

    fun move(view: View) {
        val currentPlace = currentPlaceStack.pop()
        PlaceService.updateLocation(placeForMove, currentPlace)
        finish()
    }

    fun back(view: View) {
        currentPlaceStack.pop()
        placeListsStack.pop().visibility = View.GONE
        placeListsStack.peek().visibility = View.VISIBLE

        if (placeListsStack.size == 1) {
            backButton.isEnabled = false
        }
    }

    fun cancel(view: View) {
        finish()
    }

}
