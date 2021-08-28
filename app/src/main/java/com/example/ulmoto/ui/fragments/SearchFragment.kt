package com.example.ulmoto.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ulmoto.R
import com.example.ulmoto.data.models.RecordEntity
import com.example.ulmoto.ui.MainActivity
import com.example.ulmoto.ui.adapters.RecordAdapter
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Kamil Macek on 17.5.2020.
 */
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var records: List<RecordEntity> = arrayListOf()
    private lateinit var adapter: RecordAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        reloadAllRecordsFromDatabase()

        button_search.setOnClickListener {
            filterData()
        }

        chipGroup_search_filter.setOnCheckedChangeListener { group, checkedId ->
            val checkedChip = group.findViewById<Chip>(checkedId)

            val message = StringBuilder("Vyhľadávanie podľa ")

            when (checkedChip.id) {
                R.id.chip_search_first_name -> message.append("mena")
                R.id.chip_search_last_name -> message.append("priezviska")
                R.id.chip_search_licence_plate -> message.append("EČV")
            }

            Toast.makeText(
                this.requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reload -> {
                Toast.makeText(
                    this.requireContext(),
                    "Boli načítané všetky záznamy",
                    Toast.LENGTH_SHORT
                ).show()

                reloadAllRecordsFromDatabase()
                editText_search_filter.setText("", TextView.BufferType.EDITABLE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterData() {
        progressBar_search.visibility = View.VISIBLE
        val id = chipGroup_search_filter.checkedChipId

        GlobalScope.launch(Dispatchers.IO) {
            when (id) {
                R.id.chip_search_first_name -> {
                    records = (activity as MainActivity).database.recordDao()
                        .getAllByFirstName(editText_search_filter.text.toString())
                }
                R.id.chip_search_last_name -> {
                    records = (activity as MainActivity).database.recordDao()
                        .getAllByLastName(editText_search_filter.text.toString())
                }
                R.id.chip_search_licence_plate -> {
                    records = (activity as MainActivity).database.recordDao()
                        .getAllByLicencePlate(editText_search_filter.text.toString())
                }
            }

            withContext(Dispatchers.Main) {
                updateListViewAdapter()
            }
        }
    }

    fun reloadAllRecordsFromDatabase() {
        editText_search_filter.setText("", TextView.BufferType.EDITABLE)
        GlobalScope.launch(Dispatchers.IO) {
            records = (activity as MainActivity).database.recordDao().getAll()

            withContext(Dispatchers.Main) {
                updateListViewAdapter()
            }
        }
    }

    private fun updateListViewAdapter() {
        progressBar_search.visibility = View.VISIBLE

        rvSearch.adapter = null
        adapter = RecordAdapter(this@SearchFragment.records as ArrayList<RecordEntity>)

        if (records.isEmpty()) {
            textView_search_no_data.visibility = View.VISIBLE
        } else {
            textView_search_no_data.visibility = View.GONE
        }

        textView_search_results_count.text =
            "${getString(R.string.label_number_of_records)}${records.size}"

        rvSearch.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )

        val mDivider = ContextCompat.getDrawable(this.requireContext(), R.drawable.list_divider)
        divider.setDrawable(mDivider!!)

        rvSearch.addItemDecoration(divider)
        rvSearch.adapter = adapter

        progressBar_search.visibility = View.GONE
    }
}