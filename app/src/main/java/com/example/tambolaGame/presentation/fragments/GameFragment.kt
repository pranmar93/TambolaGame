package com.example.tambolaGame.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.numbergenerator.NumberGenerator
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.MainActivity.Companion.ADD_WINNER
import com.example.tambolaGame.MainActivity.Companion.CHANGE_WINNER
import com.example.tambolaGame.MainActivity.Companion.GEN_NEW_NUMBER
import com.example.tambolaGame.MainActivity.Companion.REMOVE_WINNER
import com.example.tambolaGame.MainActivity.Companion.currNumber
import com.example.tambolaGame.MainActivity.Companion.gameStart
import com.example.tambolaGame.MainActivity.Companion.members
import com.example.tambolaGame.MainActivity.Companion.myDevice
import com.example.tambolaGame.MainActivity.Companion.prevNumber
import com.example.tambolaGame.MainActivity.Companion.tambolaGameList
import com.example.tambolaGame.MainActivity.Companion.tambolaMembers
import com.example.tambolaGame.MainActivity.Companion.ticketSize
import com.example.tambolaGame.R
import com.example.tambolaGame.interfaces.WinnerChangedListener
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.presentation.adapters.GamesAdapter
import com.example.tambolaGame.ticketGrid.TambolaGrid
import com.example.tambolaGame.utils.RoleEnums
import com.example.tambolaGame.utils.ScreenshotUtils
import com.example.ticketgenerator.GridGenerator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_game_ticket.view.*
import java.text.SimpleDateFormat
import java.util.*


class GameFragment: Fragment(), WinnerChangedListener {

