package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.dto.CreateItem
import com.amairoiv.keeper.android.service.ItemService
import kotlinx.android.synthetic.main.activity_create_item.*

class CreateItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)
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
}
