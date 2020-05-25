package com.example.tambolaGame.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tambolaGame.R
import com.example.tambolaGame.presentation.adapters.WinnerAdapter
import kotlinx.android.synthetic.main.fragment_winner.view.*

class WinnerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_winner, container, false)

        root.winner_rv.adapter = WinnerAdapter(context!!)

        return root
    }
}