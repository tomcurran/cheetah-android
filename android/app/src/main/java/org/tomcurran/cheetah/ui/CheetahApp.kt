package org.tomcurran.cheetah.ui

import androidx.compose.runtime.Composable
import org.tomcurran.cheetah.ui.main.MainScreen
import org.tomcurran.cheetah.ui.theme.CheetahTheme

@Composable
fun CheetahApp() {
    CheetahTheme {
        MainScreen()
    }
}
