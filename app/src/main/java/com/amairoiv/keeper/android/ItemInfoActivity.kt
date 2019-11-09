package com.amairoiv.keeper.android

import android.content.Intent
import com.amairoiv.keeper.android.service.PlaceService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.UserService

class ItemInfoActivity : AppCompatActivity() {

    private lateinit var item: Item
    private lateinit var placeNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)
        showItemInfo()

        UserService.addToRecent(item)
    }

    private fun showItemInfo() {
        item = intent.getSerializableExtra("ITEM") as Item

        placeNameTextView = findViewById(R.id.itemName)
        placeNameTextView.text = item.name
    }

    override fun onResume() {
        super.onResume()

        item = ItemService.get(item.id)
        val location =  PlaceService.getLocation(item.placeId)

        setToolbarTitle()

        findViewById<TextView>(R.id.itemLocation).text =
            location.joinToString(" -> ") { it.name }

        findViewById<Button>(R.id.back_to_items_btn_id).setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra("PLACE", PlaceService.findById(item.placeId))
            startActivity(intent)
            finish()
        }
    }

    private fun setToolbarTitle() {
        val toolbarTitle = findViewById<TextView>(R.id.item_info_toolbar_title)
        toolbarTitle.text = item.name
    }


    fun deleteItem(view: View) {
        ItemService.deleteItem(item.id)
        finish()
    }

    fun rename(view: View) {
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

    fun move(view: View) {
        val intent = Intent(this, MoveItemActivity::class.java)
        intent.putExtra("ITEM", item)
        startActivity(intent)
    }

    fun goToLocation(view: View) {
        val intent = Intent(this, DisplayPlaceActivity::class.java)
        intent.putExtra("PLACE_ID", item.placeId)
        startActivity(intent)
    }
}
