package com.example.tambolaGame.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tambolaGame.R
import com.example.tambolaGame.models.UserDevice
import kotlinx.android.synthetic.main.item_participants_wallet.view.*


class ParticipantsWalletAdapter(private val context: Context, private val type: String, private val adapterList: List<UserDevice>):
    RecyclerView.Adapter<ParticipantsWalletAdapter.ParticipantsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_participants_wallet, parent, false)
        return ParticipantsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        holder.bind(adapterList[position])

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (position == 0 && type == "Participants") {
                val drawable = ContextCompat.getDrawable(context, R.drawable.border)!!.mutate()
                drawable.setTint(Color.parseColor("#F1FF73"))
                holder.itemView.background = drawable
            }
    }

    override fun getItemCount(): Int = adapterList.size

    inner class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: UserDevice) {
            itemView.wallet_name.text = item.userName
            itemView.wallet_phone.text = item.phone

            if (type == "Wallet")
                itemView.wallet_money.text = "Rs. ${item.walletMoney}"
            else
                itemView.wallet_money.visibility = View.GONE
        }

    }
}