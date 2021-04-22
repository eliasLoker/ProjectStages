package com.example.projectstages.customview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.projectstages.R

class SpinnerAdapterWithImage(
    context: Context,
    private val imagesIds: Array<Int>
) : ArrayAdapter<String>(context, imagesIds[0], arrayOfNulls(imagesIds.size)) {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parentViewGroup: ViewGroup): View {
        val view = inflater.inflate(R.layout.item_spinner, parentViewGroup, false).apply {
            when(position) {
                0 -> setPadding(10, 0, 10, 0)
                imagesIds.size -> setPadding(10, 0, 10, 0)
                else -> setPadding(10, 20, 10, 0)
            }
        }
        val icon = view.findViewById<ImageView>(R.id.spinner_image).apply {
//            setPadding()
        }
        icon.setImageResource(imagesIds[position])
        return view
    }
}