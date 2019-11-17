package com.amairoiv.keeper.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import com.amairoiv.keeper.android.adapter.ItemAdapter
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.UserService


class ItemListActivity : AppCompatActivity() {

    private val items: MutableList<Item> = ArrayList()
    private val filteredItems: MutableList<Item> = ArrayList()
    private val selectedItems: MutableList<Item> = ArrayList()

    private lateinit var adapter: ItemAdapter
    private lateinit var searchInput: EditText

    private lateinit var moveItem: MenuItem
    private lateinit var deleteItem: MenuItem
    private lateinit var cancelItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_list)
        val toolbar = findViewById<Toolbar>(R.id.items_list_toolbar)
        setSupportActionBar(toolbar)

        val toolbarTitle = findViewById<TextView>(R.id.item_list_back_toolbar_title)
        toolbarTitle.text = "Вещи"

        searchInput = findViewById(R.id.search)
        searchInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(value: Editable) {
                filterItems()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                clearSelectedItems()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        searchInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                clearSelectedItems()
            }
        }

        showItemsView()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_list_menu, menu)
        moveItem = menu!!.findItem(R.id.item_list_menu_move)
        deleteItem = menu.findItem(R.id.item_list_menu_delete)
        cancelItem = menu.findItem(R.id.item_list_menu_cancel)
        if (selectedItems.isEmpty()) {
            changeMenuItemsVisibility(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_list_menu_move -> {
                val intent = Intent(this, MoveItemActivity::class.java)
                intent.putExtra("ITEMS", selectedItems.toTypedArray())
                clearSelectedItems()
                startActivity(intent)
            }
            R.id.item_list_menu_delete -> {
                selectedItems.map { ItemService.deleteItem(it.id) }
                clearSelectedItems()
                val listView = findViewById<ListView>(R.id.itemList)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
                onResume()
                adapter.notifyDataSetChanged()
            }
            R.id.item_list_menu_cancel -> {
                clearSelectedItems()
                val listView = findViewById<ListView>(R.id.itemList)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
            }
        }

        return super.onOptionsItemSelected(item)
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

    private fun clearSelectedItems() {
        selectedItems.clear()
        searchInput.isFocusable = true
        changeMenuItemsVisibility(false)
    }

    private fun changeMenuItemsVisibility(isVisible: Boolean) {
        moveItem.isVisible = isVisible
        deleteItem.isVisible = isVisible
        cancelItem.isVisible = isVisible
    }

    private fun showItemsView() {

        val listView = findViewById<ListView>(R.id.itemList)

        adapter = ItemAdapter(this, filteredItems)

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]

            val intent = Intent(this, ItemInfoActivity::class.java)
            intent.putExtra("ITEM", item)
            intent.putExtra("PREV_ACTIVITY", "ItemListActivity")

            startActivity(intent)
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
                searchInput.isFocusable = false
                changeMenuItemsVisibility(true)
            } else {
                searchInput.isFocusable = true
                changeMenuItemsVisibility(false)
            }

            true
        }
    }

    fun back(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
