package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService
import android.text.Editable
import android.text.TextWatcher


class ItemListActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private val filteredItems: MutableList<Item> = ArrayList()

    private lateinit var adapter: ItemAdapter
    private lateinit var searchInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_item_list)

        searchInput = findViewById(R.id.search)
        searchInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(value: Editable) {
                filterItems()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        showItemsView()
    }


    override fun onResume() {
        super.onResume()
        this.items.clear()
        val user = UserService.getUser()
        val items = ItemService.getAll(user)
        this.items.addAll(items)

        filterItems()
    }

    private fun filterItems() {
        val strForSearch = searchInput.text

        filteredItems.clear()

        if (strForSearch.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            items.filter { it.name.contains(strForSearch, true) }
                .forEach { filteredItems.add(it) }
        }
        adapter.notifyDataSetChanged()
    }

    private fun showItemsView() {

        val listView = findViewById<ListView>(R.id.itemList)

        adapter = ItemAdapter(this, filteredItems)

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
