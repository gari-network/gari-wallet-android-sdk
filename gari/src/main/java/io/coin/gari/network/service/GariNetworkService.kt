package io.coin.gari.network.service

import com.google.gson.reflect.TypeToken
import io.coin.gari.exceptions.InvalidResponseBodyException
import io.coin.gari.exceptions.WalletNotRegisteredException
import io.coin.gari.network.Api
import io.coin.gari.network.core.HttpCode
import io.coin.gari.network.core.NetworkClient
import io.coin.gari.network.entity.ApiGariWallet
import io.coin.gari.network.response.GariResponse
import io.coin.gari.network.response.WalletDetailsResponse

internal class GariNetworkService(
    private val networkClient: NetworkClient
) {

    fun getWalletDetails(
        gariClientId: String,
        token: String
    ): Result<ApiGariWallet> {
        return try {
            val apiWalletResponse: WalletDetailsResponse = networkClient.get(
                gariClientId = gariClientId,
                token = token,
                path = Api.Path.WALLET_DETAILS,
                responseType = TypeToken.getParameterized(WalletDetailsResponse::class.java).type
            )

            when (apiWalletResponse.code) {
                HttpCode.NOT_FOUND.code -> {
                    throw WalletNotRegisteredException()
                }

                HttpCode.SUCCESS.code -> {
                    val apiWallet = apiWalletResponse.data
                        ?: throw InvalidResponseBodyException()

                    Result.success(apiWallet)
                }

                else -> {
                    throw InvalidResponseBodyException()
                }
            }
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    fun createWallet(
        gariClientId: String,
        token: String,
        pubKey: String
    ): Result<ApiGariWallet> {
        return try {
            val params = hashMapOf(
                Api.Param.PUBLIC_KEY to pubKey
            )

            val apiWalletResponse = networkClient.post<GariResponse<ApiGariWallet>>(
                gariClientId = gariClientId,
                token = token,
                path = Api.Path.WALLET_CREATE,
                params = params,
                responseType = TypeToken.getParameterized(
                    GariResponse::class.java,
                    ApiGariWallet::class.java
                ).type
            )

            val apiWallet = apiWalletResponse.data

            if (apiWalletResponse.code != HttpCode.SUCCESS.code
                || apiWallet == null
            ) {
                throw InvalidResponseBodyException()
            }

            Result.success(apiWallet)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    fun getEncodedAirdropTransaction(
        gariClientId: String,
        token: String,
        pubKey: String,
        airdropAmount: String
    ): Result<String> {
        return try {
            val params = hashMapOf(
                Api.Param.PUBLIC_KEY to pubKey,
                Api.Param.AIRDROP_AMOUNT to airdropAmount,
            )

            val response = networkClient.post<GariResponse<String>>(
                gariClientId = gariClientId,
                token = token,
                path = Api.Path.AIRDROP_GET_ENCODED_TRANSACTION,
                params = params,
                responseType = TypeToken.getParameterized(
                    GariResponse::class.java,
                    String::class.java
                ).type
            )

            val encodedTransaction = response.data

            if (encodedTransaction.isNullOrEmpty()) {
                return Result.failure(InvalidResponseBodyException())
            }

            Result.success(encodedTransaction)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    fun sendSignedAirdropTransaction(
        gariClientId: String,
        token: String,
        pubKey: String,
        airdropAmount: String,
        encodedTransaction: String,
    ): Result<String> {
        return try {
            val params = hashMapOf(
                Api.Param.PUBLIC_KEY to pubKey,
                Api.Param.AIRDROP_AMOUNT to airdropAmount,
                Api.Param.ENCODED_TRANSACTION to encodedTransaction,
            )

            val response = networkClient.post<GariResponse<String>>(
                gariClientId = gariClientId,
                token = token,
                path = Api.Path.AIRDROP_SEND_SIGNED_TRANSACTION,
                params = params,
                responseType = TypeToken.getParameterized(
                    GariResponse::class.java,
                    String::class.java
                ).type
            )

            val transactionSignature = response.data

            if (transactionSignature.isNullOrEmpty()) {
                return Result.failure(InvalidResponseBodyException("Missing airdrop transaction signature"))
            }

            Result.success(transactionSignature)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    fun getEncodedTransaction(
        gariClientId: String,
        token: String,
        receiverPublicKey: String,
        transactionAmount: String
    ): Result<String> {
        return try {
            val params = hashMapOf(
                Api.Param.RECEIVER_PUBLIC_KEY to receiverPublicKey,
                Api.Param.AMOUNT to transactionAmount,
            )

            val response = networkClient.post<GariResponse<String>>(
                gariClientId = gariClientId,
                token = token,
                path = Api.Path.GET_SIGNED_TRANSACTION,
                params = params,
                responseType = TypeToken.getParameterized(
                    GariResponse::class.java,
                    String::class.java
                ).type
            )

            val encodedTransaction = response.data

            if (encodedTransaction.isNullOrEmpty()) {
                return Result.failure(InvalidResponseBodyException())
            }

            Result.success(encodedTransaction)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    fun getTransactionsHistory() {

    }
}