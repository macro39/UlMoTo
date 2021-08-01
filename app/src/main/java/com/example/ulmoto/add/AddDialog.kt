package com.example.ulmoto.add

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.ulmoto.MainActivity
import com.example.ulmoto.R
import com.example.ulmoto.persister.RecordEntity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


/**
 * Created by Kamil Macek on 18.5.2020.
 */
class AddDialog : DialogFragment() {

    lateinit var fileUri: Uri

    private val imagePathList: ArrayList<Uri> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editText_add_licence_plate.filters =
            editText_add_licence_plate.filters + InputFilter.AllCaps()

        button_add_choose_image.setOnClickListener {
            pickImageFromGallery()
        }

        button_add_take_photo.setOnClickListener {
            askCameraPermission()
        }

        radioButton_image_first.setOnCheckedChangeListener { _, isChecked ->
            radioButton_image_second.isChecked = !isChecked
        }

        radioButton_image_second.setOnCheckedChangeListener { _, isChecked ->
            radioButton_image_first.isChecked = !isChecked
        }

        button_add.setOnClickListener {
            editText_add_first_name.error = null
            editText_add_last_name.error = null
            editText_add_licence_plate.error = null

            if (editText_add_first_name.text.trim().isEmpty()) {
                editText_add_first_name.error = "Vyplňte meno!"
                editText_add_first_name.requestFocus()
                return@setOnClickListener
            }

            if (editText_add_last_name.text.trim().isEmpty()) {
                editText_add_last_name.error = "Vyplňte priezvisko!"
                editText_add_last_name.requestFocus()
                return@setOnClickListener
            }

            if (editText_add_licence_plate.getText(true).trim()
                    .isEmpty() || editText_add_licence_plate.getText(true).trim().count() != 7
            ) {
                editText_add_licence_plate.error = "Vyplňte EĆV!"
                editText_add_licence_plate.requestFocus()
                return@setOnClickListener
            }

            if (imageView_add_first.drawable == null && imageView_add_second.drawable == null) {
                val alertDialog = AlertDialog.Builder(this.requireContext())
                alertDialog.setTitle("Upozornenie")
                alertDialog.setMessage("Prajete si vytvoriť nový záznam bez fotografie TP?")

                alertDialog.setPositiveButton("Áno") { dialog, _ ->
                    dialog.dismiss()
                    checkLicencePlateAlreadyAdded()
                }

                alertDialog.setNegativeButton("Nie") { dialog, _ ->
                    dialog.dismiss()
                    return@setNegativeButton
                }

                alertDialog.show()
            } else {
                checkLicencePlateAlreadyAdded()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }

    private fun checkLicencePlateAlreadyAdded() = GlobalScope.launch(Dispatchers.IO) {
        val licencePlate = editText_add_licence_plate.getText(false).toString().toUpperCase()

        val sameRecords =
            (context as MainActivity).database.recordDao().getAllByLicencePlate(licencePlate)

        if (sameRecords.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                val alertDialog = AlertDialog.Builder(this@AddDialog.requireContext())
                alertDialog.setTitle("Upozornenie")
                alertDialog.setMessage("Záznam s EČV vozidla $licencePlate už existuje. Prajete si pokračovať a uložiť tento záznam?")

                alertDialog.setPositiveButton("Áno") { dialog, _ ->
                    dialog.dismiss()
                    addRecord()
                }

                alertDialog.setNegativeButton("Nie") { dialog, _ ->
                    dialog.dismiss()
                    editText_add_licence_plate.requestFocus()
                    return@setNegativeButton
                }

                alertDialog.show()
            }
        } else {
            addRecord()
        }
    }

    private fun addRecord() = GlobalScope.launch(Dispatchers.IO) {
        val newRecord = RecordEntity(
            firstName = editText_add_first_name.text.toString().capitalize(),
            lastName = editText_add_last_name.text.toString().capitalize(),
            licencePlate = editText_add_licence_plate.getText(false).toString().toUpperCase()
        )

        if (imagePathList.size != 0) {
            for ((counter, uri: Uri) in imagePathList.withIndex()) {
                val iStream: InputStream = activity?.contentResolver?.openInputStream(uri)!!
                val imageAsByteArray: ByteArray = iStream.readBytes()

                if (counter == 0) {
                    newRecord.imageFirst = imageAsByteArray
                }

                if (counter == 1) {
                    newRecord.imageSecond = imageAsByteArray
                }
            }
        }

        (context as MainActivity).database.recordDao().insert(newRecord)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                this@AddDialog.requireContext(),
                "Záznam bol úspešne pridaný!",
                Toast.LENGTH_SHORT
            ).show()
            (context as MainActivity).notifyDataSetChanged()
        }
    }

    private fun askCameraPermission() {
        Dexter.withActivity(this.requireActivity())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        launchCamera()
                    } else {
                        Toast.makeText(
                            this@AddDialog.requireContext(),
                            "Prosím povoľte vytváranie fotografí aplikáciou!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    AlertDialog.Builder(this@AddDialog.requireContext())
                        .setTitle(
                            "Chyba!"
                        )
                        .setMessage(
                            "Prosím povoľte vytváranie fotografií aplikáciou!"
                        )
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token.cancelPermissionRequest()
                        }
                        .setPositiveButton(
                            android.R.string.ok
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token.continuePermissionRequest()
                        }
                        .show()
                }
            }).check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        fileUri = activity?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun displayImage(uri: Uri) {
        if (radioButton_image_first.isChecked) {
            imageView_add_first.setImageURI(uri)
            radioButton_image_second.isChecked = true

            if (imagePathList.size > 0) {
                imagePathList[0] = uri
            } else {
                imagePathList.add(uri)
            }
        } else {
            imageView_add_second.setImageURI(uri)
            radioButton_image_first.isChecked = true

            if (imagePathList.size > 1) {
                imagePathList[1] = uri
            } else {
                imagePathList.add(uri)
            }
        }
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
                    Toast.makeText(
                        context,
                        "Prosím povoľte prístup k súborom v zariadení!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == TAKE_PHOTO_REQUEST
        ) {
            displayImage(fileUri)
        } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data!!.data != null) {
                displayImage(data.data!!)
            } else {
                if (data.clipData != null) {
                    val mClipData = data.clipData

                    imagePathList.clear()

                    if (mClipData!!.itemCount > 2) {
                        Toast.makeText(
                            this.requireContext(),
                            "Vybrali ste viac než 2 fotografie. Do záznamu sa dajú pridať maximálne 2 obrázky!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    for (i in 0 until mClipData.itemCount) {
                        val item = mClipData.getItemAt(i)

                        if (i == 0) {
                            imageView_add_first?.setImageURI(item.uri)
                            imagePathList.add(item.uri)
                        }

                        if (i == 1) {
                            imageView_add_second?.setImageURI(item.uri)
                            imagePathList.add(item.uri)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val TAKE_PHOTO_REQUEST = 1002
        private const val PERMISSION_CODE = 1001
    }
}