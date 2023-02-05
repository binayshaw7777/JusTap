package com.binay.shaw.justap.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.repository.AccountsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 30,January,2023
 */
class AccountsViewModel(
    application: Application
) : AndroidViewModel(application) {


    val getAllUser : LiveData<List<Accounts>>
    private val repository: AccountsRepository


    init {
        val dao = LocalUserDatabase.getDatabase(application).accountsDao()
        repository = AccountsRepository(dao)
        getAllUser = repository.getAccountsList
    }

    fun deleteAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccount(accounts)
        }

    fun updateAccount(newData: String, id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(newData, id)
        }

    fun insertAccount(accounts: Accounts) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAccount(accounts)
        }

    fun deleteEntryById(accountID: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntryById(accountID)
    }

}