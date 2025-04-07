package site.neotrend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow

private typealias Screen = @Composable () -> Unit

class Navigation {

    private val screens: HashMap<String, Screen> = HashMap()
    private lateinit var currentScreen: MutableStateFlow<String>

    fun screen(name: String, screen: Screen) {
        screens[name] = screen
    }

    fun navigate(screen: String) {
        currentScreen.value = screen
    }

    @Composable
    fun host(initialScreen: String) {
        currentScreen = remember { MutableStateFlow(initialScreen) }
        val currentScreen: String by currentScreen.collectAsState()
        screens[currentScreen]?.invoke()
    }

}
