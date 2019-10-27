package com.amairoiv.keeper.android

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item

class ItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        showItemsView()
    }

    private fun showItemsView() {
        val items = intent.extras?.get("ITEMS") as Array<Item>

        val listView = findViewById<ListView>(R.id.items)

        val itemsList = ArrayList(items.toList())

        val adapter = ItemAdapter(this, itemsList)
        listView.adapter = adapter
    }
}
