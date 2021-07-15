package com.example.projectstages.utils

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import com.example.projectstages.R

fun EditText.onTextChanged(callback: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback.invoke(s.toString())
        }

        override fun afterTextChanged(s: Editable?) { }
    })
}

fun Spinner.onItemSelected(callback: (Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback.invoke(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) { }
    }
}

fun Fragment.getStringExt(@StringRes resId: Int) : String
= resources.getString(resId)

fun Fragment.getStringArrayExt(resId: Int) : Array<String>
= resources.getStringArray(resId)

fun Fragment.getLongFromBundleExt(key: String) : Long
= arguments?.getLong(key) ?: 0L

fun Fragment.getBooleanFromBundleExt(key: String) : Boolean
= arguments?.getBoolean(key) ?: false

fun Fragment.getStringFromBundleExt(key: String) : String
= arguments?.getString(key, "") ?: ""

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), message, length).show()

fun Fragment.showSimpleDialog(title: String, message: String) {
    val dialog = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
    dialog.show()
}

fun Fragment.showSuccessDialog(title: String, message: String) {
    val titleAndMessage = buildSpannedString {
        bold {
            append("$title\n\n")
        }
        italic {
            append(message)
        }
    }

    val marginParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(16, 16, 16, 16)
    }

    val mainVerticalLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
    }
    val successImageView = ImageView(requireContext()).apply {
        setBackgroundResource(R.drawable.ic_completed_operation)
        layoutParams = marginParams
    }
    val messageTextView = TextView(requireContext()).apply {
        text = titleAndMessage
        textSize = 18f
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        layoutParams = marginParams
    }
    mainVerticalLayout.addView(successImageView)
    mainVerticalLayout.addView(messageTextView)

    val dialog = AlertDialog.Builder(requireContext())
        .setView(mainVerticalLayout)
        .setCancelable(false)
        .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
    dialog.show()
}

fun Fragment.showErrorDialog(title: String, message: String) {
    val titleAndMessage = buildSpannedString {
        bold {
            append("$title\n\n")
        }
        italic {
            append(message)
        }
    }

    val marginParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(16, 16, 16, 16)
    }

    val mainVerticalLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
    }
    val errorImageView = ImageView(requireContext()).apply {
        setBackgroundResource(R.drawable.ic_decline)
        layoutParams = marginParams
    }
    val messageTextView = TextView(requireContext()).apply {
        text = titleAndMessage
        textSize = 18f
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        layoutParams = marginParams
    }
    mainVerticalLayout.addView(errorImageView)
    mainVerticalLayout.addView(messageTextView)

    val dialog = AlertDialog.Builder(requireContext())
        .setView(mainVerticalLayout)
        .setCancelable(false)
        .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
    dialog.show()
}
