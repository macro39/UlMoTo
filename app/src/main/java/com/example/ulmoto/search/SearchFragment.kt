package com.example.ulmoto.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ulmoto.MainActivity
import com.example.ulmoto.R
import com.example.ulmoto.persister.RecordEntity
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Kamil Macek on 17.5.2020.
 */
class SearchFragment : Fragment() {

    private var records: List<RecordEntity> = arrayListOf()
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadRecordsFromDatabase()

        button_search.setOnClickListener {
            filterData()
        }
//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    private fun filterData() {
    }

    fun reloadRecordsFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            records = (activity as MainActivity).database.recordDao().getAll()

            withContext(Dispatchers.Main) {
                updateListViewAdapter()
            }
        }
    }

    private fun updateListViewAdapter() {
        progressBar_search.visibility = View.VISIBLE

        listView_search.adapter = null

        adapter = RecordAdapter(
            this@SearchFragment.requireActivity(),
            this@SearchFragment.records as ArrayList<RecordEntity>
        )

        textView_search_results_count.text =
            "${getString(R.string.label_number_of_records)}${records.size}"

        listView_search.adapter = adapter

        progressBar_search.visibility = View.GONE
    }
}
