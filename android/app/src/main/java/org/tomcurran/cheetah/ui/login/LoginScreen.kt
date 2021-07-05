package org.tomcurran.cheetah.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.tomcurran.cheetah.ui.theme.CheetahTheme

@Composable
fun LoginScreen() {
    LoginContent("Android")
}

@Composable
fun LoginContent(name: String) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Hello $name!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text("Login!")
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
        LoginContent("Android")
    }
}
