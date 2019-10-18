package com.amairoiv.keeper.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

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

        val listView = findViewById<ListView>(R.id.listViewExample)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView.setAdapter(arrayAdapter)

        listView.setOnItemClickListener { adapterView, _, index, _ ->
            val clickItemObj = adapterView.adapter.getItem(index)
            Toast.makeText(
                this@MainActivity,
                "You clicked $clickItemObj",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
