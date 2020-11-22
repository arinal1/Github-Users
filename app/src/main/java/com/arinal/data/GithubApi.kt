package com.arinal.data

import com.arinal.data.model.SearchModel
import com.arinal.data.model.UserModel
import com.arinal.data.model.UsersModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserModel

    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int,
        @Query("per_page") size: Int = 20
    ): MutableList<UsersModel>

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") size: Int = 20
    ): SearchModel

}
