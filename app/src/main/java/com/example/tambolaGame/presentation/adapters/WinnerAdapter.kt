package com.example.tambolaGame.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tambolaGame.MainActivity.Companion.tambolaGameList
import com.example.tambolaGame.R
import com.example.tambolaGame.models.Game
import kotlinx.android.synthetic.main.item_winner.view.*


class WinnerAdapter(private val context: Context): RecyclerView.Adapter<WinnerAdapter.WinnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinnerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_winner, parent, false)
        return WinnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: WinnerViewHolder, position: Int) {
        holder.bind(tambolaGameList[position])
    }

    override fun getItemCount(): Int = tambolaGameList.size

    inner class WinnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Game) {
            itemView.gameName.text = item.gameName
            itemView.gamePrice.text = "Rs. ${item.gamePrice}"

            if (item.gameWinner != null) {
                itemView.gameWinner.text = item.gameWinner!!.userName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.border)!!.mutate()
                    drawable.setTint(ContextCompat.getColor(context, R.color.colorPrimary))
                    itemView.background = drawable
                }
            }
            else
                itemView.gameWinner.text = "-- NO WINNER --"
        }

    }
}