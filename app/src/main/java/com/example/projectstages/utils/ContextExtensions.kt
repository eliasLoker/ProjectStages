package com.example.projectstages.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

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
