package com.amairoiv.keeper.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService

class ItemsActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private val selectedItems: MutableList<Item> = ArrayList()

    private lateinit var extras: Bundle
    private lateinit var place: Place
    private lateinit var adapter: ItemAdapter

    private lateinit var moveItem: MenuItem
    private lateinit var deleteItem: MenuItem
    private lateinit var cancelItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        extras = intent.extras!!
        place = extras.getSerializable("PLACE") as Place
        adapter = ItemAdapter(this, items)
        setSupportActionBar(findViewById(R.id.items_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        showItemsView()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items_menu, menu)
        moveItem = menu!!.findItem(R.id.items_menu_move)
        deleteItem = menu.findItem(R.id.items_menu_delete)
        cancelItem = menu.findItem(R.id.items_menu_cancel)
        if (selectedItems.isEmpty()) {
            changeMenuItemsVisibility(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.items_menu_add -> {
                val intent = Intent(this, CreateItemActivity::class.java)
                intent.putExtra("PLACE_ID", place.id)
                startActivity(intent)
            }
            R.id.items_menu_move -> {
                val intent = Intent(this, MoveItemActivity::class.java)
                intent.putExtra("ITEMS", selectedItems.toTypedArray())
                clearSelectedItems()
                startActivity(intent)
            }
            R.id.items_menu_delete -> {
                selectedItems.map { ItemService.deleteItem(it.id) }
                clearSelectedItems()
                val listView = findViewById<ListView>(R.id.items)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
                onResume()
                adapter.notifyDataSetChanged()
            }
            R.id.items_menu_cancel -> {
                clearSelectedItems()
                val listView = findViewById<ListView>(R.id.items)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun changeMenuItemsVisibility(isVisible: Boolean) {
        moveItem.isVisible = isVisible
        deleteItem.isVisible = isVisible
        cancelItem.isVisible = isVisible
    }

    private fun clearSelectedItems() {
        selectedItems.clear()
        changeMenuItemsVisibility(false)
    }


    override fun onResume() {
        super.onResume()

        val items = PlaceService.getItemForPlace(place.id)
        this.items.clear()
        this.items.addAll(items)
        adapter.notifyDataSetChanged()
        setToolbarTitle()
        setParentButtonTitle()
        findViewById<TextView>(R.id.noItemsText).visibility = if (items.isEmpty()) View.VISIBLE else View.GONE

        findViewById<Button>(R.id.back_to_place_btn_id).setOnClickListener {
            val intent = Intent(this, DisplayPlaceActivity::class.java)
            intent.putExtra("PLACE_ID", place.id)
            startActivity(intent)
            finish()
        }
    }

    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.items_toolbar_title)
        toolbarTitle.text = "Вещи в ${place.name}"
    }

    private fun setParentButtonTitle() {
        val backButton = findViewById<Button>(R.id.back_to_place_btn_id)
        backButton.text = place.name
    }

    private fun showItemsView() {

        val listView = findViewById<ListView>(R.id.items)

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            if (selectedItems.isNotEmpty()) {
                val item = items[position]

                val intent = Intent(this, ItemInfoActivity::class.java)
                intent.putExtra("ITEM", item)
                intent.putExtra("PLACE", place)

                startActivity(intent)
            }
        }

        listView.setOnItemLongClickListener { _, view, position, _ ->
            val item = items[position]

            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                view.setBackgroundColor(Color.parseColor("#FAFAFA"))
            } else {
                selectedItems.add(item)
                view.setBackgroundColor(Color.parseColor("#00E6CF"))
            }

            if (selectedItems.isNotEmpty()) {
                changeMenuItemsVisibility(true)
            } else {
                changeMenuItemsVisibility(false)
            }

            true
        }
    }
}
