package org.tomcurran.cheetah.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.tomcurran.cheetah.ui.MainActivity
import org.tomcurran.cheetah.ui.theme.CheetahTheme

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val name: String by loginViewModel.name.observeAsState("")
    if (name.isNotEmpty()) {
        MainActivity.keepSplashScreenVisible = false
    }
    val loggingIn: Boolean by loginViewModel.loggingIn.observeAsState(false)
    LoginContent(
        name = name,
        loggingIn = loggingIn,
        onLoginClick = { loginViewModel.login() },
    )
}

@Composable
fun LoginContent(name: String, loggingIn: Boolean, onLoginClick: () -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Hello $name!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = !loggingIn,
                onClick = onLoginClick,
            ) {
                Text(text = "Login!")
            }
        }
    }
}

@Preview("Login screen", showBackground = true)
@Preview("Login screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Login screen (big font)", fontScale = 1.5f)
@Preview("Login screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun DefaultPreview() {
    CheetahTheme {
        LoginContent(
            name = "Android",
            loggingIn = false,
            onLoginClick = {},
        )
    }
}
