package com.amairoiv.keeper.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService

class ItemInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)
        showItemInfo()
    }

    private fun showItemInfo() {
        val item = intent.extras?.get("ITEM") as Item
        findViewById<TextView>(R.id.itemName).text = item.name

        val location = (intent.extras?.getSerializable("LOCATION")  as Array<String>?)!!

        findViewById<TextView>(R.id.itemLocation).text = location.joinToString(" -> ")
    }

    fun deleteItem(view: View) {
        val item = intent.extras?.get("ITEM") as Item
        ItemService.deleteItem(item.id)
        finish()
    }
}
