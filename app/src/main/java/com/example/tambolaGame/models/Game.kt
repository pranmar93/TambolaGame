package com.example.tambolaGame.models

data class Game (
    var gameId: Int,
    var gameName: String,
    var gamePrice: Int,
    var gameWinner: UserDevice?
)