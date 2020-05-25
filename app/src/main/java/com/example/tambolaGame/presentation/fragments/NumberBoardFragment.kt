package com.example.tambolaGame.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tambolaGame.R
import com.example.tambolaGame.presentation.adapters.NumberBoardAdapter
import kotlinx.android.synthetic.main.fragment_number_board.view.*

class NumberBoardFragment: Fragment() {

    private lateinit var root: View
    private lateinit var numberBoardAdapter: NumberBoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_number_board, container, false)

        numberBoardAdapter = NumberBoardAdapter(context!!)

        root.numberBoard_rv.adapter = numberBoardAdapter

        return root
    }
}