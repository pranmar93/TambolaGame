package com.example.tambolaGame.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.MainActivity.Companion.gamesTotalPrice
import com.example.tambolaGame.MainActivity.Companion.members
import com.example.tambolaGame.MainActivity.Companion.tambolaGameList
import com.example.tambolaGame.MainActivity.Companion.ticketPrice
import com.example.tambolaGame.R
import com.example.tambolaGame.interfaces.GameChangedListener
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.presentation.adapters.AddGamesAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_edit_game.view.*
import kotlinx.android.synthetic.main.fragment_add_games.*
import kotlinx.android.synthetic.main.fragment_add_games.view.*
import java.util.*

class AddGamesFragment: Fragment(), GameChangedListener {

    private lateinit var root: View
    private lateinit var addGamesAdapter: AddGamesAdapter

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_add_games, container, false)

        if (tambolaGameList.size == 0) {
            root.addGames_recycler.visibility = View.GONE
            root.no_added_games.visibility = View.VISIBLE
        } else {
            root.no_added_games.visibility = View.GONE
            root.addGames_recycler.visibility = View.VISIBLE
        }

        addGamesAdapter = AddGamesAdapter(context!!, this)
        root.addGames_recycler.adapter = addGamesAdapter

        root.btn_new_games.setOnClickListener {
            if (gamesTotalPrice < (ticketPrice * members)) {
                val rootView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_edit_game, null, false)
                val nameLayout = rootView.layout_gameName
                val priceLayout = rootView.layout_gamePrice
                val winnerLayout = rootView.layout_gameWinner
                val nameInput = rootView.edit_gameName
                val priceInput = rootView.edit_gamePrice
                val winnerInput = rootView.edit_gameWinner
                winnerLayout.visibility = View.GONE
                winnerInput.visibility = View.GONE

                val alertDialog: AlertDialog? =
                    AlertDialog.Builder(context!!, R.style.CustomDialogTheme)
                        .setView(rootView)
                        .setTitle("Add Game")
                        .setPositiveButton("OK", null)
                        .create()
                alertDialog?.setCanceledOnTouchOutside(false)
                alertDialog?.setCancelable(true)

                alertDialog?.setOnShowListener {
                    val positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

                    positiveButton.setOnClickListener {
                        if (checkMail(nameLayout, nameInput, priceLayout, priceInput)) {
                            if ((gamesTotalPrice + priceInput.text.toString().toInt()) <= (ticketPrice * members)) {
                                alertDialog.dismiss()
                                val gameName = nameInput.text.toString()
                                val gamePrice = priceInput.text.toString()
                                val gameId = if (tambolaGameList.size == 0) {
                                    0
                                } else {
                                    tambolaGameList[tambolaGameList.size - 1].gameId + 1
                                }
                                val game = Game(gameId,
                                    gameName,
                                    gamePrice.toInt(),
                                    null
                                )
                                addGamesAdapter.addGame(game)
                            } else {
                                priceLayout.isErrorEnabled = true
                                priceLayout.error = "Limit Exceeded"
                            }
                        }
                    }
                }

                alertDialog?.show()
            } else {
                Toast.makeText(context, "Cannot add new game as prize limit reached.", Toast.LENGTH_SHORT).show()
            }
        }

        root.btn_start_game.setOnClickListener {
            if (gamesTotalPrice == (members * ticketPrice)) {
                val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme).create()
                val title = "Create Server"
                builder.setTitle(title)
                val msg = "No new game can be added once server is created"
                builder.setMessage(msg)
                builder.setButton(
                    Dialog.BUTTON_POSITIVE,
                    "ACCEPT"
                ) { dialog, _ ->
                    (activity as MainActivity).startAdvertising()
                    findNavController().navigate(R.id.action_nav_add_games_to_nav_games)
                    dialog.dismiss()
                }
                builder.setButton(
                    Dialog.BUTTON_NEGATIVE,
                    "DECLINE"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setCanceledOnTouchOutside(false)
                builder.setCancelable(true)
                builder.show()
            } else
                Toast.makeText(context, "Total Prize Money does not match total amount collected", Toast.LENGTH_SHORT).show()

        }

        return root
    }

    private fun checkMail(nameLayout: TextInputLayout, nameEdit: TextInputEditText,
                          priceLayout: TextInputLayout, priceEdit: TextInputEditText
    ): Boolean {
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
            tambolaGameList.find {g -> g.gameName.toLowerCase(Locale.getDefault()) == nameEdit.text.toString().toLowerCase(
                Locale.getDefault()).trim()} != null -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        addGames_recycler.adapter = null
    }

    override fun isGameListEmpty(gameList: ArrayList<Game>) {
        if (gameList.size == 0) {
            addGames_recycler.visibility = View.GONE
            no_added_games.visibility = View.VISIBLE
        } else {
            no_added_games.visibility = View.GONE
            addGames_recycler.visibility = View.VISIBLE
        }
    }
}