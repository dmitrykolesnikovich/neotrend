package site.neotrend.ios

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import site.neotrend.App

@Suppress("unused")
fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
