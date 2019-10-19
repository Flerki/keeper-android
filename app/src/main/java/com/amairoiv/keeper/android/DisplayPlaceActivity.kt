package com.amairoiv.keeper.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class DisplayPlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_place)
        showPlacesView()
    }

    private fun showPlacesView() {
        val dataList = ArrayList<String>()
        dataList.add("Место 1")
        dataList.add("Место 2")
        dataList.add("Место 3")
        dataList.add("Место 4")

        val listView = findViewById<ListView>(R.id.places)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, _, _ ->
            val intent = Intent(this, DisplayPlaceActivity::class.java)
            startActivity(intent)
        }
    }
}
