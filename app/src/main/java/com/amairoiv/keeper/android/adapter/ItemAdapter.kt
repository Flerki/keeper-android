package com.amairoiv.keeper.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.amairoiv.keeper.android.R
import com.amairoiv.keeper.android.model.Item

class ItemAdapter(
    private val context: Context,
    private val dataSource: MutableList<Item>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_item, parent, false)

        val item = getItem(position) as Item

        val titleTextView = rowView.findViewById(R.id.item_list_title) as TextView
        titleTextView.text = item.name

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}