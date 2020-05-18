package com.example.ulmoto


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ulmoto.add.AddDialog
import com.example.ulmoto.persister.AppDatabase
import com.example.ulmoto.persister.RecordEntity
import com.example.ulmoto.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Kamil Macek on 17.5.2020.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var currentFragment: Fragment
    private var dialogFragment: DialogFragment? = null

    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        database = AppDatabase.create(this)

        GlobalScope.launch(Dispatchers.IO) {
            database.recordDao().insert(RecordEntity(firstName = "SKUSKA", lastName = "SKUSKA"))
            database.recordDao().insert(RecordEntity(firstName = "SKUSKA2", lastName = "SKUSKA2"))
        }

        fab.setOnClickListener { view ->
            val ft: FragmentTransaction? =
                supportFragmentManager.beginTransaction()
            val prev: Fragment? =
                supportFragmentManager.findFragmentByTag("AddDialog")
            if (prev != null) {
                ft?.remove(prev)
            }
            ft?.addToBackStack(null)
            dialogFragment = AddDialog()
            dialogFragment?.show(ft!!, "AddDialog")
        }
    }

    override fun onStart() {
        super.onStart()

        if (dialogFragment == null) {
            currentFragment =
                supportFragmentManager.fragments.last()?.childFragmentManager?.fragments?.get(0)!!
        }
    }

    fun notifyDataSetChanged() {
        (currentFragment as SearchFragment).updateListView()
        dialogFragment?.dismiss()
        dialogFragment = null
    }
}
