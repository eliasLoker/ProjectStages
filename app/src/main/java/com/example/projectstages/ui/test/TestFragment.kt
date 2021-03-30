package com.example.projectstages.ui.test

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.example.projectstages.R
//import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment(R.layout.fragment_test) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        open_dialog.setOnClickListener {
//            openDialog()
//        }
    }

    private fun openDialog() {
        Log.d("TestDebug", "Open dialog")
        val dateDeliveryList = getDateDelivery()
        val dateList = dateDeliveryList.toStingDateDelivery()
        val mainScrollView = ScrollView(requireContext())
        val mainVerticalLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35)
            background = ContextCompat.getDrawable(requireContext(), R.color.blue_white)
        }
        val checkDateRadioGroup = RadioGroup(requireContext()).apply {
            orientation = RadioGroup.VERTICAL
        }.apply {
            dateList.forEach {
                val button = RadioButton(requireContext()).apply {
                    id = View.generateViewId()
                    text = it
                }
                addView(button)
            }
        }
        mainVerticalLayout.apply {
            addView(checkDateRadioGroup)
        }
        mainScrollView.addView(mainVerticalLayout)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Выберите дату доставки")
            .setView(mainScrollView)
            .setPositiveButton("Выбрать") { dialog, _ ->
                Log.d("TestDebug", "Checked: ${checkDateRadioGroup.checkedRadioButtonId}")
                Log.d("TestDebug", "Checked: clicked")
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun List<DateDelivery>.toStingDateDelivery() = map { it.date }

    private fun getDateDelivery() = ArrayList<DateDelivery>().apply {
        add(DateDelivery(1, "15:15-15:30"))
        add(DateDelivery(2, "15:30-15:45"))
        add(DateDelivery(3, "15:45-16:00"))
        add(DateDelivery(4, "16:00-16:15"))
        add(DateDelivery(5, "16:15-16:30"))

        add(DateDelivery(6, "15:15-15:30"))
        add(DateDelivery(7, "15:30-15:45"))
        add(DateDelivery(8, "15:45-16:00"))
        add(DateDelivery(9, "16:00-16:15"))
        add(DateDelivery(10, "16:15-16:30"))

        add(DateDelivery(11, "15:15-15:30"))
        add(DateDelivery(12, "15:30-15:45"))
        add(DateDelivery(13, "15:45-16:00"))
        add(DateDelivery(14, "16:00-16:15"))
        add(DateDelivery(15, "16:15-16:30"))

        add(DateDelivery(16, "15:15-15:30"))
        add(DateDelivery(17, "15:30-15:45"))
        add(DateDelivery(18, "15:45-16:00"))
        add(DateDelivery(19, "16:00-16:15"))
        add(DateDelivery(20, "16:15-16:30"))

        add(DateDelivery(21, "15:15-15:30"))
        add(DateDelivery(22, "15:30-15:45"))
        add(DateDelivery(23, "15:45-16:00"))
        add(DateDelivery(24, "16:00-16:15"))
        add(DateDelivery(25, "16:15-16:30"))
    }

    fun getRadioButton(title: String) = RadioButton(requireContext()).apply {
        text = title
    }

    companion object {
        @JvmStatic
        fun newInstance()= Bundle()
    }
}