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

    private var placeForMove: Place? = null

    private var currentPlaceStack: Stack<Place?> = Stack()
    private var placeListsStack: Stack<ListView> = Stack()

    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move)

        placeForMove = intent.extras?.getSerializable("PLACE") as Place?

        show()
    }

    private fun show() {
        val lLayout = LinearLayout(this)
        lLayout.orientation = LinearLayout.VERTICAL
        lLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        val moveButton = Button(this)
        moveButton.text = "Переместить"
        moveButton.setOnClickListener {
            val currentPlace = currentPlaceStack.pop()
            PlaceService.updateLocation(placeForMove!!, currentPlace)
            finish()
        }
        lLayout.addView(moveButton)

        backButton = Button(this)
        backButton.isEnabled = false
        backButton.text = "Назад"
        backButton.setOnClickListener {
            currentPlaceStack.pop()
            placeListsStack.pop().visibility = View.GONE
            placeListsStack.peek().visibility = View.VISIBLE

            if (placeListsStack.size == 1) {
                backButton.isEnabled = false
            }
        }
        lLayout.addView(backButton)

        val cancelButton = Button(this)
        cancelButton.text = "Отменить"
        cancelButton.setOnClickListener { finish() }
        lLayout.addView(cancelButton)

        currentPlaceStack.push(null)
        val root = PlaceService.getRoot()
        addListViewForPlaces(root, lLayout)

        setContentView(lLayout)
    }

    private fun addListViewForPlaces(places: MutableList<Place>, layout: LinearLayout) {
        val view = ListView(this)
        placeListsStack.push(view)

        view.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
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
        }

        layout.addView(view)
    }

}
