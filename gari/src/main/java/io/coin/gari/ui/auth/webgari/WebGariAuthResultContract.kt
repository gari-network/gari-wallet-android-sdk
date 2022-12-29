package io.coin.gari.ui.auth.webgari

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import io.coin.gari.domain.entity.WalletKeyResult
import io.coin.gari.ui.auth.core.AuthResult

internal class WebGariAuthResultContract : ActivityResultContract<String, WalletKeyResult>() {

    override fun createIntent(context: Context, input: String): Intent {
        return WebGariAuthActivity.buildIntent(
            context = context,
            jwtToken = input
        )
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): WalletKeyResult {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                intent
                    ?.extras
                    ?.getParcelable<WalletKeyResult>(AuthResult.WALLET_KEY_RESULT)
                    ?: return WalletKeyResult.Failure
            }

            else -> {
                WalletKeyResult.Canceled
            }
        }
    }
}