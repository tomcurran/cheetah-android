package org.tomcurran.cheetah.ui.main

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.tomcurran.cheetah.ui.MainActivity
import org.tomcurran.cheetah.ui.theme.CheetahTheme
import org.tomcurran.cheetah.util.EventObserver

@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val messageVisible: Boolean by mainViewModel.messageVisible.observeAsState(false)
    val message: String by mainViewModel.message.observeAsState("")
    val loggingIn: Boolean by mainViewModel.loggingIn.observeAsState(false)
    val loginLogoutText: String by mainViewModel.loginLogoutText.observeAsState("")
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        mainViewModel.onActivityResult(activityResult)
    }
    mainViewModel.startActivityForResult.observe(LocalLifecycleOwner.current, EventObserver { intent ->
        try {
            activityResultLauncher.launch(intent)
        } catch (e: Exception) {
            // intentionally left blank - onActivityResult will handle the failure
        }
    })
    MainContent(
        messageVisible = messageVisible,
        message = message,
        loggingIn = loggingIn,
        loginLogoutText = loginLogoutText,
        onLoginLogoutClick = { mainViewModel.loginLogout() },
    )
    MainActivity.keepSplashScreenVisible = false // TODO place in better location
}

@Composable
fun MainContent(
    messageVisible: Boolean,
    message: String,
    loggingIn: Boolean,
    loginLogoutText: String,
    onLoginLogoutClick: () -> Unit,
) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loggingIn) {
                CircularProgressIndicator()
            } else {
                if (messageVisible) {
                    Text(text = message)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(onClick = onLoginLogoutClick) {
                    Text(text = loginLogoutText)
                }
            }
        }
    }
}

@Preview("Main screen logged in", showBackground = true)
@Preview("Main screen logged in (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Main screen logged in (big font)", fontScale = 1.5f)
@Preview("Main screen logged in (large screen)", device = Devices.PIXEL_C)
@Composable
fun DefaultPreview() {
    CheetahTheme {
        MainContent(
            messageVisible = true,
            message = "Hi, Kirstie!",
            loggingIn = false,
            loginLogoutText = "Logout",
            onLoginLogoutClick = {},
        )
    }
}

@Preview("Main screen logged out", showBackground = true)
@Preview("Main screen logged out (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Main screen logged out (big font)", fontScale = 1.5f)
@Preview("Main screen logged out (large screen)", device = Devices.PIXEL_C)
@Composable
fun LoggedOutPreview() {
    CheetahTheme {
        MainContent(
            messageVisible = false,
            message = "",
            loggingIn = false,
            loginLogoutText = "Login",
            onLoginLogoutClick = {},
        )
    }
}

@Preview("Main screen logging in", showBackground = true)
@Preview("Main screen logging in (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Main screen logging in (big font)", fontScale = 1.5f)
@Preview("Main screen logging in (large screen)", device = Devices.PIXEL_C)
@Composable
fun LoggingInPreview() {
    CheetahTheme {
        MainContent(
            messageVisible = false,
            message = "",
            loggingIn = true,
            loginLogoutText = "",
            onLoginLogoutClick = {},
        )
    }
}
