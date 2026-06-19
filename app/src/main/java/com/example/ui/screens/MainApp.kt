package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.viewmodel.BumdesViewModel
import com.example.ui.theme.*

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Beranda")
    object UnitUsaha : Screen("unit_usaha", "Unit")
    object Transaksi : Screen("transaksi", "Keuangan")
    object User : Screen("user", "Profil")
}

@Composable
fun MainApp(viewModel: BumdesViewModel) {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.UnitUsaha, Screen.Transaksi, Screen.User)

    val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
    val isDarkTheme = when (themePref) {
        com.example.viewmodel.ThemeMode.LIGHT -> false
        com.example.viewmodel.ThemeMode.DARK -> true
        com.example.viewmodel.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = if (isDarkTheme) R.drawable.bg_dark_1781839051163 else R.drawable.bg_light_1781839037656),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        // subtle overlay for text readability
        Box(modifier = Modifier.matchParentSize().background(if (isDarkTheme) Color.Black.copy(alpha=0.4f) else Color.White.copy(alpha=0.6f)))

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    tonalElevation = 8.dp
                ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            when (screen) {
                                Screen.Dashboard -> Icon(Icons.Filled.Home, contentDescription = "Beranda")
                                Screen.UnitUsaha -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Unit Usaha")
                                Screen.Transaksi -> Icon(Icons.Filled.ShoppingCart, contentDescription = "Keuangan")
                                Screen.User -> Icon(Icons.Filled.Person, contentDescription = "Profil")
                            }
                        },
                        label = { Text(screen.label) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Dashboard.route, Modifier.padding(innerPadding)) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(Screen.Transaksi.route) { TransaksiScreen(viewModel) }
            composable(Screen.UnitUsaha.route) { UnitUsahaScreen(viewModel) }
            composable(Screen.User.route) { UserProfileScreen(viewModel) }
        }
    }
    }
}
