package com.example.ulmoto.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ulmoto.R
import kotlinx.android.synthetic.main.fragment_add_repair.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Kamil Macek on 22.5.2020.
 */
class AddRepairDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_repair, container, false)
    }

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

            val datePickerDialog = DatePickerDialog(this.requireActivity(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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

            (parentFragment as DetailFragment).addRepair(
                editText_add_repair_description.text.toString(),
                SimpleDateFormat("dd/MM/yyyy").parse(textView_add_repair_date.text.toString()),
                editText_add_repair_price.text.toString().toDouble()
            )

            this.dismiss()
        }
    }
}