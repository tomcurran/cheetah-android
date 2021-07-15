package org.tomcurran.cheetah.ui.login

import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseCustomTokenService {
    data class FirebaseTokenResponse(val firebaseCustomToken: String)

    @POST("firebaseToken")
    suspend fun firebaseToken(@Query("strava_refresh_token") stravaRefreshToken: String): FirebaseTokenResponse
}
