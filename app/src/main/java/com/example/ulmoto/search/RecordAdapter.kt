package com.example.ulmoto.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import com.example.ulmoto.R
import com.example.ulmoto.detail.DetailFragment
import com.example.ulmoto.persister.RecordEntity


/**
 * Created by Kamil Macek on 18.5.2020.
 */
class RecordAdapter(private var activity: Activity, private var navController: NavController, private var records: ArrayList<RecordEntity>) :
    BaseAdapter() {

    private class ViewHolder(row: View?) {
        var mTextViewFullName: TextView? = null
        var mTextViewLicencePlate: TextView? = null
        var mButtonDetail: Button? = null

        init {
            this.mTextViewFullName = row?.findViewById(R.id.textView_detail_list_description)
            this.mTextViewLicencePlate = row?.findViewById(R.id.textView_detail_list_price)
            this.mButtonDetail = row?.findViewById(R.id.button_search_list_detail)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.listview_search_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val record = records[position]

        viewHolder.mTextViewFullName?.text = record.firstName + " " + record.lastName
        viewHolder.mTextViewLicencePlate?.text = record.licencePlate

        viewHolder.mButtonDetail?.setOnClickListener {
            val args = Bundle()
            args.putLong(DetailFragment.RECORD_ID, record.recordId)
            navController.navigate(R.id.DetailFragment, args)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return records[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return records.size
    }
}