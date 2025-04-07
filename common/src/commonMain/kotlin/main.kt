package site.neotrend

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import site.neotrend.SheetStep.INITIAL
import site.neotrend.SheetStep.MESSAGE
import site.neotrend.SheetStep.PAYMENT
import site.neotrend.SheetStep.SUCCESS
import site.neotrend.platform.Video
import site.neotrend.platform.VideoPlayer
import site.neotrend.platform.bitmap
import site.neotrend.platform.epochMillis

private enum class SheetStep {
    INITIAL,
    PAYMENT,
    MESSAGE,
    SUCCESS,
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
        ModalBottomSheetLayout(sheetState = sheetState, sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp), sheetContent = {
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val video: Video = remember {
                    Video(appState.author.fileName) {
                        playerClicked()
                    }
                }
                VideoPlayer(modifier = Modifier.fillMaxSize(), video)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                        if (elementsVisible) {
                            CircleImage(appState.avatarBitmap, 128)
                            Text("${if (video.duration.value != 0L) video.duration.value else ""}", color = Color.White)
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
