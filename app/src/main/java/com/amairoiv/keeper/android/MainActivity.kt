package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arrayAdapterListView()
    }

    private fun arrayAdapterListView() {
        title = "Keeper"

        val dataList = ArrayList<String>()
        dataList.add("Места")
        dataList.add("Вещи")

        val placesIndex = 0

        val listView = findViewById<ListView>(R.id.listViewExample)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, index, _ ->
            if (index == placesIndex){
                val intent = Intent(this, DisplayPlaceActivity::class.java)
                startActivity(intent)
            }
        }

    }
}
