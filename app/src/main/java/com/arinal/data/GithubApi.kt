package com.arinal.data

import com.arinal.data.model.UsersModel
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int,
        @Query("per_page") size: Int = 20
    ): MutableList<UsersModel>

}
