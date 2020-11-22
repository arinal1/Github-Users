package com.arinal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arinal.data.GithubApi
import com.arinal.data.model.LiveUserModel
import com.arinal.data.model.UsersModel
import com.arinal.utils.Constants.MESSAGE_EMPTY
import com.arinal.utils.Event
import kotlinx.coroutines.*

class MainViewModel(private val githubApi: GithubApi) : ViewModel() {

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage
    private fun clearMessage() = _errorMessage.postValue("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isOnSearch = MutableLiveData(false)
    fun isOnSearch() = _isOnSearch.value == true

    private val _goToTop = MutableLiveData<Event<Unit>>()
    val goToTop: LiveData<Event<Unit>> get() = _goToTop
    fun goToTop() = _goToTop.postValue(Event(Unit))

    private val _goToBlog = MutableLiveData<Event<Unit>>()
    val goToBlog: LiveData<Event<Unit>> get() = _goToBlog
    fun goToBlog() = _goToBlog.postValue(Event(Unit))

    private val _searchList = MutableLiveData(mutableListOf<UsersModel>())
    val searchList: LiveData<MutableList<UsersModel>> get() = _searchList

    private val _sendEmail = MutableLiveData<Event<Unit>>()
    val sendEmail: LiveData<Event<Unit>> get() = _sendEmail
    fun sendEmail() = _sendEmail.postValue(Event(Unit))

    private val _userList = MutableLiveData(mutableListOf<UsersModel>())
    val userList: LiveData<MutableList<UsersModel>> get() = _userList

    private var lastSearchQuery = ""
    private var job: Job = Job()
    private var searchPage = 0
    var searchQuery = MutableLiveData("")
    var selectedIndex = 0
    val selectedUser = LiveUserModel()

    private fun clearSearchList() {
        _searchList.value = mutableListOf()
        searchPage = 0
    }

    fun clearList() {
        if (isOnSearch()) clearSearchList()
        else _userList.value = mutableListOf()
    }

    fun initData() {
        if (isOnSearch() && _searchList.value.isNullOrEmpty()) searchUsers()
        else if (!isOnSearch() && _userList.value.isNullOrEmpty()) getUsers()
    }

    fun loadData() = if (isOnSearch()) searchUsers() else getUsers()

    private fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clearMessage()
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

    private fun searchUsers() {
        viewModelScope.launch {
            job.cancelAndJoin()
            job = launch(Dispatchers.IO) {
                try {
                    clearMessage()
                    _isLoading.postValue(true)
                    val data = githubApi.searchUsers(searchQuery.value ?: "", ++searchPage)
                    if (data.totalCount == 0) _errorMessage.postValue(MESSAGE_EMPTY)
                    else {
                        val searchList = mutableListOf<UsersModel>().apply {
                            addAll(_searchList.value ?: listOf())
                            addAll(data.items)
                        }
                        _searchList.postValue(searchList)
                    }
                } catch (e: CancellationException) {
                } catch (t: Throwable) {
                    _errorMessage.postValue(t.message)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun cancelSearch() {
        lastSearchQuery = ""
        searchQuery.postValue("")
        _isLoading.postValue(true)
        clearSearchList()
    }

    fun startSearch() {
        if (lastSearchQuery != searchQuery.value) {
            lastSearchQuery = searchQuery.value ?: ""
            _isOnSearch.postValue(true)
            clearSearchList()
            searchUsers()
        }
    }

    fun stopSearch() {
        stopJob()
        _isOnSearch.postValue(false)
    }

    private fun stopJob() {
        job.cancel()
        clearMessage()
        _isLoading.postValue(false)
    }

    fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            job.cancelAndJoin()
            selectedUser.resetValue()
            job = launch {
                try {
                    clearMessage()
                    _isLoading.postValue(true)
                    val username = if (isOnSearch()) _searchList.value?.get(selectedIndex)?.login ?: ""
                    else _userList.value?.get(selectedIndex)?.login ?: ""
                    val user = githubApi.getUser(username)
                    selectedUser.postValue(user)
                } catch (e: CancellationException) {
                } catch (t: Throwable) {
                    _errorMessage.postValue(t.message)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun stopGetUser() {
        stopJob()
        selectedUser.resetValue()
        selectedIndex = 0
    }

}
