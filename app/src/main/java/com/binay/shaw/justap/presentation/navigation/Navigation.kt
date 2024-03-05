package com.binay.shaw.justap.presentation.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.account.AccountScreen
import com.binay.shaw.justap.presentation.connect.ConnectScreen
import com.binay.shaw.justap.presentation.history.HistoryScreen
import com.binay.shaw.justap.presentation.home.HomeScreen
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.FadeIn
import com.binay.shaw.justap.presentation.themes.FadeOut
import com.binay.shaw.justap.utilities.composeUtils.BackPressCompose
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ComposableDestinationInComposeScope", "ComposableNavGraphInComposeScope")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    activity: Activity,
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val screensWithoutNavBar = persistentListOf(
        ""
    )

    BackPressCompose()

    Scaffold(
//        modifier = modifier,
        bottomBar = {
            ShowBottomNavigationBar(
                backStackEntry,
                screensWithoutNavBar,
                navController
            )

        },
        contentWindowInsets = WindowInsets(0.dp)
    ) {


        NavHost(
            navController,
            startDestination = Screens.HomeScreen.name,
            modifier = Modifier.padding(it),
            enterTransition = { FadeIn },
            exitTransition = { FadeOut },
            popEnterTransition = { FadeIn },
            popExitTransition = { FadeOut },
        ) {

            composable(route = Screens.HomeScreen.name) {
                HomeScreen()
            }

            composable(route = Screens.ConnectScreen.name) {
                ConnectScreen()
            }

            composable(route = Screens.HistoryScreen.name) {
                HistoryScreen()
            }

            composable(route = Screens.AccountScreen.name) {
                AccountScreen()
            }
        }
    }
}


@Composable
fun ShowBottomNavigationBar(
    backStackEntry: State<NavBackStackEntry?>,
    screensWithoutNavBar: PersistentList<String>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    if (backStackEntry.value?.destination?.route !in screensWithoutNavBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background
        ) {

            val bottomNavItems = listOf(
                BottomNavItem(
                    name = "Home",
                    route = Screens.HomeScreen.name,
                    icon = ImageVector.vectorResource(R.drawable.bottom_nav_item_home)
                ),
                BottomNavItem(
                    name = "Connect",
                    route = Screens.ConnectScreen.name,
                    icon = ImageVector.vectorResource(R.drawable.bottom_nav_item_connect)
                ),
                BottomNavItem(
                    name = "History",
                    route = Screens.HistoryScreen.name,
                    icon = ImageVector.vectorResource(R.drawable.bottom_nav_item_history)
                ),
                BottomNavItem(
                    name = "Account",
                    route = Screens.AccountScreen.name,
                    icon = ImageVector.vectorResource(R.drawable.bottom_nav_item_account)
                )
            )

            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    modifier = modifier,
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = if (backStackEntry.value?.destination?.route == item.route)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            text = item.name,
                            fontFamily = DMSansFontFamily,
                            color = if (backStackEntry.value?.destination?.route == item.route)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (backStackEntry.value?.destination?.route == item.route)
                                FontWeight.SemiBold
                            else
                                FontWeight.Normal,
                        )
                    },
                    selected = backStackEntry.value?.destination?.route == item.route,
                    onClick = {
                        val currentDestination =
                            navController.currentBackStackEntry?.destination?.route
                        if (item.route != currentDestination) {
                            navController.navigate(item.route) {
                                navController.graph.findStartDestination().let { route ->
                                    popUpTo(route.id) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}
