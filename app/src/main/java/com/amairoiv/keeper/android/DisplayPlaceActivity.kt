package com.amairoiv.keeper.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import com.amairoiv.keeper.android.adapter.BaseElementAdapter
import com.amairoiv.keeper.android.model.BaseElement
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService

class DisplayPlaceActivity : AppCompatActivity() {

    private var adapter: BaseElementAdapter? = null
    private var placeId: String? = null
    private var place: Place? = null

    private lateinit var searchInput: EditText

    private val elements: MutableList<BaseElement> = ArrayList()
    private val filteredElements: MutableList<BaseElement> = ArrayList()
    private val selectedElements: MutableList<BaseElement> = ArrayList()

    private lateinit var addPlace: MenuItem
    private lateinit var moveElement: MenuItem
    private lateinit var deleteElement: MenuItem
    private lateinit var cancelElement: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_place)

        placeId = intent.extras?.getString("PLACE_ID")

        if (placeId != null){
            place = PlaceService.findById(placeId!!)
        }

        if (place == null) {
            hideItemsButtons()
        }
        val toolbar = findViewById<Toolbar>(R.id.places_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        findViewById<Button>(R.id.parent_place_btn_id).setOnClickListener {
            val intent = if (place == null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, DisplayPlaceActivity::class.java)
                intent.putExtra("PLACE_ID", PlaceService.findById(place!!.id)?.parentId)
            }
            startActivity(intent)
            finish()
        }

        showPlacesAndItemsView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.place_menu, menu)
        addPlace = menu!!.findItem(R.id.place_menu_add)
        if (place == null) {
            menu.findItem(R.id.place_menu_add_item).isVisible = false
        }
        moveElement = menu.findItem(R.id.place_menu_move)
        deleteElement = menu.findItem(R.id.place_menu_delete)
        cancelElement = menu.findItem(R.id.place_menu_cancel)
        if (selectedElements.isEmpty()) {
            changeMenuItemsVisibility(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.place_menu_add_place -> {
                val intent = Intent(this, CreatePlaceActivity::class.java)
                intent.putExtra("PARENT_PLACE_ID", placeId)
                startActivity(intent)
            }
            R.id.place_menu_add_item -> {
                val intent = Intent(this, CreateItemActivity::class.java)
                intent.putExtra("PLACE_ID", place!!.id)
                startActivity(intent)
            }
            R.id.place_menu_move -> {
                val intent = Intent(this, MoveBaseElementActivity::class.java)
                intent.putExtra("ELEMENTS", selectedElements.toTypedArray())
                clearSelectedElements()
                startActivity(intent)
            }
            R.id.place_menu_delete -> {
                selectedElements.map {
                    if (it is Place) {
                        PlaceService.deletePlace(it.id)
                    } else {
                        ItemService.deleteItem(it.id)
                    }
                }
                clearSelectedElements()
                val listView = findViewById<ListView>(R.id.base_elements)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
                onResume()
                adapter?.notifyDataSetChanged()
            }
            R.id.place_menu_cancel -> {
                clearSelectedElements()
                val listView = findViewById<ListView>(R.id.base_elements)
                listView.forEach { it.setBackgroundColor(Color.parseColor("#FAFAFA")) }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        this.elements.clear()

        if (placeId == null) {
            this.elements.addAll(PlaceService.getRoot().toMutableList())
        } else {
            this.elements.addAll(place!!.children.toMutableList())
            val items = PlaceService.getItemForPlace(place!!.id)
            this.elements.addAll(items)
        }

        initSearchInput()

        if (placeId != null && !PlaceService.exists(placeId!!)) {
            finish()
        } else {
            hideOrShowNoChildrenText()
            setToolbarTitle()
            setParentButtonTitle()
        }

        filterElements()
    }

    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.places_toolbar_title)
        toolbarTitle.text = if (placeId == null) "Места" else place!!.name
    }

    private fun setParentButtonTitle() {
        val parentButton = findViewById<Button>(R.id.parent_place_btn_id)
        place?.let { PlaceService.findById(it.id)?.parentId?.let { parentId -> parentButton.text = PlaceService.findById(parentId)?.name ?: ""} }
    }

    private fun hideOrShowNoChildrenText() {
        val textView = findViewById<TextView>(R.id.noChildrenText)

        if ((placeId == null && PlaceService.getRoot().isEmpty()) || (placeId != null
            && PlaceService.findById(placeId!!)!!.children.isEmpty() && PlaceService.getItemForPlace(placeId!!).isEmpty())) {
            textView.text = "Пусто :("
            textView.visibility = View.VISIBLE
            searchInput.visibility = View.GONE
            hideItemsButtons()
        } else {
            textView.visibility = View.GONE
        }
    }


    private fun initSearchInput() {
        searchInput = findViewById(R.id.base_elements_search)
        searchInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(value: Editable) {
                filterElements()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                clearSelectedElements()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        searchInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                clearSelectedElements()
            }
        }
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
        adapter!!.notifyDataSetChanged()
    }

    private fun hideItemsButtons() {
        findViewById<Button>(R.id.itemsBtn).visibility = View.GONE
    }

    private fun showPlacesAndItemsView() {

        val listView = findViewById<ListView>(R.id.base_elements)

        adapter = BaseElementAdapter(this, filteredElements)

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            val nextElement = elements[position]

            if (nextElement is Place) {
                val placeIntent = Intent(this, DisplayPlaceActivity::class.java)
                placeIntent.putExtra("PLACE_ID", nextElement.id)
                startActivity(placeIntent)
            } else {
                val itemsIntent = Intent(this, ItemInfoActivity::class.java)
                itemsIntent.putExtra("ITEM", nextElement as Item)
                itemsIntent.putExtra("PLACE", place)

                startActivity(itemsIntent)
            }
        }

        listView.setOnItemLongClickListener { _, view, position, _ ->
            val item = elements[position]

            if (selectedElements.contains(item)) {
                selectedElements.remove(item)
                view.setBackgroundColor(Color.parseColor("#FAFAFA"))
            } else {
                selectedElements.add(item)
                view.setBackgroundColor(Color.parseColor("#00E6CF"))
            }

            if (selectedElements.isNotEmpty()) {
                searchInput.isFocusable = false
                changeMenuItemsVisibility(true)
            } else {
                searchInput.isFocusable = true
                changeMenuItemsVisibility(false)
            }

            true
        }
    }

    private fun clearSelectedElements() {
        selectedElements.clear()
        searchInput.isFocusable = true
        changeMenuItemsVisibility(false)
    }

    private fun changeMenuItemsVisibility(isVisible: Boolean) {
        addPlace.isVisible = !isVisible
        moveElement.isVisible = isVisible
        deleteElement.isVisible = isVisible
        cancelElement.isVisible = isVisible
    }


    fun showItems(view: View) {
        val itemsIntent = Intent(this, ItemsActivity::class.java)
        itemsIntent.putExtra("PLACE", place)
        startActivity(itemsIntent)
    }

    fun showPlaceInfo(view: View) {
        val placeInfoIntent = Intent(this, PlaceInfoActivity::class.java)
        placeInfoIntent.putExtra("PLACE", place)
        startActivity(placeInfoIntent)
    }
}
