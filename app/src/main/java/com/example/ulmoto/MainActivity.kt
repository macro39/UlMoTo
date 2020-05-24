package com.example.ulmoto


import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
            database.recordDao().insert(
                RecordEntity(
                    firstName = "Adolf",
                    lastName = "Hitler",
                    licencePlate = "LV-145EG"
                )
            )
            database.recordDao().insert(
                RecordEntity(
                    firstName = "Janko",
                    lastName = "Tribula",
                    licencePlate = "BA-420KU"
                )
            )
        }

        floatingActionButton_add_record.setOnClickListener {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                enableBackButton(false)
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun notifyDataSetChanged() {
        (currentFragment as SearchFragment).reloadAllRecordsFromDatabase()
        dialogFragment?.dismiss()
        dialogFragment = null
    }

    fun enableBackButton(enable: Boolean) {
        floatingActionButton_add_record.visibility = if (enable) {
            View.GONE
        } else {
            View.VISIBLE
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
    }
}
