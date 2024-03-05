package com.binay.shaw.justap.presentation.navigation

import com.binay.shaw.justap.utilities.Constants

sealed class Screens(val name: String) {
    object HomeScreen : Screens(Constants.HOME_SCREEN)
    object ConnectScreen : Screens(Constants.CONNECT_SCREEN)
    object HistoryScreen : Screens(Constants.HISTORY_SCREEN)
    object AccountScreen : Screens(Constants.ACCOUNT_SCREEN)
}


