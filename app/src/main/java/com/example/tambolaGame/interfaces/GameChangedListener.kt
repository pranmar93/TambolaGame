package com.example.tambolaGame.interfaces

import com.example.tambolaGame.models.Game

interface GameChangedListener {
    fun isGameListEmpty(gameList: ArrayList<Game>)
    fun updateActionSubTitle()
}