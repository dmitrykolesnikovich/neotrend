package site.neotrend.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import site.neotrend.common.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
