package com.arinal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arinal.data.GithubApi
import com.arinal.data.model.UsersModel
import com.arinal.utils.Constants.MESSAGE_EMPTY
import com.arinal.utils.Event
import kotlinx.coroutines.*

class MainViewModel(private val githubApi: GithubApi) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isOnSearch = MutableLiveData(false)
    val isOnSearch: LiveData<Boolean> get() = _isOnSearch

    private val _userList = MutableLiveData(mutableListOf<UsersModel>())
    val userList: LiveData<MutableList<UsersModel>> get() = _userList

    private val _searchList = MutableLiveData(mutableListOf<UsersModel>())
    val searchList: LiveData<MutableList<UsersModel>> get() = _searchList

    private var searchJob: Job = Job()

    private var searchPage = 0

    var searchQuery = MutableLiveData("")

    private val _goToTop = MutableLiveData<Event<Unit>>()
    val goToTop: LiveData<Event<Unit>> get() = _goToTop
    fun goToTop() = _goToTop.postValue(Event(Unit))

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage

    fun clearSearchList() {
        _searchList.value = mutableListOf()
        searchPage = 0
    }

    fun clearList() {
        if (isOnSearchMode()) clearSearchList()
        else _userList.value = mutableListOf()
    }

    fun cancelSearch() {
        searchQuery.postValue("")
        _isLoading.postValue(true)
        _isOnSearch.postValue(false)
        clearSearchList()
    }

    fun initData() {
        if (isOnSearchMode() && _searchList.value.isNullOrEmpty()) searchUsers()
        else if (!isOnSearchMode() && _userList.value.isNullOrEmpty()) getUsers()
    }

    fun loadData() = if (isOnSearchMode()) searchUsers() else getUsers()

    private fun getUsers() {
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

    fun searchUsers() {
        viewModelScope.launch {
            searchJob.cancelAndJoin()
            searchJob = launch(Dispatchers.IO) {
                try {
                    _errorMessage.postValue("")
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

    fun stopSearch() {
        searchJob.cancel()
        _errorMessage.postValue("")
        _isLoading.postValue(false)
        _isOnSearch.postValue(false)
    }

    fun setOnSearchMode() = _isOnSearch.postValue(true)
    private fun isOnSearchMode() = _isOnSearch.value == true

}
