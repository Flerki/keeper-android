package com.amairoiv.keeper.android

import android.content.Context
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
    private lateinit var renameBtn: Button
    private val c: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)

        place = intent.extras?.get("PLACE") as Place

        renameBtn = findViewById(R.id.placeInfoRenameBtn)
        placeNameTextView = findViewById(R.id.placeName)

        renameBtn.setOnClickListener {
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

        showPlaceInfo()
    }

    private fun showPlaceInfo() {
        placeNameTextView.text = place.name

        val location = (intent.extras?.getSerializable("LOCATION")  as Array<String>?)!!

        findViewById<TextView>(R.id.placeLocation).text = location.copyOf(location.size - 1).joinToString(" -> ")
    }

    fun deletePlace(view: View) {
        val placeInfo = intent.extras?.get("PLACE") as Place
        PlaceService.deletePlace(placeInfo.id)
        finish()
    }
}
