package com.amairoiv.keeper.android

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.amairoiv.keeper.android.service.PlaceService
import com.amairoiv.keeper.android.service.UserService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val SDK_INT = android.os.Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        arrayAdapterListView()
    }

    private fun arrayAdapterListView() {
        title = "Keeper"

        val dataList = ArrayList<String>()
        dataList.add("Места")
        dataList.add("Вещи")
        dataList.add("Выход")

        val placesIndex = 0
        val exitIndex = 2

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

            if (index == exitIndex){
                val intent = Intent(this, LoginActivity::class.java)
                UserService.logout()
                startActivity(intent)

                finish()
            }
        }

    }
}
