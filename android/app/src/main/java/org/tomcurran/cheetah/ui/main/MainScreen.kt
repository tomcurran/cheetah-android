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
    val debugInfoVisible: Boolean by mainViewModel.debugInfoVisible.observeAsState(false)
    val debugInfo: String by mainViewModel.debugInfo.observeAsState("")
    val loggingIn: Boolean by mainViewModel.loggingIn.observeAsState(false)
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
        debugInfoVisible = debugInfoVisible,
        debugInfo = debugInfo,
        loggingIn = loggingIn,
        onLoginClick = { mainViewModel.login() },
    )
    MainActivity.keepSplashScreenVisible = false // TODO place in better location
}

@Composable
fun MainContent(
    debugInfoVisible: Boolean,
    debugInfo: String,
    loggingIn: Boolean,
    onLoginClick: () -> Unit,
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
                if (debugInfoVisible) {
                    Text(text = debugInfo)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(
                    onClick = onLoginClick,
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
}

@Preview("Main screen", showBackground = true)
@Preview("Main screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Main screen (big font)", fontScale = 1.5f)
@Preview("Main screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun DefaultPreview() {
    CheetahTheme {
        MainContent(
            debugInfoVisible = true,
            debugInfo = "Access token: test...",
            loggingIn = false,
            onLoginClick = {},
        )
    }
}
