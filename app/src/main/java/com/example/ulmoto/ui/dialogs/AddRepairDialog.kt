package com.example.ulmoto.ui.dialogs

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.example.ulmoto.R
import com.example.ulmoto.data.models.RepairEntity
import com.example.ulmoto.showImagePreview
import com.example.ulmoto.ui.MainActivity
import com.example.ulmoto.ui.adapters.ImagePreviewAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add_repair.*
import kotlinx.android.synthetic.main.fragment_add_repair.btnPickImages
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Kamil Macek on 22.5.2020.
 */
class AddRepairDialog : DialogFragment(R.layout.fragment_add_repair) {

    private val images: ArrayList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val c: Calendar = Calendar.getInstance()
        val month: Int = c.get(Calendar.MONTH) + 1
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        val year: Int = c.get(Calendar.YEAR)

        textView_add_repair_date.text =
            "" + String.format("%02d", day) + "/" + String.format("%02d", (month + 1)) + "/$year"

        textView_add_repair_date.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val month: Int = c.get(Calendar.MONTH)
            val day: Int = c.get(Calendar.DAY_OF_MONTH)
            val year: Int = c.get(Calendar.YEAR)

            val datePickerDialog = DatePickerDialog(
                this.requireActivity(),
                { _, year, month, dayOfMonth ->
                    textView_add_repair_date.text =
                        "" + String.format("%02d", dayOfMonth) + "/" + String.format(
                            "%02d",
                            (month + 1)
                        ) + "/$year"
                }, year, month, day
            )
            datePickerDialog.show()
        }

        button_add_repair.setOnClickListener {
            if (editText_add_repair_description.text.trim().isEmpty()) {
                editText_add_repair_description.error = "Vyplňte popis!"
                editText_add_repair_description.requestFocus()
                return@setOnClickListener
            }

            if (editText_add_repair_price.text.trim().isEmpty()) {
                editText_add_repair_price.error = "Vyplňte sumu!"
                editText_add_repair_price.requestFocus()
                return@setOnClickListener
            }

            val repair = RepairEntity(
                description = editText_add_repair_description.text.toString(),
                dateOfRepair = SimpleDateFormat("dd/MM/yyyy").parse(textView_add_repair_date.text.toString()),
                price = editText_add_repair_price.text.toString().toDouble(),
                images = images
            )

            (context as MainActivity).notifyDataSetChanged(repair)

            this.dismiss()
        }

        btnPickImages.setOnClickListener {
            images.clear()
            updateImagesPreview()

            UwMediaPicker
                .with(this)
                .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
                .setGridColumnCount(4)
                .setLightStatusBar(true)
                .launch { selectedMediaList ->
                    images.addAll(selectedMediaList?.map { it.mediaPath }!!)
                    updateImagesPreview()
                }
        }
    }

    fun updateImagesPreview() {
        val adapter = ImagePreviewAdapter(images)
        pvImages.create(adapter, lifecycle = lifecycle)

        adapter.setOnItemClickListener(object : OnItemClickListener<String> {
            override fun onItemClicked(item: String?, position: Int) {
                showImagePreview(requireContext(), images as ArrayList<String?>)
            }
        })
    }
}