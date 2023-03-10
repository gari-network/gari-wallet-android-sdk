package io.gari.sample.network.service

import io.gari.sample.network.Api
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface DemoApiService {

    @GET(Api.Path.LOGIN)
    suspend fun getWeb3AuthToken(
        @Query(Api.Param.USER_ID) userId: String,
    ): Response<String>

    @POST(Api.Path.TRANSACTION)
    suspend fun sendTransaction(
        @Header(Api.Header.TOKEN) token: String,
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): Response<String>

    @GET(Api.Path.REFRESH_JWT_TOKEN)
    suspend fun refreshJwtToken(
        @Header(Api.Header.TOKEN) token: String,
        @QueryMap params: Map<String, @JvmSuppressWildcards Any>
    ): Response<String>
}