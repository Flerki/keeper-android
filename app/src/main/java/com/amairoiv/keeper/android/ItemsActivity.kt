package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.PlaceService

class ItemsActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private lateinit var extras: Bundle
    private lateinit var place: Place
    private lateinit var adapter: ItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        extras = intent.extras!!
        place = extras.getSerializable("PLACE") as Place
        adapter = ItemAdapter(this, items)
        showItemsView()

    }

    override fun onResume() {
        super.onResume()

        val items = PlaceService.getItemForPlace(place.id)
        this.items.clear()
        this.items.addAll(items)
        adapter.notifyDataSetChanged()

        findViewById<TextView>(R.id.noItemsText).visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showItemsView() {

        val listView = findViewById<ListView>(R.id.items)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]

            val intent = Intent(this, ItemInfoActivity::class.java)
            intent.putExtra("ITEM", item)
            intent.putExtra("PLACE", place)

            startActivity(intent)
        }
    }
}
