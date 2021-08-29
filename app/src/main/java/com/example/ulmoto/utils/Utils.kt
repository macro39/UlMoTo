package com.example.ulmoto

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer

enum class FilterBy {
    FIRST_NAME,
    LAST_NAME,
    LICENCE_PLATE
}

fun showImagePreview(context: Context, images: ArrayList<String?>, position: Int = 0) {
    if (images.isEmpty()) {
        Toast.makeText(context, "Žiadne fotografie neboli nájdené", Toast.LENGTH_SHORT).show()
        return
    }
    StfalconImageViewer.Builder(
        context,
        images
    ) { view, url ->
        if (url != null) {
            Glide.with(view.context)
                .asBitmap()
                .load(url)
                .into(view)
        }
    }
        .withStartPosition(position)
        .withBackgroundColor(Color.WHITE)
        .withHiddenStatusBar(false)
        .show()
}