package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Transaksi
import com.example.data.entity.UnitUsaha
import com.example.data.entity.User
import com.example.data.repository.BumdesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class BumdesViewModel(private val repository: BumdesRepository) : ViewModel() {

    private val _themePreference = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val themePreference: StateFlow<ThemeMode> = _themePreference.asStateFlow()

    fun setThemePreference(mode: ThemeMode) {
        _themePreference.value = mode
    }

    val allUnits: StateFlow<List<UnitUsaha>> = repository.allUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransaksi: StateFlow<List<Transaksi>> = repository.allTransaksi
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPemasukan: StateFlow<Double?> = repository.totalPemasukan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPengeluaran: StateFlow<Double?> = repository.totalPengeluaran
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun insertUnit(unit: UnitUsaha) {
        viewModelScope.launch {
            repository.insertUnit(unit)
        }
    }

    fun addUnit(nama: String, deskripsi: String) {
        insertUnit(UnitUsaha(namaUnit = nama, deskripsi = deskripsi))
    }

    fun addTransaksi(jenis: String, kategori: String, nominal: Double, idUnit: Int?, keterangan: String, status: String = "Disetujui") {
        viewModelScope.launch {
            val noTrx = "TRX-\${System.currentTimeMillis()}"
            val trx = Transaksi(
                nomorTransaksi = noTrx,
                jenis = jenis,
                kategori = kategori,
                nominal = nominal,
                idUnit = idUnit,
                keterangan = keterangan,
                status = status
            )
            repository.insertTransaksi(trx)
        }
    }
    
    fun updateTransaksiStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateTransaksiStatus(id, status)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }
}

class BumdesViewModelFactory(private val repository: BumdesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BumdesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BumdesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
