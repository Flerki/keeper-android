package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService

class ItemListActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private lateinit var adapter: ItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_item_list)
        showItemsView()
    }

    override fun onResume() {
        super.onResume()
        this.items.clear()
        val user = UserService.getUser()
        val items = ItemService.getAll(user)
        this.items.addAll(items)
        adapter.notifyDataSetChanged()
    }

    private fun showItemsView() {

        val listView = findViewById<ListView>(R.id.itemList)

        adapter = ItemAdapter(this, items)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]

            val intent = Intent(this, ItemInfoActivity::class.java)
            intent.putExtra("ITEM", item)
            intent.putExtra("PLACE", PlaceService.findById(item.placeId))

            startActivity(intent)
        }
    }
}
