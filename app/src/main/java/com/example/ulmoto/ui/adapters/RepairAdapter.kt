package com.example.ulmoto.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.example.ulmoto.R
import com.example.ulmoto.db.models.Repair
import com.example.ulmoto.hide
import com.example.ulmoto.show
import com.example.ulmoto.showImagePreview
import com.example.ulmoto.ui.adapters.RepairAdapter.RepairViewHolder
import kotlinx.android.synthetic.main.item_repair_detail.view.*
import java.text.SimpleDateFormat

class RepairAdapter(
    repairs: ArrayList<Repair> = arrayListOf()
) :
    DragDropSwipeAdapter<Repair, RepairViewHolder>(repairs) {

    inner class RepairViewHolder(view: View) : DragDropSwipeAdapter.ViewHolder(view)

    fun submitList(newData: List<Repair>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepairViewHolder {
        return RepairViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repair_detail, parent, false)
        )
    }

    override fun getViewHolder(itemView: View): RepairViewHolder = RepairViewHolder(itemView)

    override fun getViewToTouchToStartDraggingItem(
        item: Repair,
        viewHolder: RepairViewHolder,
        position: Int
    ): View? {
        return viewHolder.itemView.tvDescription
    }

    override fun onBindViewHolder(item: Repair, viewHolder: RepairViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvDescription.text = item.description

            if (item.description.contains("\n")) {
                tvExpandButtonTitle.show()
            } else {
                tvExpandButtonTitle.hide()
            }

            tvExpandButtonTitle.setOnClickListener {
                expand_collapse.callOnClick()
            }

            tvDescription.setOnExpandStateChangeListener { _, isExpanded ->
                tvExpandButtonTitle.text = if (isExpanded) {
                    "Skryť popis"
                } else {
                    "Zobraziť celý popis"
                }
            }

            tvDate.text =
                SimpleDateFormat("dd/MM/yyyy").format(item.dateOfRepair)
            tvPrice.text = "Suma " + item.price.toString() + "€"

            btnShowImages.setOnClickListener {
                showImagePreview(context, item.images as java.util.ArrayList<String?>)
            }
        }
    }
}