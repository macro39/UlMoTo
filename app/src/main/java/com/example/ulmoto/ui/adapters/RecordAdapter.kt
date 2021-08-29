package com.example.ulmoto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ulmoto.R
import com.example.ulmoto.db.models.Record
import com.example.ulmoto.ui.fragments.SearchFragmentDirections
import kotlinx.android.synthetic.main.item_search_record.view.*

class RecordAdapter(private var records: ArrayList<Record>) :
    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun submitList(newData: List<Record>) {
        records.clear()
        records.addAll(newData)
        notifyDataSetChanged()
    }

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