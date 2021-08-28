package com.example.ulmoto.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.example.ulmoto.*
import com.example.ulmoto.data.models.Record
import com.example.ulmoto.ui.MainActivity
import com.example.ulmoto.ui.adapters.ImagePreviewAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.carousel_item.view.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Kamil Macek on 18.5.2020.
 */
class AddDialog : DialogFragment(R.layout.fragment_add) {

    private val imagePathList: ArrayList<Uri> = arrayListOf()

    private val images: ArrayList<String> = arrayListOf()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        updateImagesPreview()

        editText_add_licence_plate.filters =
            editText_add_licence_plate.filters + InputFilter.AllCaps()

        button_add_choose_image.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(requireActivity())
                .galleryOnly()
//                .crop()
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            displayImage(data?.data!!)
                        }
                        com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                "Chyba pri výbere obrázkov!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        button_add_take_photo.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(requireActivity())
                .cameraOnly()
//                .crop()
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            displayImage(data?.data!!)
                        }
                        com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                "Chyba pri výbere obrázkov!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        radioButton_image_first.setOnCheckedChangeListener { _, isChecked ->
            radioButton_image_second.isChecked = !isChecked
        }

        radioButton_image_second.setOnCheckedChangeListener { _, isChecked ->
            radioButton_image_first.isChecked = !isChecked
        }

        btnPickImages.setOnClickListener {
            images.clear()
            updateImagesPreview()

            UwMediaPicker
                .with(this)                        // Activity or Fragment
                .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                .setGridColumnCount(4)                                  // Grid column count, default is 3
//                .setMaxSelectableMediaCount(10)                         // Maximum selectable media count, default is null which means infinite
                .setLightStatusBar(true)                                // Is llight status bar enable, default is true
//                .enableImageCompression(true)				// Is image compression enable, default is false
//                .setCompressionMaxWidth(1280F)				// Compressed image's max width px, default is 1280
//                .setCompressionMaxHeight(720F)				// Compressed image's max height px, default is 720
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)		// Compressed image's format, default is JPEG
//                .setCompressionQuality(85)				// Image compression quality, default is 85
//                .setCompressedFileDestinationPath(destinationPath)	// Compressed image file's destination path, default is "${application.getExternalFilesDir(null).path}/Pictures"
                .launch { selectedMediaList ->
                    images.addAll(selectedMediaList?.map { it.mediaPath }!!)
                    updateImagesPreview()
                }
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

            if (etTelephone.getText(true).trim().isEmpty()
                || etTelephone.getText(true).trim().count() != 9
            ) {
                etTelephone.error = "Vyplňte telefón!"
                etTelephone.requestFocus()
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
        val newRecord = Record(
            firstName = editText_add_first_name.text.toString().capitalize(),
            lastName = editText_add_last_name.text.toString().capitalize(),
            licencePlate = editText_add_licence_plate.getText(false).toString().uppercase(),
            telephone = etTelephone.getText(false).toString(),
            images = images
        )

        if (imagePathList.size != 0) {
            for ((counter, uri: Uri) in imagePathList.withIndex()) {
                when (counter) {
                    0 -> {
                        newRecord.imageFirst = uri.path
                    }
                    1 -> {
                        newRecord.imageSecond = uri.path
                    }
                }
            }
        }

        (context as MainActivity).database.recordDao().upsert(newRecord)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                this@AddDialog.requireContext(),
                "Záznam bol úspešne pridaný!",
                Toast.LENGTH_SHORT
            ).show()

            (context as MainActivity).notifyDataSetChanged()

            this@AddDialog.dismiss()
        }
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

    fun updateImagesPreview() {
        val adapter = ImagePreviewAdapter(images)
        isImages.create(adapter, lifecycle = lifecycle)

        adapter.setOnItemClickListener(object : OnItemClickListener<String> {
            override fun onItemClicked(item: String?, position: Int) {
                showImagePreview(requireContext(), images as ArrayList<String?>)
            }
        })
    }
}