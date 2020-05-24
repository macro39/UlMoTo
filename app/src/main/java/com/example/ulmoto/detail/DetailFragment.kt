package com.example.ulmoto.detail

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ulmoto.MainActivity
import com.example.ulmoto.R
import com.example.ulmoto.persister.RecordWithRepairs
import com.example.ulmoto.persister.RepairEntity
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * Created by Kamil Macek on 22.5.2020.
 */
class DetailFragment : Fragment() {

    companion object {
        const val RECORD_ID = "RECORD_ID"
    }

    private lateinit var selectedRecord: RecordWithRepairs
    private var selectedRecordId: Long = -1

    private lateinit var repairAdapter: RepairAdapter

    private var priceCount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).enableBackButton(true)

        button_detail_add_repair.setOnClickListener {
            val ft: FragmentTransaction? =
                childFragmentManager.beginTransaction()
            val prev: Fragment? =
                childFragmentManager.findFragmentByTag("AddRepair")
            if (prev != null) {
                ft?.remove(prev)
            }
            ft?.addToBackStack(null)
            val addRepairFragment = AddRepairDialog()
            addRepairFragment.show(ft!!, "AddRepair")
        }

        imageView_detail_first.setOnClickListener {
            showDetailOfImage(true)
        }

        imageView_detail_second.setOnClickListener {
            showDetailOfImage(false)
        }

        selectedRecordId = arguments?.getLong(RECORD_ID)!!

        GlobalScope.launch(Dispatchers.IO) {
            reloadSelectedRecord()

            updatePriceCount()

            withContext(Dispatchers.Main) {
                textView_detail_first_name.text = selectedRecord.recordEntity.firstName
                textView_detail_last_name.text = selectedRecord.recordEntity.lastName
                textView_detail_licence_plate.text = selectedRecord.recordEntity.licencePlate

                if (selectedRecord.recordEntity.imageFirst != null) {
                    imageView_detail_first.setImageDrawable(
                        BitmapDrawable(
                            BitmapFactory.decodeByteArray(
                                selectedRecord.recordEntity.imageFirst, 0,
                                selectedRecord.recordEntity.imageFirst?.size!!
                            )
                        )
                    )
                }

                if (selectedRecord.recordEntity.imageSecond != null) {
                    imageView_detail_second.setImageDrawable(
                        BitmapDrawable(
                            BitmapFactory.decodeByteArray(
                                selectedRecord.recordEntity.imageSecond, 0,
                                selectedRecord.recordEntity.imageSecond?.size!!
                            )
                        )
                    )
                }

                updateListViewAdapter()
            }
        }
    }

    private fun updatePriceCount() = GlobalScope.launch(Dispatchers.IO) {
        priceCount = try {
            (activity as MainActivity).database.repairDao().getPriceCount(selectedRecordId)
        } catch (e: NullPointerException) {
            0.0
        }

        withContext(Dispatchers.Main) {
            textView_detail_price_count.text = priceCount.toString() + "€"
        }
    }

    private fun reloadSelectedRecord() = GlobalScope.launch(Dispatchers.IO) {
        selectedRecord =
            (context as MainActivity).database.recordWithRepairsDao().getRecordWithRepairs(
                selectedRecordId
            )
    }

    private fun updateListViewAdapter() = GlobalScope.launch(Dispatchers.Main) {
        listView_detail_repairs.adapter = null

        repairAdapter = RepairAdapter(
            this@DetailFragment.requireActivity(),
            selectedRecord.repairList as ArrayList<RepairEntity>
        )

        if (selectedRecord.repairList.isEmpty()) {
            textView_detail_no_records.visibility = View.VISIBLE
        } else {
            textView_detail_no_records.visibility = View.GONE
        }

        listView_detail_repairs.adapter = repairAdapter
    }

    fun addRepair(description: String, dateOfRepair: Date, price: Double) {
        val repairEntity = RepairEntity(
            recordEntityId = selectedRecordId,
            description = description,
            dateOfRepair = dateOfRepair,
            price = price
        )

        GlobalScope.launch(Dispatchers.IO) {
            (activity as MainActivity).database.repairDao().insert(repairEntity)

            reloadSelectedRecord()
            updateListViewAdapter()
            updatePriceCount()
        }
    }

    private fun showDetailOfImage(firstImage: Boolean) {
        val factory = LayoutInflater.from(context)
        val view: View = factory.inflate(R.layout.image_view_detail, null)

        val imageView = view.findViewById<ImageView>(R.id.image_view_dialog_detail)

        if (firstImage) {
            imageView.setImageDrawable(
                BitmapDrawable(
                    BitmapFactory.decodeByteArray(
                        selectedRecord.recordEntity.imageFirst, 0,
                        selectedRecord.recordEntity.imageFirst?.size!!
                    )
                )
            )
        } else {
            imageView.setImageDrawable(
                BitmapDrawable(
                    BitmapFactory.decodeByteArray(
                        selectedRecord.recordEntity.imageSecond, 0,
                        selectedRecord.recordEntity.imageSecond?.size!!
                    )
                )
            )
        }

        val dialog = Dialog(this.requireContext(), android.R.style.Theme_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view)
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog.show()
    }
}