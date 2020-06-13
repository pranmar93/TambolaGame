package com.example.tambolaGame.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tambolaGame.R
import com.example.tambolaGame.presentation.fragments.GameFragment
import kotlinx.android.synthetic.main.item_number_board.view.*


class NumberBoardAdapter(private val context: Context):
    RecyclerView.Adapter<NumberBoardAdapter.NumberViewHolder>(){

    init {
        setHasStableIds(true)
    }

    private val numberList = (1..90).toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_number_board, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numberList[position].toString())

        if ((holder.adapterPosition + 1) !in GameFragment.numberGenerator.arrayNumber) {
            holder.itemView.cell.background = ContextCompat.getDrawable(context, R.drawable.circle_color_border)
        } else {
            holder.itemView.cell.background = ContextCompat.getDrawable(context, R.drawable.circle_border)
        }
    }

    override fun getItemCount(): Int = numberList.size

    override fun getItemId(position: Int): Long {
        return numberList.indexOf(numberList[position]).toLong()
    }

    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String) {
            itemView.cell.text = item
        }
    }
}