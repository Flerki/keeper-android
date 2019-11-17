package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.dto.CreatePlace
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService
import kotlinx.android.synthetic.main.activity_create_place.*

class CreatePlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_place)

        val toolbarTitle = findViewById<TextView>(R.id.create_place_toolbar_title)
        toolbarTitle.text = "Добавление места"
    }


    fun createPlace(view: View) {
        val placeName = enterPlaceName.text.toString()
        val parentPlaceId = intent.extras?.get("PARENT_PLACE_ID") as String?
        val createPlace = CreatePlace(
            UserService.getUser(),
            placeName,
            parentPlaceId
        )
        PlaceService.createPlace(createPlace)
        finish()
    }

    fun finish(view: View) {
        finish()
    }
}
