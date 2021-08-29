package com.example.ulmoto.ui.fragments

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.anilokcun.uwmediapicker.UwMediaPicker
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.example.ulmoto.*
import com.example.ulmoto.db.models.RecordWithRepairs
import com.example.ulmoto.db.models.Repair
import com.example.ulmoto.ui.adapters.ImagePreviewAdapter
import com.example.ulmoto.ui.adapters.RepairAdapter
import com.example.ulmoto.ui.dialogs.AddRepairDialog
import com.example.ulmoto.ui.viewmodels.MainViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.opensooq.pluto.listeners.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var selectedRecord: RecordWithRepairs
    private var selectedRecordId: Long = -1

    private lateinit var repairAdapter: RepairAdapter

    private val viewModel: MainViewModel by viewModels()

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

        viewModel.selectRecord(selectedRecordId)

        setupRepairRecyclerView()
    }

    private fun showData() {
        textView_detail_last_name.text =
            selectedRecord.record.firstName + " " + selectedRecord.record.lastName
        textView_detail_licence_plate.text = selectedRecord.record.licencePlate
        tvTelephone.text = selectedRecord.record.telephone ?: "-"

        tvRepairsCount.text =
            "${getString(R.string.label_number_of_records)}${selectedRecord.repairList.size}"

        textView_detail_price_count.text = selectedRecord.getRepairsPrice().toString() + "€"

        if (selectedRecord.repairList.isEmpty()) {
            textView_detail_no_records.visibility = View.VISIBLE
        } else {
            textView_detail_no_records.visibility = View.GONE
        }

        val previewImages = arrayListOf<String?>()

        if (selectedRecord.record.imageFirst != null) {
            imageView_detail_first.setImageURI(Uri.parse(selectedRecord.record.imageFirst))
            previewImages.add(selectedRecord.record.imageFirst)
        }

        if (selectedRecord.record.imageSecond != null) {
            imageView_detail_second.setImageURI(Uri.parse(selectedRecord.record.imageSecond))
            previewImages.add(selectedRecord.record.imageSecond)
        }

        if (previewImages.isNotEmpty()) {
            imageView_detail_first.setOnClickImagePreview(previewImages, 0)

            if (previewImages.size == 2) {
                imageView_detail_second.setOnClickImagePreview(previewImages, 1)
            }
        }

        updateImagesPreview()
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
                    ArrayList(selectedRecord.record.images)
                }

                selectedMediaList?.forEach {
                    val img = it.mediaPath
                    if (!images.contains(img)) {
                        images.add(img)
                    }
                }

                selectedRecord.record.images = images
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
                if (selectedRecord.record.telephone.isNullOrBlank()) {
                    Toast.makeText(context, "Nie je priradené žiadne číslo", Toast.LENGTH_SHORT)
                        .show()
                    return true
                }

                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:" + selectedRecord.record.telephone)
                    startActivity(this)
                }

                true
            }
            R.id.actionShare -> {
                val uris = arrayListOf<Uri>()

                if (selectedRecord.record.imageFirst != null) {
                    uris.add(Uri.parse("file://" + selectedRecord.record.imageFirst))
                }

                if (selectedRecord.record.imageSecond != null) {
                    uris.add(Uri.parse("file://" + selectedRecord.record.imageSecond))
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
                        selectedRecord.record.licencePlate = it
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
                        selectedRecord.record.telephone = it
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
                        viewModel.delete(selectedRecord)
                        val action =
                            DetailFragmentDirections.actionDetailFragmentToSearchFragment()
                        findNavController().navigate(action)
                    },
                    onNegativeButtonClicked = {})

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateRecord() {
        viewModel.upsertRecord(selectedRecord.record)

        requireContext().toast("Záznam bol úspešne aktualizovaný!")
    }

    private fun updateImagesPreview() {
        if (selectedRecord.record.images.isEmpty()) {
            tvImagesTitle.hide()
        } else {
            tvImagesTitle.show()
        }

        val adapter =
            ImagePreviewAdapter(selectedRecord.record.images as MutableList<String>)
        isDetailImages.create(adapter, lifecycle = lifecycle)

        adapter.setOnItemClickListener(object : OnItemClickListener<String> {
            override fun onItemClicked(item: String?, position: Int) {
                showImagePreview(
                    requireContext(),
                    selectedRecord.record.images as ArrayList<String?>,
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
                            this.selectedRecord.record.imageFirst = data?.data.toString()
                        } else {
                            this.selectedRecord.record.imageSecond = data?.data.toString()
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

    private fun setupRepairRecyclerView() {
        repairAdapter = RepairAdapter(arrayListOf())
        repairAdapter.setHasStableIds(true)

        rvRepairs.orientation =
            DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING

        rvRepairs.disableDragDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.UP)
        rvRepairs.disableDragDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.DOWN)

        val onItemSwipeListener = object : OnItemSwipeListener<Repair> {
            override fun onItemSwiped(
                position: Int,
                direction: OnItemSwipeListener.SwipeDirection,
                item: Repair
            ): Boolean {
                viewModel.delete(item)
                return false
            }
        }

        rvRepairs.swipeListener = onItemSwipeListener

        rvRepairs.layoutManager = LinearLayoutManager(context)
        rvRepairs.adapter = repairAdapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.selectedRecord.collect {
                progressBar.show()
                selectedRecord = it
                showData()
                repairAdapter.submitList(selectedRecord.repairList)
                progressBar.hide()
            }
        }
    }

    fun addRepair(repair: Repair) {
        repair.recordEntityId = selectedRecordId
        viewModel.upsertRepair(repair)
    }
}