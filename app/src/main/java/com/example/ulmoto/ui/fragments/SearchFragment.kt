package com.example.ulmoto.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ulmoto.FilterBy
import com.example.ulmoto.R
import com.example.ulmoto.db.models.Record
import com.example.ulmoto.hide
import com.example.ulmoto.show
import com.example.ulmoto.ui.adapters.RecordAdapter
import com.example.ulmoto.ui.viewmodels.MainViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var records: List<Record> = arrayListOf()
    private lateinit var adapter: RecordAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        updateListViewAdapter()

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
                progressBar_search.show()
                viewModel.loadRecords()

                Toast.makeText(
                    this.requireContext(),
                    "Boli načítané všetky záznamy",
                    Toast.LENGTH_SHORT
                ).show()

                editText_search_filter.setText("", TextView.BufferType.EDITABLE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterData() {
        progressBar_search.show()
        val id = chipGroup_search_filter.checkedChipId

        val searchText = editText_search_filter.text.toString()
        when (id) {
            R.id.chip_search_first_name -> {
                viewModel.filter(FilterBy.FIRST_NAME, searchText)
            }
            R.id.chip_search_last_name -> {
                viewModel.filter(FilterBy.LAST_NAME, searchText)
            }
            R.id.chip_search_licence_plate -> {
                viewModel.filter(FilterBy.LICENCE_PLATE, searchText)
            }
        }
    }

    private fun updateListViewAdapter() {
        progressBar_search.show()

        rvSearch.adapter = null
        adapter = RecordAdapter(this@SearchFragment.records as ArrayList<Record>)

        updateViews()

        rvSearch.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        val mDivider = ContextCompat.getDrawable(this.requireContext(), R.drawable.list_divider)
        divider.setDrawable(mDivider!!)

        rvSearch.addItemDecoration(divider)
        rvSearch.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.records.collect {
                progressBar_search.show()
                records = it
                updateViews()

                adapter.submitList(records)
                progressBar_search.hide()
            }
        }

        progressBar_search.hide()
    }

    private fun updateViews() {
        if (records.isEmpty()) {
            textView_search_no_data.visibility = View.VISIBLE
        } else {
            textView_search_no_data.visibility = View.GONE
        }

        textView_search_results_count.text =
            "${getString(R.string.label_number_of_records)}${records.size}"
    }
}
