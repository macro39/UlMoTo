package com.example.ulmoto.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ulmoto.R
import com.example.ulmoto.persister.RecordEntity
import kotlinx.android.synthetic.main.item_search_record.view.*


/**
 * Created by Kamil Macek on 18.5.2020.
 */
class RecordAdapter(private var records: ArrayList<RecordEntity>) :
    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_record, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = records[position]

        holder.itemView.apply {
            tvDescription.text = item.firstName + " " + item.lastName
            tvPrice.text = item.licencePlate

            tvDetail.setOnClickListener {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                        item.recordId
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }
}