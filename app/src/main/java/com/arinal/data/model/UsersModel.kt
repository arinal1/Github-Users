package com.arinal.data.model

import com.google.gson.annotations.SerializedName

data class UsersModel(
    @SerializedName("avatar_url")
    val avatarUrl: String = "",
    val id: Int = 0,
    val login: String = ""
) {
    fun getSmallAvatar() = "${avatarUrl}&s=100"
}
