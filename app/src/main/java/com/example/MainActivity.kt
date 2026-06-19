package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.repository.BumdesRepository
import com.example.ui.screens.MainApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BumdesViewModel
import com.example.viewmodel.BumdesViewModelFactory
import com.example.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val db = Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java, "bumdes_database"
    ).build()

    val repository = BumdesRepository(db.userDao(), db.unitUsahaDao(), db.transaksiDao())
    val factory = BumdesViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[BumdesViewModel::class.java]

    setContent {
      val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
      val darkTheme = when (themePref) {
          ThemeMode.LIGHT -> false
          ThemeMode.DARK -> true
          ThemeMode.SYSTEM -> isSystemInDarkTheme()
      }

      MyApplicationTheme(darkTheme = darkTheme) {
        MainApp(viewModel)
      }
    }
  }
}
