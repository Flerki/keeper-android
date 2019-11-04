package com.amairoiv.keeper.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class PlaceInfoActivity : AppCompatActivity() {

    private lateinit var place: Place
    private lateinit var placeNameTextView: TextView
    private val c: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)

        place = intent.getSerializableExtra("PLACE") as Place

        placeNameTextView = findViewById(R.id.placeName)

        findViewById<Button>(R.id.back_to_places_btn_id).setOnClickListener {
            val intent = Intent(this, DisplayPlaceActivity::class.java)
            intent.putExtra("PLACE_ID", place.id)
            startActivity(intent)
            finish()
        }

        showPlaceInfo()
    }

    override fun onResume() {
        super.onResume()
        val location = PlaceService.getLocation(place.id)

        setToolbarTitle()

        findViewById<TextView>(R.id.placeLocation).text =
            location.dropLast(1).joinToString(" -> ") { it.name }
    }

    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.place_info_toolbar_title)
        toolbarTitle.text = place.name
    }


    private fun showPlaceInfo() {
        placeNameTextView.text = place.name
    }

    fun deletePlace(view: View) {
        PlaceService.deletePlace(place.id)
        finish()
    }

    fun movePlace(view: View) {
        val intent = Intent(this, MoveActivity::class.java)
        intent.putExtra("PLACE", place)
        startActivity(intent)
    }

    fun rename(view: View) {
        val layoutInflaterAndroid = LayoutInflater.from(c)
        val mView = layoutInflaterAndroid.inflate(R.layout.name_input_dialog, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(c)
        alertDialogBuilderUserInput.setView(mView)

        val userInputDialogEditText = mView.findViewById(R.id.userInputDialog) as EditText
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = userInputDialogEditText.text.toString()
                val updatedPlace = Place(place.id, newName, place.children, place.parentId)
                PlaceService.update(updatedPlace)
                place.name = newName
                placeNameTextView.text = place.name
            }
            .setNegativeButton(
                "Отменить"
            ) { dialogBox, _ -> dialogBox.cancel() }

        val alertDialogAndroid = alertDialogBuilderUserInput.create()
        alertDialogAndroid.show()
    }
}
