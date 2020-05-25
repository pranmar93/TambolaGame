package com.example.tambolaGame.interfaces

interface WinnerChangedListener {
    fun setWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, winner: String, winnerUserId: Int)
    fun changeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, newWinner: String, newWinnerUserId: Int, prevWinnerUserId: Int)
    fun removeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, prevWinnerUserId: Int)
}