package com.arinal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arinal.data.GithubApi
import com.arinal.data.model.UsersModel
import com.arinal.utils.Constants.MESSAGE_EMPTY
import com.arinal.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val githubApi: GithubApi) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _userList = MutableLiveData(mutableListOf<UsersModel>())
    val userList: LiveData<MutableList<UsersModel>> get() = _userList

    private val _goToTop = MutableLiveData<Event<Unit>>()
    val goToTop: LiveData<Event<Unit>> get() = _goToTop
    fun goToTop() = _goToTop.postValue(Event(Unit))

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage

    fun clearUserList() {
        _userList.value = mutableListOf()
    }

    fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _errorMessage.postValue("")
                _isLoading.postValue(true)
                val lastUserId = _userList.value?.lastOrNull()?.id ?: 0
                val data = githubApi.getUsers(lastUserId)
                val userList = mutableListOf<UsersModel>().apply {
                    addAll(_userList.value ?: listOf())
                    addAll(data)
                }
                _userList.postValue(userList)
                if (userList.isEmpty()) _errorMessage.postValue(MESSAGE_EMPTY)
            } catch (t: Throwable) {
                _errorMessage.postValue(t.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}
