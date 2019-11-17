package com.amairoiv.keeper.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.adapter.ElementAdapter
import com.amairoiv.keeper.android.model.Element
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var adapter: ElementAdapter

    private val elements: MutableList<Element> = ArrayList()
    private val filteredElements: MutableList<Element> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        findViewById<ListView>(R.id.all_elements_list).visibility = View.GONE

        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        arrayAdapterListView()

        searchInput = findViewById(R.id.global_search)
        searchInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(value: Editable) {
                filterElements()

                val menuListView = findViewById<ListView>(R.id.listViewExample)
                val elementListView = findViewById<ListView>(R.id.all_elements_list)
                if (value.isEmpty()) {
                    menuListView.visibility = View.VISIBLE
                    elementListView.visibility = View.GONE
                } else {
                    menuListView.visibility = View.GONE
                    elementListView.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        showElementList()
    }

    override fun onResume() {
        super.onResume()
        this.elements.clear()
        val elements = UserService.getAllElements()
        this.elements.addAll(elements)
    }

    private fun filterElements() {
        val strForSearch = searchInput.text

        filteredElements.clear()

        if (strForSearch.isEmpty()) {
            filteredElements.addAll(elements)
        } else {
            elements.filter { it.name.contains(strForSearch, true) }
                .forEach { filteredElements.add(it) }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_exit -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                UserService.logout()
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun arrayAdapterListView() {
        title = "Keeper"

        val dataList = ArrayList<String>()
        dataList.add("Места")
        dataList.add("Вещи")
        dataList.add("Недавно просмотренные вещи")

        val placesIndex = 0
        val itemsIndex = 1
        val recentItemsIndex = 2

        val listView = findViewById<ListView>(R.id.listViewExample)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, index, _ ->
            if (index == placesIndex) {
                val intent = Intent(this, DisplayPlaceActivity::class.java)
                val testUserId = UserService.getUser()
                PlaceService.initializePlaceHierarchyFor(testUserId)

                startActivity(intent)
            }

            if (index == itemsIndex) {
                val intent = Intent(this, ItemListActivity::class.java)
                startActivity(intent)
            }

            if (index == recentItemsIndex) {
                val intent = Intent(this, RecentItemsActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun showElementList() {
        val elementListView = findViewById<ListView>(R.id.all_elements_list)

        adapter = ElementAdapter(this, filteredElements)
        elementListView.adapter = adapter

        elementListView.setOnItemClickListener { _, _, position, _ ->
            val element = filteredElements[position]

            if (element.isPlace) {
                val placeIntent = Intent(this, DisplayPlaceActivity::class.java)
                placeIntent.putExtra("PLACE_ID", element.id)
                startActivity(placeIntent)
            } else {
                val itemsIntent = Intent(this, ItemInfoActivity::class.java)
                val item = ItemService.get(element.id)
                itemsIntent.putExtra("ITEM", item)
                itemsIntent.putExtra("PREV_ACTIVITY", "MainActivity")

                startActivity(itemsIntent)
            }
        }
    }

    fun clearSearch(view: View) {
        closeKeyBoard()
        searchInput.text.clear()
        searchInput.clearFocus()
        filterElements()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
