package com.amairoiv.keeper.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place

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
}
