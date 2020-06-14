package com.example.tambolaGame.ticketGrid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.ContextCompat
import com.example.tambolaGame.R
import com.example.ticketgenerator.GridGenerator
import com.example.ticketgenerator.TambolaTicketGrid


class TambolaGrid (private val context: Context) {

    @SuppressLint("InflateParams")
    fun initView(ticketGrid: GridGenerator.Node): GridView {
        val root = LayoutInflater.from(context).inflate(R.layout.layout_tambola_grid, null, true)

        val grid = root.findViewById<TambolaTicketGrid>(R.id.gridView)
        grid.setExpanded(true)

        val ticketArray = arrayListOf<String>()
        ticketGrid.matrix.forEach { it.forEach { p -> ticketArray.add(p.toString()) } }

        grid.adapter = object : ArrayAdapter<String>(
            context, android.R.layout.simple_list_item_1, ticketArray
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                val tv = view as TextView

                val lp = AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT
                )

                tv.layoutParams = lp

                tv.gravity = Gravity.CENTER
                tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

                if (ticketArray[position] == "0") {
                    tv.text = ""
                    tv.isClickable = true
                    tv.isLongClickable = true
                } else {
                    tv.text = ticketArray[position]
                    tv.isClickable = false
                    tv.isLongClickable = false
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setBackgroundColor(parseColor("#ffffff"))
                } else {
                    tv.background = ContextCompat.getDrawable(context, R.drawable.cell_state_drawable)
                }

                return tv
            }
        }

        grid.onItemClickListener = OnItemClickListener { _, view, _, _ ->

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (view.foreground == null)
                    view.foreground = ContextCompat.getDrawable(context, R.drawable.circle1)
                else
                    view.foreground = null
            } else {
                if (view.alpha == 0.98f) {
                    view.isActivated = false
                    /*view.background =
                        ContextCompat.getDrawable(context, R.drawable.background_uncheck)*/
                    view.alpha = 1f
                }
                else {
                    view.isActivated = true
                    view.alpha = 0.98f
                    /*view.background = ContextCompat.getDrawable(context, R.drawable.background_check)*/
                }
            }
        }

        return grid
    }
}