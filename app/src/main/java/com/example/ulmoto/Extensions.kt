package com.example.ulmoto

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.kolotnev.formattedittext.MaskedEditText

fun View.setOnClickImagePreview(images: ArrayList<String?>, position: Int = 0) {
    this.setOnClickListener {
        showImagePreview(this.context, images, position)
    }
}

fun View.show() {
    GlobalScope.launch(Dispatchers.Main) {
        this@show.isVisible = true
    }
}

fun View.hide() {
    GlobalScope.launch(Dispatchers.Main) {
        this@hide.isVisible = false
    }
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(this@toast, message, duration).show()
    }
}

fun Context.openMaskedEdittextDialog(
    title: String,
    mask: String,
    finalLength: Int,
    removeMask: Boolean,
    onPositiveButtonClicked: (text: String) -> Unit,
    onNegativeButtonClicked: (() -> Unit?)? = null
) {
    val input = MaskedEditText(this).apply {
        setText("")
        gravity = Gravity.CENTER_HORIZONTAL
        setMask(mask)
        placeholder = '_'
        filters += InputFilter.AllCaps()
    }

    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setPositiveButton("Potvrdiť", null)
    builder.setNegativeButton("Zrušiť", null)
        .setView(input)

    val dialog = builder.show()

    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        if (input.getText(true).trim().isEmpty() ||
            input.getText(true).trim().count() != finalLength
        ) {
            input.error = "Musí byť vyplnené!"
            input.requestFocus()
        } else {
            dialog.dismiss()
            onPositiveButtonClicked(input.getText(removeMask).toString())
        }
    }

    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
        dialog.dismiss()
        onNegativeButtonClicked?.invoke()
    }
}

fun Context.yesNoDialog(
    title: String,
    message: String,
    onPositiveButtonClicked: () -> Unit,
    onNegativeButtonClicked: () -> Unit
) = AlertDialog.Builder(this).apply {
    setTitle(title)
    setMessage(message)
    setPositiveButton("Áno") { dialog, _ ->
        dialog.dismiss()
        onPositiveButtonClicked()
    }
    setNegativeButton("Nie") { dialog, _ ->
        dialog.dismiss()
        onNegativeButtonClicked()
        return@setNegativeButton
    }
    show()
}