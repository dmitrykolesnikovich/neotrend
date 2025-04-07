package site.neotrend

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import site.neotrend.SheetStep.*
import site.neotrend.platform.VideoPlayer
import site.neotrend.platform.bitmap
import site.neotrend.platform.epochMillis

private enum class SheetStep {
    INITIAL, PAYMENT, MESSAGE, SUCCESS,
}

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
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun Player(navigation: Navigation) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    // model
    var appState: AppState by remember { mutableStateOf(EmptyAppState()) }
    fun updateAppState() = coroutineScope.launch(Dispatchers.IO) {
        appState = readAppState()
    }

    // elements
    var elementsVisible: Boolean by remember { mutableStateOf(true) }
    fun playerClicked() {
        elementsVisible = false
        elementsVisible = true
    }
    if (elementsVisible) {
        LaunchedEffect(epochMillis()) {
            delay(3000)
            elementsVisible = false
            println("hide!!!")
        }
    }

    // sheet
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
            sheetState = sheetState, sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp), sheetContent = {
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
            }) {
            Box(
                modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { playerClicked() }.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                VideoPlayer(modifier = Modifier.fillMaxSize(), appState.author.fileName)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                        if (elementsVisible) {
                            CircleImage(appState.avatarBitmap, 128)
                            Row(modifier = Modifier.padding(16.dp)) {
                                Image("eye.svg", 29, 28)
                                Image("comments.svg", 29, 28)
                                Image("arrows.svg", 29, 28)
                                Image("bookmark.svg", 29, 28)
                            }
                        }
                        Button(onClick = { updateSheetStep(PAYMENT) }) { Text("Заказать обзор у блогера") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleImage(bitmap: ImageBitmap, diameter: Int) {
    Image(bitmap, contentDescription = null, modifier = Modifier.size(diameter.dp).clip(RoundedCornerShape((diameter / 2).dp)))
}

@Composable
private fun Image(drawable: String, width: Int, height: Int) {
    Image(drawable.bitmap(), contentDescription = null, modifier = Modifier.width(width.dp).height(height.dp))
}
