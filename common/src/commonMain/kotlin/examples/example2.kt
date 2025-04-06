package site.neotrend.common.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import site.neotrend.common.App
import site.neotrend.common.Navigation

@Composable
fun example2() {
    println("example2")
    val navigation: Navigation = remember { Navigation() }
    navigation.screen("example1") { example1(navigation) }
    navigation.screen("App") { App(navigation) }
    navigation.host("example1")
}
