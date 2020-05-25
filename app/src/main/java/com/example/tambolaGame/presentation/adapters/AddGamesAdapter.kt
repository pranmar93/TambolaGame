package com.example.tambolaGame.presentation.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tambolaGame.MainActivity.Companion.gamesTotalPrice
import com.example.tambolaGame.MainActivity.Companion.members
import com.example.tambolaGame.MainActivity.Companion.tambolaGameList
import com.example.tambolaGame.MainActivity.Companion.ticketPrice
import com.example.tambolaGame.R
import com.example.tambolaGame.interfaces.GameChangedListener
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.utils.AddGamesListDiffUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_edit_game.view.*
import kotlinx.android.synthetic.main.item_add_game.view.*
import java.util.*
import kotlin.collections.ArrayList

class AddGamesAdapter
    (private val context: Context,
     private val gameChangedListener: GameChangedListener)
    : RecyclerView.Adapter<AddGamesAdapter.AddGameViewHolder>(){

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddGameViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_add_game, parent, false)
        return AddGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddGameViewHolder, position: Int) {
        holder.bind(tambolaGameList[position])

        holder.itemView.delete_game.setOnClickListener {
            deleteGame(holder.adapterPosition)
        }

        holder.itemView.edit_game.setOnClickListener {
            editGame(holder.adapterPosition)
        }
    }

    override fun onBindViewHolder(
        holder: AddGameViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val payloadBundle = payloads[0] as Bundle
            val game = Game(payloadBundle.getInt("gameId"), payloadBundle.getString("gameName")!!, payloadBundle["price"] as Int, null)
            holder.bind(game)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = tambolaGameList.size

    override fun getItemId(position: Int): Long {
        return tambolaGameList.indexOf(tambolaGameList[position]).toLong()
    }

    private fun deleteGame(position: Int) {
        val alertDialog = AlertDialog.Builder(context, R.style.CustomDialogTheme).create()
        alertDialog.setTitle("Delete Game")
        alertDialog.setMessage("Do you want to delete this game?")
        alertDialog.setCancelable(true)
        alertDialog.setCanceledOnTouchOutside(true)

        alertDialog.setButton(
            Dialog.BUTTON_POSITIVE,
            context.getString(R.string.yes)
        ) { dialog, _ ->
            dialog.dismiss()
            val oldList = ArrayList<Game>()
            oldList.addAll(tambolaGameList)
            gamesTotalPrice -= tambolaGameList[position].gamePrice
            tambolaGameList.removeAt(position)
            updateAddGamesList(oldList, tambolaGameList)
            if (tambolaGameList.size == 0)
                gameChangedListener.isGameListEmpty(tambolaGameList)
        }

        alertDialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            context.getString(R.string.no)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun editGame(position: Int) {
        val game = tambolaGameList[position]

        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_game, null, false)
        val nameLayout = rootView.layout_gameName
        val priceLayout = rootView.layout_gamePrice
        val winnerLayout = rootView.layout_gameWinner
        val nameInput = rootView.edit_gameName
        val priceInput = rootView.edit_gamePrice
        val winnerInput = rootView.edit_gameWinner
        nameInput.setText(game.gameName)
        priceInput.setText(game.gamePrice.toString())
        winnerLayout.visibility = View.GONE
        winnerInput.visibility = View.GONE

        val alertDialog: AlertDialog? =
            AlertDialog.Builder(context, R.style.CustomDialogTheme)
                .setView(rootView)
                .setTitle("Edit Game")
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null)
                .create()
        alertDialog?.setCanceledOnTouchOutside(false)
        alertDialog?.setCancelable(true)

        alertDialog?.setOnShowListener {
            val positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

            positiveButton.setOnClickListener {
                if (checkMail(nameLayout, nameInput, priceLayout, priceInput)) {
                    val prizeMoney = gamesTotalPrice  - game.gamePrice + priceInput.text.toString().trim().toInt()
                    if (prizeMoney <= (ticketPrice * members)) {
                        alertDialog.dismiss()
                        gamesTotalPrice = prizeMoney
                        val oldList = ArrayList<Game>()
                        oldList.addAll(tambolaGameList)
                        val newGame = Game(game.gameId, nameInput.text.toString().trim(), priceInput.text.toString().trim().toInt(), null)
                        tambolaGameList[position] = newGame
                        updateAddGamesList(oldList, tambolaGameList)
                    } else {
                        priceLayout.isErrorEnabled = true
                        priceLayout.error = "Limit Exceeded"
                    }
                }
            }

            val negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            negativeButton.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog?.show()
    }

    fun addGame(game: Game) {
        val pos = tambolaGameList.size
        val oldList = ArrayList<Game>()
        oldList.addAll(tambolaGameList)
        gamesTotalPrice += game.gamePrice
        tambolaGameList.add(game)
        updateAddGamesList(oldList, tambolaGameList)
        if (pos == 0)
            gameChangedListener.isGameListEmpty(tambolaGameList)
    }

    private fun updateAddGamesList(oldList: List<Game>, newList: List<Game>) {
        val gamesDiffUtil = AddGamesListDiffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(gamesDiffUtil)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun checkMail(nameLayout: TextInputLayout, nameEdit: TextInputEditText,
                          priceLayout: TextInputLayout, priceEdit: TextInputEditText): Boolean {
        when {
            TextUtils.isEmpty(nameEdit.text!!.trim()) -> {
                nameLayout.isErrorEnabled = true
                nameLayout.error = "Empty Game Name"
                return false
            }
            TextUtils.isEmpty(priceEdit.text!!.trim()) -> {
                priceLayout.isErrorEnabled = true
                priceLayout.error = "Empty Price"
                return false
            }
            tambolaGameList.filter { g -> g.gameName.toLowerCase(Locale.getDefault()) == nameEdit.text.toString().toLowerCase(Locale.getDefault()).trim()}.size > 1 -> {
                nameLayout.isErrorEnabled = true
                nameLayout.error = "Duplicate Game"
                return false
            }
            else -> {
                nameLayout.isErrorEnabled = false
                priceLayout.isErrorEnabled = false
                return true
            }
        }
    }

    class AddGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Game) {
            itemView.add_game.setText(item.gameName)
            itemView.add_price.setText(item.gamePrice.toString())
        }

    }
}