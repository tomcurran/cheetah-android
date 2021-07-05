package org.tomcurran.cheetah.ui

import androidx.compose.runtime.Composable
import org.tomcurran.cheetah.ui.login.LoginScreen
import org.tomcurran.cheetah.ui.theme.CheetahTheme

@Composable
fun CheetahApp() {
    CheetahTheme {
        LoginScreen()
    }
}
