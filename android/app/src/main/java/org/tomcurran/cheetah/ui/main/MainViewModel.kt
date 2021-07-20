package org.tomcurran.cheetah.ui.main

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import org.tomcurran.cheetah.BuildConfig
import org.tomcurran.cheetah.R
import org.tomcurran.cheetah.util.Event
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val STRAVA_OAUTH_SCOPE = "activity:read_all"
        private const val STRAVA_OAUTH_AUTH_ENDPOINT = "https://www.strava.com/oauth/mobile/authorize"
        private const val STRAVA_OAUTH_TOKEN_ENDPOINT = "https://www.strava.com/api/v3/oauth/token"
    }

    private val _firebaseAuth = Firebase.auth

    private val _firebaseCustomTokenService = Retrofit.Builder()
        .baseUrl(BuildConfig.FIREBASE_CUSTOM_TOKEN_HOST)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FirebaseCustomTokenService::class.java)

    private val _authService = AuthorizationService(getApplication())

    private val _startActivityForResult = MutableLiveData<Event<Intent>>()
    val startActivityForResult: LiveData<Event<Intent>> = _startActivityForResult

    private val _loggedIn = MutableLiveData<Boolean>()

    private val _loggingIn = MutableLiveData<Boolean>()
    val loggingIn: LiveData<Boolean> = _loggingIn

    val loginLogoutText: LiveData<String> = Transformations.map(_loggedIn) {
            loggedIn -> if (loggedIn) "Logout" else "Login"
    }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    val messageVisible: LiveData<Boolean> = Transformations.map(message) {
        message -> message.isNotEmpty()
    }

    init {
        _loggingIn.value = false
        _message.value = ""
        _loggedIn.value = false

        val firebaseUser = _firebaseAuth.currentUser
        if (firebaseUser != null) {
            _message.value = "Hi, ${firebaseUser.displayName}!"
            _loggedIn.value = true
        }
    }

    fun loginLogout() {
        if (_loggedIn.value == true) {
            logout()
        } else {
            login()
        }
    }

    private fun login() {
        _loggingIn.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val authServiceConfiguration = AuthorizationServiceConfiguration(
                STRAVA_OAUTH_AUTH_ENDPOINT.toUri(),
                STRAVA_OAUTH_TOKEN_ENDPOINT.toUri()
            )
            val authRequest = AuthorizationRequest.Builder(
                authServiceConfiguration,
                BuildConfig.STRAVA_OAUTH_CLIENT_ID,
                ResponseTypeValues.CODE,
                BuildConfig.STRAVA_OAUTH_REDIRECT_URI.toUri()
            ).setScope(STRAVA_OAUTH_SCOPE).build()
            val authRequestIntent = _authService.getAuthorizationRequestIntent(
                authRequest,
                _authService.createCustomTabsIntentBuilder(authRequest.toUri())
                    .setDefaultColorSchemeParams(
                        CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(
                                ContextCompat.getColor(getApplication(), R.color.colorPrimary)
                            ).build()
                    ).build()
            )
            withContext(Dispatchers.Main) {
                _startActivityForResult.value = Event(authRequestIntent)
            }
        }
    }

    fun onActivityResult(activityResult: ActivityResult) {
        viewModelScope.launch(Dispatchers.IO) {
            var message = ""
            val data = activityResult.data
            if (activityResult.resultCode == Activity.RESULT_OK && data != null) {
                val authResponse = AuthorizationResponse.fromIntent(data)
                val authException = AuthorizationException.fromIntent(data)
                if (authResponse != null || authException != null) {
                    val authState = AuthState(authResponse, authException)
                    if (authResponse != null) {
                        val tokenRequest = authResponse.createTokenExchangeRequest(mapOf("client_secret" to BuildConfig.STRAVA_OAUTH_CLIENT_SECRET))
                        val (tokenResponse, tokenEx) = suspendCoroutine<Pair<TokenResponse?, AuthorizationException?>> { continuation ->
                            _authService.performTokenRequest(
                                tokenRequest, authState.clientAuthentication
                            ) { tokenResponse, tokenEx ->
                                continuation.resume(Pair(tokenResponse, tokenEx))
                            }
                        }
                        authState.update(tokenResponse, tokenEx);
                        val refreshToken = authState.refreshToken
                        if (authState.isAuthorized && refreshToken != null) {
                            try {
                                val firebaseTokenResponse = _firebaseCustomTokenService.firebaseToken(refreshToken)
                                val authResult = _firebaseAuth.signInWithCustomToken(firebaseTokenResponse.firebaseCustomToken).await()
                                val firebaseUser = authResult.user
                                if (firebaseUser != null) {
                                    message = "Hi, ${firebaseUser.displayName}!"
                                    withContext(Dispatchers.Main) {
                                        _loggedIn.value = true
                                    }
                                } else {
                                    message = "Error logging in"
                                }
                            } catch (_: Exception) {
                                message = "Error logging in"
                            }
                        } else {
                            message = "Not authorised"
                        }
                    } else {
                        message = "Error / Cancelled"
                    }
                }
            } else {
                message = "Error / Cancelled"
            }
            withContext(Dispatchers.Main) {
                _message.value = message
                _loggingIn.value = false
            }
        }
    }

    private fun logout() {
        _firebaseAuth.signOut()
        _message.value = ""
        _loggedIn.value = false
    }
}