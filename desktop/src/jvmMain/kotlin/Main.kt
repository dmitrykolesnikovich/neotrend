package site.neotrend.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import site.neotrend.App
import site.neotrend.examples.example1

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
//        example1()
    }
}
