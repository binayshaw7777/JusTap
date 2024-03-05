package com.binay.shaw.justap.presentation.mainScreens.historyScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.model.LocalHistory
import com.binay.shaw.justap.repository.LocalHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LocalHistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    val accountListLiveData = MutableLiveData<List<LocalHistory>>()

    val getAllHistory : LiveData<List<LocalHistory>>
    private val repository: LocalHistoryRepository

    init {
        val dao = LocalUserDatabase.getDatabase(application).localUserHistoryDao()
        repository = LocalHistoryRepository(dao)
        getAllHistory = repository.getAllHistory
    }

    fun insertUserHistory(localHistory: LocalHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUserHistory(localHistory)
        }
    }

    fun deleteUserHistory(localHistory: LocalHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUserHistory(localHistory)
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
        }
    }

}