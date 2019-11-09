package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.UserService

class RecentItemsActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_recent_items)
        title = "Недавние вещи"

        adapter = ItemAdapter(this, items)
        showItemsView()
    }

    override fun onResume() {
        super.onResume()
        val recentItems = UserService.getRecentItems()

        this.items.clear()
        this.items.addAll(recentItems)
        adapter.notifyDataSetChanged()
    }

    private fun showItemsView() {
        val listView = findViewById<ListView>(R.id.items)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]

            val intent = Intent(this, ItemInfoActivity::class.java)
            intent.putExtra("ITEM", item)

            startActivity(intent)
        }
    }
}
