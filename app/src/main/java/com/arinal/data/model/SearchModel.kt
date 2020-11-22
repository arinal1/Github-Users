package com.arinal.data.model

import com.google.gson.annotations.SerializedName

data class SearchModel(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean = false,
    val items: List<UsersModel> = listOf(),
    @SerializedName("total_count")
    val totalCount: Int = 0
)
