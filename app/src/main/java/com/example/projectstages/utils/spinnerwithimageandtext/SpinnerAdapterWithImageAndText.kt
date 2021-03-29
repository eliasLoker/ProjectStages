package com.example.projectstages.utils.spinnerwithimageandtext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.projectstages.R

class SpinnerAdapterWithImageAndText(
    private val context: Context,
    private val spinnerItems: List<SpinnerItem>
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getCount() = spinnerItems.size

    override fun getItem(position: Int) = null

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val view = layoutInflater.inflate(R.layout.item_spinner_with_text, parent, false)
            val image = view.findViewById<ImageView>(R.id.ivCountry)
                .apply { setImageResource(spinnerItems[position].imageID) }
            val text = view.findViewById<TextView>(R.id.tvCountry)
                .apply { text = spinnerItems[position].description }
            return view
        }
        return convertView
    }
}