    private lateinit var root: View
    private lateinit var ticketLayout: LinearLayout
    private lateinit var gamesAdapter: GamesAdapter
    private lateinit var ticketGrids: Array<GridGenerator.Node?>

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::root.isInitialized) {
            root = inflater.inflate(R.layout.fragment_game_ticket, container, false)

            ticketLayout = root.game_ticket

            if (!::ticketGrids.isInitialized)
                ticketGrids = GridGenerator().getGrid(ticketSize)

            getTickets()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                (root.gen_number as FloatingActionButton).size = FloatingActionButton.SIZE_MINI
            }

            numberGenerator = NumberGenerator()

            if (myDevice.userRole == RoleEnums.SERVER) {
                root.gen_number.setOnClickListener {
                    if (tambolaMembers.size == members || gameStart) {
                        if (gameStart) {
                            prevNumber = currNumber
                            currNumber = numberGenerator.generateNumber()

                            (activity as MainActivity).sendAllData(GEN_NEW_NUMBER, currNumber)

                            if (currNumber == "Empty") {
                                root.curr_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                                root.curr_number.text = "End"
                                root.prev_number.text = prevNumber
                                root.gen_number.visibility = View.GONE
                            } else {
                                root.curr_number.text = currNumber
                                root.prev_number.text = prevNumber
                            }
                        } else {
                            val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme).create()
                            val title = "Start Game"
                            builder.setTitle(title)
                            val msg = "No new member will be able to join once game begins"
                            builder.setMessage(msg)
                            builder.setButton(
                                Dialog.BUTTON_POSITIVE,
                                "ACCEPT"
                            ) { dialog, _ ->
                                for (i in 0 until ticketSize) {
                                    root.game_ticket.getChildAt(i).isEnabled = true
                                }
                                gameStart = true
                                (activity as MainActivity).stopAdvertising()
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
                        }
                    } else {
                        Toast.makeText(context, "Some members still have not joined the game", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                root.gen_number.visibility = View.GONE
            }

            root.number_layout.setOnClickListener {
                findNavController().navigate(R.id.action_nav_games_to_nav_number_board)
            }

            for (i in 0 until ticketSize) {
                root.game_ticket.getChildAt(i).isEnabled = false
            }
        }

        if (currNumber == "Empty") {
            root.curr_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            root.curr_number.text = "End"
            root.prev_number.text = prevNumber
        } else {
            root.curr_number.text = currNumber
            root.prev_number.text = prevNumber
        }

        gamesAdapter = GamesAdapter(context!!,this)

        root.games_rv.adapter = gamesAdapter

        return root
    }

    private fun getTickets() {
        root.scroll_game.removeAllViews()

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(10, 10, 10, 10)

        for (i in 0 until ticketSize) {
            val view = TambolaGrid(context!!).initView(ticketGrids[i]!!)

            if (view.parent != null) {
                (view.parent as ViewGroup).removeView(view)
            }

            view.layoutParams = lp
            ticketLayout.addView(view, lp)
        }

        root.scroll_game.addView(ticketLayout, lp)
    }

    /*override fun onResume() {
        super.onResume()
        *//*activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (activity as MainActivity).hideActionBar()*//*
    }*/

    fun updateGamesAdapter(oldList: List<Game>) {
        gamesAdapter.updateGamesList(oldList, tambolaGameList)
    }

    fun updateNewNumber() {
        if (!gameStart) {
            gameStart = true
            for (i in 0 until ticketSize) {
                root.game_ticket.getChildAt(i).isEnabled = true
            }
        }
        if (currNumber == "Empty") {
            root.curr_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            root.curr_number.text = resources.getString(R.string.game_end)
            root.prev_number.text = prevNumber
        } else {
            root.curr_number.text = currNumber
            root.prev_number.text = prevNumber
        }
    }

    fun takeScreenshot() {
        val screenshotUtils = ScreenshotUtils()
        val screenBitMap = screenshotUtils.getScreenShot(root.game_ticket)
        val timeStamp = SimpleDateFormat("dd_MM_yy_h:mm:ss", Locale.ENGLISH).format(Date())

        if (screenBitMap != null) {
            val screenshotFile = screenshotUtils.store(
                screenBitMap,
                "${myDevice.userName}_${timeStamp}.jpg",
                context!!
            )
            if (myDevice.userRole == RoleEnums.SERVER) {
                val list = Array(tambolaMembers.size - 1) { "" }

                for ((i, item) in tambolaMembers.withIndex()) {
                    if (i != 0)
                        list[i-1] = item.userName!!
                }

                if (list.isNotEmpty()) {
                    val alertDialog = AlertDialog.Builder(this.context!!, R.style.CustomDialogTheme)
                    alertDialog.setTitle("Select Recipient")

                    alertDialog.setItems(
                        list
                    ) { _, index ->
                        val user = tambolaMembers[index+1]
                        (activity as MainActivity).sendFile(user.endpointID!!, screenshotFile!!)
                    }
                    alertDialog.create()
                    alertDialog.show()
                } else {
                    (activity as MainActivity).hideProgressBar()
                    Toast.makeText(context, "No recipient available", Toast.LENGTH_SHORT).show()
                }
            } else {
                (activity as MainActivity).sendFile(tambolaMembers[0].endpointID!!, screenshotFile!!)
            }
        }
        else
            Toast.makeText(context, "Error in taking screenshot", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        /*requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (activity as MainActivity).showActionBar()*/
        root.games_rv.adapter = null
        super.onPause()
    }

    override fun setWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, winner: List<String>, winnerUserId: List<Int>) {
        (activity as MainActivity).sendAllData(ADD_WINNER, "$position`$gameId`$gameName`$gamePrice`$winner`$winnerUserId")
        (activity as MainActivity).invalidateOptionsMenu()
    }

    override fun changeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, newWinner: List<String>, newWinnerUserId: List<Int>, prevWinnerUserId: List<Int>) {
        (activity as MainActivity).sendAllData(CHANGE_WINNER, "$position`$gameId`$gameName`$gamePrice`$newWinner`$newWinnerUserId`$prevWinnerUserId")
        (activity as MainActivity).invalidateOptionsMenu()
    }

    override fun removeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, prevWinnerUserId: List<Int>) {
        (activity as MainActivity).sendAllData(REMOVE_WINNER, "$position`$gameId`$gameName`$gamePrice`$prevWinnerUserId")
        (activity as MainActivity).invalidateOptionsMenu()
    }

    companion object {
        lateinit var numberGenerator: NumberGenerator
    }
}