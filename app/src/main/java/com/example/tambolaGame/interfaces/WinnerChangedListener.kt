package com.example.tambolaGame.interfaces

interface WinnerChangedListener {
    fun setWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, winner: List<String>, winnerUserId: List<Int>)
    fun changeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, newWinner: List<String>, newWinnerUserId: List<Int>, prevWinnerUserId: List<Int>)
    fun removeWinnerData(position: Int, gameId: Int, gameName: String, gamePrice: Int, prevWinnerUserId: List<Int>)
}