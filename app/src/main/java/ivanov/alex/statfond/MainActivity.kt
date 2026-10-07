package ivanov.alex.statfond

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ivanov.alex.statfond.presentation.AdTestingScreen
import ivanov.alex.statfond.presentation.LogoLoadingScreen
import ivanov.alex.statfond.presentation.MyGameScreen
import ivanov.alex.statfond.presentation.rememberInterstitialAdManager
import ivanov.alex.statfond.ui.theme.StatfondTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StatfondTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App()
                    LogoLoadingScreen()
                //StickyBannerSlot("demo-banner-yandex",320.dp)
                    StickyBannerSlot("demo-banner-yandex")
                    rememberInterstitialAdManager()


                    AdTestingScreen()


                    //Greeting(
                    //    name = "Android",
                    //    modifier = Modifier.padding(innerPadding)
                    //)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StatfondTheme {
        Greeting("Android")
    }
}