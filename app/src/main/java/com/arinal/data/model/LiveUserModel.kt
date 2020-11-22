package com.arinal.data.model

import androidx.lifecycle.MutableLiveData

data class LiveUserModel(
    val avatarUrl: MutableLiveData<String> = MutableLiveData(""),
    val blog: MutableLiveData<String> = MutableLiveData(""),
    val company: MutableLiveData<String> = MutableLiveData(""),
    val email: MutableLiveData<String> = MutableLiveData(""),
    val id: MutableLiveData<Int> = MutableLiveData(0),
    val location: MutableLiveData<String> = MutableLiveData(""),
    val login: MutableLiveData<String> = MutableLiveData(""),
    val name: MutableLiveData<String> = MutableLiveData("")
) {
    fun postValue(user: UserModel) {
        avatarUrl.postValue(user.avatarUrl)
        blog.postValue(user.blog ?: "")
        company.postValue(user.company ?: "")
        email.postValue(user.email ?: "")
        id.postValue(user.id)
        location.postValue(user.location ?: "")
        login.postValue(user.login)
        name.postValue(user.name ?: "")
    }

    fun resetValue() {
        avatarUrl.postValue("")
        blog.postValue("")
        company.postValue("")
        email.postValue("")
        id.postValue(0)
        location.postValue("")
        login.postValue("")
        name.postValue("")
    }
}
