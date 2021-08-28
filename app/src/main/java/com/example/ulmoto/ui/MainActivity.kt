package com.example.ulmoto.ui


import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.ulmoto.R
import com.example.ulmoto.data.AppDatabase
import com.example.ulmoto.data.models.Repair
import com.example.ulmoto.ui.dialogs.AddDialog
import com.example.ulmoto.ui.dialogs.AddRepairDialog
import com.example.ulmoto.ui.fragments.DetailFragment
import com.example.ulmoto.ui.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


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

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        Timber.plant(Timber.DebugTree())

        database = AppDatabase.create(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.SearchFragment)).build()
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        floatingActionButton_add_record.setOnClickListener {
            currentFragment = navHostFragment.childFragmentManager.fragments[0]

            when (currentFragment) {
                is SearchFragment -> {
                    AddDialog().show(supportFragmentManager, "AddDialog")
                }
                is DetailFragment -> {
                    AddRepairDialog().show(supportFragmentManager, "AddRepairDialog")
                }
            }
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

    fun notifyDataSetChanged(repair: Repair? = null) {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment
        currentFragment = navHostFragment!!.childFragmentManager.fragments[0]

        when (currentFragment) {
            is SearchFragment -> {
                (currentFragment as SearchFragment).reloadAllRecordsFromDatabase()
            }
            is DetailFragment -> {
                (currentFragment as DetailFragment).addRepair(repair!!)
            }
        }

        dialogFragment?.dismiss()
        dialogFragment = null
    }
}
