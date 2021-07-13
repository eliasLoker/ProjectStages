package com.example.projectstages.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

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

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(requireContext(), message, length).show()
