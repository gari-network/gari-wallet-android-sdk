# Gari Wallet Android SDK

Provides solution for easy implementation custodial Gari Wallet in your products. Web3Auth is used as a layer for saving wallet credentials. Check Web3Auth documentation to get more information (https://web3auth.io/docs/)


## 💡 Features
- create wallet
- get wallet state (activated, not registered yet)
- requesting airdrop
- transfer Gari token

## ⏪ Requirements

- Android API version 24 or newer is required.

## ⚡ Installation

### Add Gari to Gradle

...coming soon. For now it's possible to connect via library module.

### Permissions

Open your app's `AndroidManifest.xml` file and add the following permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
## 💥 Initialization

- Extend your Application class from GariApp

```kotlin
class DemoApplication : GariApp() {

    override fun onCreate() {
        super.onCreate()

        ...
    }
}
```

- initialize Gari with your associated client id (ask support for that)

```kotlin
class DemoApplication : GariApp() {

    override fun onCreate() {
        super.onCreate()

        Gari.initialize("...")
    }
}
``` 



- Start using Gari..

## 💥 Usage

Gari wallet SDK is completely friendly with coroutines API. It should be easy to implement into every project with MVVM architecture.

### Get Wallet State

Here is example how you can retrieve wallet state by user's token:

```kotlin
class WalletDetailsViewModel : ViewModel() {

    val walletState = MutableLiveData<GariWalletState>()

    init {
        loadWalletDetails()
    }

    private fun loadWalletDetails() {
        viewModelScope.launch {
            val state = Gari.getWalletState(web3AuthToken)
            walletState.postValue(state)
        }
    }
}
```

Design of GariWalletState class allow to properly handle every situation on your UI:

```kotlin
sealed class GariWalletState {

    class Activated(val pubKey: String, val balance: String) : GariWalletState()

    object NotExist : GariWalletState()

    class Error(val error: Throwable?) : GariWalletState()
}
```

### Create Wallet

##### *Important:
Token of the user should refreshed before calling function Gari.createWallet(). This limitation is coming from Web3Auth SDK.

What is WalletKeyManager and where you can get it - check "Key Management" documentation below on the page

```kotlin
class WalletDetailsViewModel : ViewModel() {

    fun registerWallet(keyManager: WalletKeyManager, userJwtToken : String) {
        viewModelScope.launch {
            val walletResult = Gari.createWallet(
                keyManager = keyManager,
                token = userJwtToken
            )
        }
    }
}
```

### Transfer Gari Token

##### *Important:
Token of the user should refreshed before calling function Gari.transferGariToken(). This limitation is coming from Web3Auth SDK.

Every transaction consist of 2 steps:
1. Create signed transaction through Gari Wallet Sdk
2. Send transaction into your backend for future processing

For getting signed transaction you can follow this example:
```kotlin
class WalletDetailsViewModel : ViewModel() {

    fun sendTransaction(walletKeyManager: WalletKeyManager) {
        val receiverPublicKey = ""
        val transactionAmount = "1" // 1 GARI
        val user userJwtToken = ""

        val amount = transactionAmount.toBigDecimalOrNull()
            ?.toLamports()
            ?.toString()
            ?: return

        viewModelScope.launch {
            Gari.transferGariToken(
                token = userJwtToken,
                keyManager = walletKeyManager,
                receiverPublicKey = receiverPublicKey,
                transactionAmount = amount
            ).onSuccess { signedTransaction ->
                /* send transaction into your backend for future processing */
            }.onFailure {
                /* handle error on UI */
            }
        }
    }
}
```

## 🌟 Key Management

For operations which are required wallet private key (like transactions), you have instantiate WalletKeyManager, which is part of Gari Wallet SDK

```kotlin
object Gari {
    
    ...

    fun provideWalletKeyManager(resultCaller: ActivityResultCaller): WalletKeyManager {
        return WalletKeyManager(resultCaller)
    }
    
    ...
}
```

This action depends on Activity Result APIs - so you have to create it before .onCreate() lifecycle function of your Activity or Fragment

```kotlin
class WalletDetailsActivity : AppCompatActivity() {

    private val walletKeyManager = Gari.provideWalletKeyManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ...
    }
}

```