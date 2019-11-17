package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.dto.CreateItem
import com.amairoiv.keeper.android.service.ItemService
import kotlinx.android.synthetic.main.activity_create_item.*

class CreateItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        val toolbarTitle = findViewById<TextView>(R.id.create_item_toolbar_title)
        toolbarTitle.text = "Добавление вещи"
    }

    fun createItem(view: View) {
        val itemName = enterItemName.text.toString()
        val placeId = intent.extras?.get("PLACE_ID") as String
        val createItem = CreateItem(
            itemName,
            placeId
        )
        ItemService.createItem(createItem)
        finish()
    }

    fun finish(view: View) {
        finish()
    }
}
