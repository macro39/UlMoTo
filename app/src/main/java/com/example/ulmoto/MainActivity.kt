package com.example.ulmoto


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ulmoto.persister.AppDatabase
import com.example.ulmoto.persister.RecordEntity
import com.example.ulmoto.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Kamil Macek on 17.5.2020.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var currentFragment: Fragment

    lateinit var database: AppDatabase

    var mImageViewFirst: ImageView? = null
    var mImageViewSecond: ImageView? = null

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
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.fragment_add, null)

            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)

            val mAlertDialog = mBuilder.show()

            mImageViewFirst = mDialogView.imageView_add_first
            mImageViewSecond = mDialogView.imageView_add_second

            mDialogView.button_choose_image.setOnClickListener {
                pickImageFromGallery()
            }

            mDialogView.button_add.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    database.recordDao().insert(RecordEntity(firstName = mDialogView.editText_add_first_name.text.toString(), lastName = mDialogView.editText_add_last_name.text.toString()))

                    withContext(Dispatchers.Main) {
                        (supportFragmentManager.fragments.last()?.childFragmentManager?.fragments?.get(0) as? SearchFragment)?.updateListView()
                        mAlertDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Povolenie nepridané", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            mImageViewFirst?.setImageURI(data?.data)
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

}
