package com.amairoiv.keeper.android

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService


class ItemInfoActivity : AppCompatActivity() {

    private lateinit var item: Item
    private lateinit var placeNameTextView: TextView
    private lateinit var prevActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)
        val toolbar = findViewById<Toolbar>(R.id.item_info_toolbar)
        setSupportActionBar(toolbar)
        showItemInfo()

        UserService.addToRecent(item)
    }

    override fun onResume() {
        super.onResume()

        item = ItemService.get(item.id)
        val location =  PlaceService.getLocation(item.placeId)

        setToolbarTitle()

        findViewById<TextView>(R.id.itemLocation).text =
            location.joinToString(" -> ") { it.name }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.item_info_menu, menu)

        menu?.add(0, R.id.item_info_menu_rename, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_edit), "Переименовать"))
        menu?.add(0, R.id.item_info_menu_move, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_go_to), "Переместить"))
        menu?.add(0, R.id.item_info_menu_delete, 1, menuIconWithText(resources.getDrawable(R.drawable.ic_delete), "Удалить"))
        return true
    }

    private fun menuIconWithText(r: Drawable, title: String): CharSequence {

        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("  $title")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return sb
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_info_menu_rename -> {
                rename()
            }
            R.id.item_info_menu_move -> {
                move()
            }
            R.id.item_info_menu_delete -> {
                deleteItem()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun showItemInfo() {
        item = intent.getSerializableExtra("ITEM") as Item
        prevActivity = intent.getSerializableExtra("PREV_ACTIVITY") as String

        placeNameTextView = findViewById(R.id.itemName)
        placeNameTextView.text = item.name
    }


    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.item_info_toolbar_title)
        toolbarTitle.text = item.name
    }


    fun deleteItem() {
        ItemService.deleteItem(item.id)
        finish()
    }

    fun rename() {
        val layoutInflaterAndroid = LayoutInflater.from(this)
        val mView = layoutInflaterAndroid.inflate(R.layout.name_input_dialog, null)
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput.setView(mView)

        val userInputDialogEditText = mView.findViewById(R.id.userInputDialog) as EditText
        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = userInputDialogEditText.text.toString()
                ItemService.rename(item, newName)
                placeNameTextView.text = item.name
            }
            .setNegativeButton(
                "Отменить"
            ) { dialogBox, _ -> dialogBox.cancel() }

        val alertDialogAndroid = alertDialogBuilderUserInput.create()
        alertDialogAndroid.show()
    }

    fun move() {
        val intent = Intent(this, MoveItemActivity::class.java)
        intent.putExtra("ITEMS", listOf(item).toTypedArray())
        startActivity(intent)
    }

    fun goToLocation(view: View) {
        val intent = Intent(this, DisplayPlaceActivity::class.java)
        intent.putExtra("PLACE_ID", item.placeId)
        startActivity(intent)
    }

    fun back(view: View) {
        when (prevActivity) {
            "DisplayPlaceActivity" -> {
                val placeIntent = Intent(this, DisplayPlaceActivity::class.java)
                placeIntent.putExtra("PLACE_ID", item.placeId)
                startActivity(placeIntent)
            }
            "ItemListActivity", "RecentItemsActivity", "MainActivity" -> {
                finish()
            }
        }

    }
}
