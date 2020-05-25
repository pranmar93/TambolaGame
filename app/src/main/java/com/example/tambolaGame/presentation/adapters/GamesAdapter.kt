package com.example.tambolaGame.presentation.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tambolaGame.MainActivity.Companion.gameStart
import com.example.tambolaGame.MainActivity.Companion.myDevice
import com.example.tambolaGame.MainActivity.Companion.tambolaGameList
import com.example.tambolaGame.MainActivity.Companion.tambolaMembers
import com.example.tambolaGame.R
import com.example.tambolaGame.gameRole
import com.example.tambolaGame.interfaces.WinnerChangedListener
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.utils.GamesListDiffUtil
import com.example.tambolaGame.utils.RoleEnums
import kotlinx.android.synthetic.main.dialog_edit_game.view.*
import kotlinx.android.synthetic.main.item_game.view.*


class GamesAdapter (private val context: Context,
                    private val winnerListener: WinnerChangedListener)
    : RecyclerView.Adapter<GamesAdapter.GameViewHolder>(){

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game1 = tambolaGameList[position]
        holder.bind(game1)

        if (game1.gameWinner != null) {
            holder.itemView.game_Name.paintFlags =
                holder.itemView.game_Name.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            if (myDevice.userName == game1.gameWinner!!.userName) {
                holder.itemView.game_Name.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                holder.itemView.setBackgroundColor(Color.CYAN)
            } else {
                holder.itemView.game_Name.typeface = Typeface.DEFAULT
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
        } else {
            holder.itemView.game_Name.paintFlags =
                holder.itemView.game_Name.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.itemView.game_Name.typeface = Typeface.DEFAULT
        }

        holder.itemView.game_Name.setOnClickListener {
            val game = tambolaGameList[position]

            if (gameStart) {
                val rootView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_edit_game, null, false)
                val nameLayout = rootView.layout_gameName
                val priceLayout = rootView.layout_gamePrice
                val winnerLayout = rootView.layout_gameWinner
                val nameInput = rootView.edit_gameName
                val priceInput = rootView.edit_gamePrice
                val winnerInput = rootView.edit_gameWinner
                nameInput.setText(game.gameName)
                priceInput.setText(game.gamePrice.toString())
                if (game.gameWinner == null) {
                    winnerInput.setText("")
                } else {
                    winnerInput.setText(game.gameWinner!!.userName)
                }

                nameLayout.isEnabled = false
                nameInput.isEnabled = false
                priceLayout.isEnabled = false
                priceInput.isEnabled = false
                winnerLayout.isEnabled = false
                winnerInput.isEnabled = false

                val alertDialog: AlertDialog? =
                    AlertDialog.Builder(context, R.style.CustomDialogTheme)
                        .setView(rootView)
                        .setTitle("Mark Winner")
                        .create()

                if (gameRole == RoleEnums.SERVER) {
                    alertDialog?.setButton(Dialog.BUTTON_POSITIVE, "Set Winner") { dialog, _ ->
                        dialog.dismiss()

                        val list = Array(tambolaMembers.size) { "" }

                        for ((i, item) in tambolaMembers.withIndex()) {
                            list[i] = item.userName!!
                        }

                        val alertD = AlertDialog.Builder(context, R.style.CustomDialogTheme)
                        alertD.setTitle("Select Winner")

                        alertD.setItems(
                            list
                        ) { _, index ->
                            val member = tambolaMembers[index]

                            val oldList = ArrayList<Game>()
                            oldList.addAll(tambolaGameList)

                            if (game.gameWinner == null) {
                                val chGame =
                                    Game(game.gameId, game.gameName, game.gamePrice, member)

                                if (member.userId != myDevice.userId) {
                                    myDevice.walletMoney -= game.gamePrice
                                    member.walletMoney += game.gamePrice
                                }

                                tambolaGameList[position] = chGame
                                updateGamesList(oldList, tambolaGameList)

                                winnerListener.setWinnerData(
                                    position,
                                    game.gameId,
                                    game.gameName,
                                    game.gamePrice,
                                    member.userName!!,
                                    member.userId
                                )
                            } else if (game.gameWinner!!.userId != member.userId) {
                                val prevWinnerId = game.gameWinner!!.userId

                                val chGame =
                                    Game(game.gameId, game.gameName, game.gamePrice, member)

                                if (myDevice.userId == prevWinnerId) {
                                    myDevice.walletMoney -= game.gamePrice
                                } else {
                                    game.gameWinner!!.walletMoney -= game.gamePrice
                                }

                                if (myDevice.userId == member.userId) {
                                    myDevice.walletMoney += game.gamePrice
                                } else {
                                    member.walletMoney += game.gamePrice
                                }

                                tambolaGameList[position] = chGame
                                updateGamesList(oldList, tambolaGameList)

                                winnerListener.changeWinnerData(
                                    position,
                                    game.gameId,
                                    game.gameName,
                                    game.gamePrice,
                                    member.userName!!,
                                    member.userId,
                                    prevWinnerId
                                )
                            }

                            Toast.makeText(this.context, "Winner Set", Toast.LENGTH_SHORT)
                                .show()
                        }
                        alertD.create()
                        alertD.show()
                    }

                    alertDialog?.setButton(Dialog.BUTTON_NEGATIVE, "Remove Winner") { dialog, _ ->
                        dialog.dismiss()

                        val oldList = ArrayList<Game>()
                        oldList.addAll(tambolaGameList)

                        val prevWinner = game.gameWinner!!

                        if (prevWinner.userId != myDevice.userId) {
                            myDevice.walletMoney += game.gamePrice
                            game.gameWinner!!.walletMoney -= game.gamePrice
                        }

                        winnerListener.removeWinnerData(
                            position,
                            game.gameId,
                            game.gameName,
                            game.gamePrice,
                            prevWinner.userId
                        )

                        val chGame = Game(game.gameId, game.gameName, game.gamePrice, null)

                        tambolaGameList[position] = chGame
                        updateGamesList(oldList, tambolaGameList)
                        Toast.makeText(this.context, "Winner Removed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                alertDialog?.setCanceledOnTouchOutside(false)
                alertDialog?.setCancelable(true)
                alertDialog?.show()
            }
        }
    }

    override fun getItemCount(): Int = tambolaGameList.size

    override fun getItemId(position: Int): Long {
        return tambolaGameList.indexOf(tambolaGameList[position]).toLong()
    }

    override fun onBindViewHolder(
        holder: GameViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val payloadBundle = payloads[0] as Bundle
            if (payloadBundle.containsKey("winner")) {
                val game = payloadBundle.getString("winner")
                holder.strikeWinner(game)
            } else {
                onBindViewHolder(holder, position)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    fun updateGamesList(oldList: List<Game>, newList: List<Game>) {
        val gamesDiffUtil = GamesListDiffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(gamesDiffUtil)
        diffResult.dispatchUpdatesTo(this)
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Game) {
            itemView.game_Name.text = item.gameName
        }

        fun strikeWinner(winner: String?) {
            if (winner != null) {
                itemView.game_Name.paintFlags =
                    itemView.game_Name.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                if (myDevice.userName == winner) {
                    itemView.game_Name.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    itemView.setBackgroundColor(Color.CYAN)
                } else {
                    itemView.game_Name.typeface = Typeface.DEFAULT
                    itemView.setBackgroundColor(Color.WHITE)
                }
            } else {
                itemView.game_Name.paintFlags =
                    itemView.game_Name.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                itemView.setBackgroundColor(Color.WHITE)
                itemView.game_Name.typeface = Typeface.DEFAULT
            }
        }

    }

}