package com.example.ulmoto.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.example.ulmoto.*
import com.example.ulmoto.persister.RecordWithRepairs
import com.example.ulmoto.persister.RepairEntity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.opensooq.pluto.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Kamil Macek on 22.5.2020.
 */
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var selectedRecord: RecordWithRepairs
    private var selectedRecordId: Long = -1

    private lateinit var repairAdapter: RepairAdapter
//    private var repairs: ArrayList<RepairEntity> = arrayListOf()

    private var priceCount: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        progressBar.show()

        button_detail_add_repair.setOnClickListener {
            val ft: FragmentTransaction =
                childFragmentManager.beginTransaction()
            val prev: Fragment? =
                childFragmentManager.findFragmentByTag("AddRepair")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val addRepairFragment = AddRepairDialog()
            addRepairFragment.show(ft, "AddRepair")
        }

        selectedRecordId = args.recordId

        GlobalScope.launch(Dispatchers.IO) {
            selectedRecord =
                (context as MainActivity).database.recordWithRepairsDao().getRecordWithRepairs(
                    selectedRecordId
                )
            showData()
        }
    }

    private fun showData() {
        GlobalScope.launch(Dispatchers.Main) {
            textView_detail_last_name.text =
                selectedRecord.recordEntity.firstName + " " + selectedRecord.recordEntity.lastName
            textView_detail_licence_plate.text = selectedRecord.recordEntity.licencePlate
            tvTelephone.text = selectedRecord.recordEntity.telephone ?: "-"

            val previewImages = arrayListOf<String?>()

            if (selectedRecord.recordEntity.imageFirst != null) {
                imageView_detail_first.setImageURI(Uri.parse(selectedRecord.recordEntity.imageFirst))
                previewImages.add(selectedRecord.recordEntity.imageFirst)
            }

            if (selectedRecord.recordEntity.imageSecond != null) {
                imageView_detail_second.setImageURI(Uri.parse(selectedRecord.recordEntity.imageSecond))
                previewImages.add(selectedRecord.recordEntity.imageSecond)
            }

            if (previewImages.isNotEmpty()) {
                imageView_detail_first.setOnClickImagePreview(previewImages, 0)
                imageView_detail_second.setOnClickImagePreview(previewImages, 1)
            }

            updatePriceCount()
            updateImagesPreview()
            updateRepairsRv()
        }
    }

    private fun pickImages(deleteOldImages: Boolean) {
        UwMediaPicker
            .with(this)
            .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery)
            .setGridColumnCount(4)
            .setLightStatusBar(true)
            .launch { selectedMediaList ->
                val images = if (deleteOldImages) {
                    ArrayList()
                } else {
                    ArrayList(selectedRecord.recordEntity.images)
                }

                selectedMediaList?.forEach {
                    val img = it.mediaPath
                    if (!images.contains(img)) {
                        images.add(img)
                    }
                }

                selectedRecord.recordEntity.images = images
                updateRecord()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionAddImages -> {
                this.requireContext().yesNoDialog("Upozornenie",
                    "Prajete si odstrániť doposiaľ pridané fotografie?",
                    onPositiveButtonClicked = {
                        pickImages(true)
                    },
                    onNegativeButtonClicked = {
                        pickImages(false)
                    })

                true
            }
            R.id.actionCall -> {
                if (selectedRecord.recordEntity.telephone.isNullOrBlank()) {
                    Toast.makeText(context, "Nie je priradené žiadne číslo", Toast.LENGTH_SHORT)
                        .show()
                    return true
                }

                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:" + selectedRecord.recordEntity.telephone)
                    startActivity(this)
                }

                true
            }
            R.id.actionShare -> {
                val uris = arrayListOf<Uri>()

                if (selectedRecord.recordEntity.imageFirst != null) {
                    uris.add(Uri.parse("file://" + selectedRecord.recordEntity.imageFirst))
                }

                if (selectedRecord.recordEntity.imageSecond != null) {
                    uris.add(Uri.parse("file://" + selectedRecord.recordEntity.imageSecond))
                }

                if (uris.isEmpty()) {
                    this.requireContext().toast("Pre zdieľanie najprv pridajte fotografie TP!")
                    return false
                }

                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        uris
                    )
                    requireContext().startActivity(this)
                }

                true
            }
            R.id.actionSetLicencePlate -> {
                requireContext().openMaskedEdittextDialog(
                    "Zmena EČV",
                    "AA-*****",
                    7,
                    false,
                    {
                        selectedRecord.recordEntity.licencePlate = it
                        updateRecord()
                    })

                true
            }
            R.id.actionSetTelephone -> {
                requireContext().openMaskedEdittextDialog(
                    "Zmena tel. č.",
                    "0999999999",
                    9,
                    false,
                    {
                        selectedRecord.recordEntity.telephone = it
                        updateRecord()
                    })
                true
            }
            R.id.actionPickFirstImage -> {
                pickOrTakeImage(true)
                true
            }
            R.id.actionPickSecondImage -> {
                pickOrTakeImage(false)
                true
            }
            R.id.actionDelete -> {
                this.requireContext().yesNoDialog("Upozornenie",
                    "Prajete si odstrániť záznam spolu so všetkými opravami?",
                    onPositiveButtonClicked = {
                        GlobalScope.launch(Dispatchers.IO) {
                            (activity as MainActivity).database.recordDao()
                                .delete(selectedRecord.recordEntity)

                            selectedRecord.repairList.forEach {
                                (activity as MainActivity).database.repairDao().delete(it)
                            }

                            withContext(Dispatchers.Main) {
                                val action =
                                    DetailFragmentDirections.actionDetailFragmentToSearchFragment()
                                findNavController().navigate(action)
                            }
                        }
                    },
                    onNegativeButtonClicked = {})

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateRecord() {
        GlobalScope.launch(Dispatchers.IO) {
            (activity as MainActivity).database.recordDao().upsert(selectedRecord.recordEntity)
        }

        requireContext().toast("Záznam bol úspešne aktualizovaný!")
        showData()
    }

    private fun updateImagesPreview() {
        if (selectedRecord.recordEntity.images.isEmpty()) {
            tvImagesTitle.hide()
        } else {
            tvImagesTitle.show()
        }

        val adapter =
            ImagePreviewAdapter(selectedRecord.recordEntity.images as MutableList<String>)
        isDetailImages.create(adapter, lifecycle = lifecycle)

        adapter.setOnItemClickListener(object : OnItemClickListener<String> {
            override fun onItemClicked(item: String?, position: Int) {
                showImagePreview(
                    requireContext(),
                    selectedRecord.recordEntity.images as ArrayList<String?>,
                    position
                )
            }
        })
    }

    private fun pickOrTakeImage(frontSite: Boolean) {
        ImagePicker.with(requireActivity())
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (frontSite) {
                            this.selectedRecord.recordEntity.imageFirst = data?.data.toString()
                        } else {
                            this.selectedRecord.recordEntity.imageSecond = data?.data.toString()
                        }

                        updateRecord()
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            "Chyba pri výbere obrázkov!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun updatePriceCount() = GlobalScope.launch(Dispatchers.IO) {
        priceCount = try {
            (activity as MainActivity).database.repairDao().getPriceCount(selectedRecordId)
        } catch (e: NullPointerException) {
            0.0
        }

        withContext(Dispatchers.Main) {
            textView_detail_price_count.text = priceCount.toString() + "€"
        }
    }

    private fun updateRepairsRv() = GlobalScope.launch(Dispatchers.Main) {
        progressBar.show()

        rvRepairs.swipeListener = null

        repairAdapter = RepairAdapter(ArrayList(selectedRecord.repairList))
        repairAdapter.setHasStableIds(true)

        if (selectedRecord.repairList.isEmpty()) {
            textView_detail_no_records.visibility = View.VISIBLE
        } else {
            textView_detail_no_records.visibility = View.GONE
        }

        rvRepairs.orientation =
            DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING

        rvRepairs.disableDragDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.UP)
        rvRepairs.disableDragDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.DOWN)

        val onItemSwipeListener = object : OnItemSwipeListener<RepairEntity> {
            override fun onItemSwiped(
                position: Int,
                direction: OnItemSwipeListener.SwipeDirection,
                item: RepairEntity
            ): Boolean {
                val list = ArrayList<RepairEntity>(selectedRecord.repairList)
                list.remove(item)
                selectedRecord.repairList = list

                GlobalScope.launch(Dispatchers.IO) {
                    (activity as MainActivity).database.repairDao().delete(item)

                    selectedRecord =
                        (context as MainActivity).database.recordWithRepairsDao()
                            .getRecordWithRepairs(
                                selectedRecordId
                            )

                    updatePriceCount()
                }

                return false
            }
        }

        rvRepairs.swipeListener = onItemSwipeListener

        rvRepairs.layoutManager = LinearLayoutManager(context)
        rvRepairs.adapter = repairAdapter

        progressBar.hide()
    }

    fun addRepair(repair: RepairEntity) {
        repair.recordEntityId = selectedRecordId

        GlobalScope.launch(Dispatchers.IO) {
            (activity as MainActivity).database.repairDao().upsert(repair)

            selectedRecord =
                (context as MainActivity).database.recordWithRepairsDao().getRecordWithRepairs(
                    selectedRecordId
                )

            withContext(Dispatchers.Main) {
                repairAdapter.dataSet = ArrayList(selectedRecord.repairList)
            }

            updatePriceCount()
        }
    }
}