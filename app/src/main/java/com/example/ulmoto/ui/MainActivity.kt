package com.example.ulmoto.ui


import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.ulmoto.R
import com.example.ulmoto.db.models.Repair
import com.example.ulmoto.ui.dialogs.AddDialog
import com.example.ulmoto.ui.dialogs.AddRepairDialog
import com.example.ulmoto.ui.fragments.DetailFragment
import com.example.ulmoto.ui.fragments.SearchFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var currentFragment: Fragment
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        navHostFragment =
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

    fun notifyDataSetChanged(repair: Repair) {
        currentFragment = navHostFragment.childFragmentManager.fragments[0]
        (currentFragment as DetailFragment).addRepair(repair)
    }
}
