package com.example.tambolaGame.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.MainActivity.Companion.myDevice
import com.example.tambolaGame.MainActivity.Companion.tambolaMembers
import com.example.tambolaGame.R
import com.example.tambolaGame.models.UserDevice
import com.example.tambolaGame.presentation.adapters.ParticipantsWalletAdapter
import kotlinx.android.synthetic.main.fragment_participants_wallet.view.*


class ParticipantsWalletFragment: Fragment() {

    private lateinit var root: View
    private lateinit var type: String
    private lateinit var participantsWalletAdapter: ParticipantsWalletAdapter
    private lateinit var adapterList: List<UserDevice>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_participants_wallet, container, false)

        refreshView(arguments!!.getString("TYPE")!!)
        arguments!!.remove("TYPE")
        return root
    }

    fun refreshView(Type: String) {
        type = Type
        adapterList = if (type == "Wallet") {
            (activity as MainActivity).supportActionBar!!.title = "Money Holder - " + myDevice.userId.toString()
            tambolaMembers.filter { user -> user.walletMoney > 0 }
        } else {
            (activity as MainActivity).supportActionBar!!.title = "Joined Members - " + myDevice.userId.toString()
            tambolaMembers
        }

        participantsWalletAdapter = ParticipantsWalletAdapter(context!!, type, adapterList)
        root.members_rv.adapter = participantsWalletAdapter
    }

    fun getType(): String {
        return type
    }
 }