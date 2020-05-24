package com.example.ulmoto.detail

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.ulmoto.R
import com.example.ulmoto.persister.RepairEntity
import java.text.SimpleDateFormat


/**
 * Created by Kamil Macek on 23.5.2020.
 */
class RepairAdapter(private var activity: Activity, private var repairs: ArrayList<RepairEntity>) :
    BaseAdapter() {

    private class ViewHolder(row: View?) {
        var mTextViewDescription: TextView? = null
        var mTextViewDateOfRepair: TextView? = null
        var mTextViewPrice: TextView? = null

        init {
            this.mTextViewDescription = row?.findViewById(R.id.textView_detail_list_description)
            this.mTextViewDateOfRepair = row?.findViewById(R.id.textView_detail_list_date_of_repair)
            this.mTextViewPrice = row?.findViewById(R.id.textView_detail_list_price)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.listview_detail_repair, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val record = repairs[position]

        viewHolder.mTextViewDescription?.text = record.description
        viewHolder.mTextViewDateOfRepair?.text = SimpleDateFormat("dd/MM/yyyy").format(record.dateOfRepair)
        viewHolder.mTextViewPrice?.text = "Suma " +  record.price.toString() + "€"

        return view
    }

    override fun getItem(position: Int): Any {
        return repairs[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return repairs.size
    }
}