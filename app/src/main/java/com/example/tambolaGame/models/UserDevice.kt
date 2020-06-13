package com.example.tambolaGame.models

import com.example.tambolaGame.utils.RoleEnums

data class UserDevice (
    var userId: Int,
    var userName: String?,
    var endpointID: String?,
    var phone: String?,
    var walletMoney: Int = 0,
    var userRole: Enum<RoleEnums>
)