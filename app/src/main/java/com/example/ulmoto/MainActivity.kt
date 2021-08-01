package com.example.ulmoto


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.ulmoto.add.AddDialog
import com.example.ulmoto.persister.AppDatabase
import com.example.ulmoto.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.SearchFragment)).build()
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

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
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun notifyDataSetChanged() {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment
        currentFragment = navHostFragment!!.childFragmentManager.fragments[0]

        if (currentFragment is SearchFragment) {
            (currentFragment as SearchFragment).reloadAllRecordsFromDatabase()
        }
        dialogFragment?.dismiss()
        dialogFragment = null
    }
}
