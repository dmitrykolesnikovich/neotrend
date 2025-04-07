package site.neotrend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import site.neotrend.SheetStep.*
import site.neotrend.platform.VideoPlayer

@Composable
fun App() {
    val navigation: Navigation = remember { Navigation() }
    navigation.screen("welcome") { Welcome(navigation) }
    navigation.screen("player") { Player(navigation) }
    navigation.host("welcome")
}

@Composable
private fun Welcome(navigation: Navigation) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { navigation.navigate("player") }) { Text("Открыть видео блогера") }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun Player(navigation: Navigation) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    // model
    var appState: AppState by remember { mutableStateOf(EmptyAppState()) }
    fun updateAppState() = coroutineScope.launch(Dispatchers.IO) {
        appState = readAppState()
    }

    // view
    val sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var sheetStep: SheetStep by remember { mutableStateOf(PAYMENT) }
    fun updateSheetStep(newState: SheetStep) = coroutineScope.launch {
        sheetState.hide()
        sheetStep = newState
        if (sheetStep != INITIAL) {
            sheetState.show()
        }
    }

    if (appState.isEmpty) {
        updateAppState()
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    } else {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetContent = {
                IconButton(onClick = { updateSheetStep(INITIAL) }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
                when (sheetStep) {
                    INITIAL -> {
                        // hidden
                    }
                    PAYMENT -> {
                        Text("PAY")
                        Button(onClick = { updateSheetStep(MESSAGE) }) { Text("MESSAGE") }
                    }
                    MESSAGE -> {
                        Text("MESSAGE")
                        Button(onClick = { updateSheetStep(SUCCESS) }) { Text("SUCCESS") }
                    }
                    SUCCESS -> {
                        Text("SUCCESS")
                        Button(onClick = { updateSheetStep(INITIAL) }) { Text("INITIAL") }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                VideoPlayer(modifier = Modifier.fillMaxSize(), appState.author.fileName)
                Button(onClick = { updateSheetStep(PAYMENT) }) { Text("Заказать обзор у блогера") }
            }
        }
    }
}

private enum class SheetStep {
    INITIAL,
    PAYMENT,
    MESSAGE,
    SUCCESS,
}
