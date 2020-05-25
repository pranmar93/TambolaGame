package com.example.tambolaGame.models

data class UserDevice (
    var userId: Int,
    var userName: String?,
    var endpointID: String?,
    var phone: String?,
    var walletMoney: Int = 0
)