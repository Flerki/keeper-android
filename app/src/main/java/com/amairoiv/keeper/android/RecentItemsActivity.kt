package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.UserService

class RecentItemsActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_items)

        val toolbar = findViewById<Toolbar>(R.id.recent_items_toolbar)
        setSupportActionBar(toolbar)

        val toolbarTitle = findViewById<TextView>(R.id.recent_items_back_toolbar_title)
        toolbarTitle.text = "Недавние вещи"

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
            intent.putExtra("PREV_ACTIVITY", "RecentItemsActivity")

            startActivity(intent)
        }
    }

    fun back(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
