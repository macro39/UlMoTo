package com.example.ulmoto.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

    private val names = arrayListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar_search.visibility = View.VISIBLE

        updateListView()
//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    fun updateListView() {
        GlobalScope.launch(Dispatchers.IO) {
            records = (activity as MainActivity).database.recordDao().getAll()

            withContext(Dispatchers.Main) {
                progressBar_search.visibility = View.GONE

                names.clear()

                for (record in records) {
                    names.add(record.lastName)
                }

                adapter = ArrayAdapter(this@SearchFragment.requireContext(),
                    R.layout.listview_search_item, names)

                listView_search.adapter = adapter
            }
        }
    }
}
