package com.amairoiv.keeper.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.model.Item
import com.amairoiv.keeper.android.model.Place
import com.amairoiv.keeper.android.service.ItemService
import com.amairoiv.keeper.android.service.PlaceService

class ItemInfoActivity : AppCompatActivity() {

    private lateinit var place: Place
    private lateinit var item: Item
    private lateinit var placeNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)
        showItemInfo()
    }

    private fun showItemInfo() {
        item = intent.getSerializableExtra("ITEM") as Item

        placeNameTextView = findViewById(R.id.itemName)
        placeNameTextView.text = item.name

        place = intent.getSerializableExtra("PLACE") as Place
        val location =  PlaceService.getLocation(place.id)

        findViewById<TextView>(R.id.itemLocation).text =
            location.joinToString(" -> ") { it.name }
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
}
