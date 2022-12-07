package io.gari.sample.network.service

import io.gari.sample.network.Api
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApiService {

    @GET(Api.Path.LOGIN)
    suspend fun getWeb3AuthToken(
        @Query(Api.Param.USER_ID) userId: String,
    ): Response<String>
}