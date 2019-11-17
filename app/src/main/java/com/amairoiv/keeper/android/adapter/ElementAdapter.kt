package com.amairoiv.keeper.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.amairoiv.keeper.android.R
import com.amairoiv.keeper.android.model.Element

class ElementAdapter(
    private val context: Context,
    private val dataSource: MutableList<Element>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_base_element, parent, false)

        val element = getItem(position) as Element

        val titleTextView = rowView.findViewById(R.id.base_element_list_title) as TextView
        val folderImageView = rowView.findViewById(R.id.ic_box) as ImageView
        if (!element.isPlace) {
            folderImageView.visibility = View.GONE
        }
        titleTextView.text = element.name

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