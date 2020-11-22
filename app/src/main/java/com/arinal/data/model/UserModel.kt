package com.arinal.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("avatar_url")
    val avatarUrl: String = "",
    val blog: String? = "",
    val company: String? = "",
    val email: String? = "",
    val id: Int = 0,
    val location: String? = "",
    val login: String = "",
    val name: String? = ""
)
