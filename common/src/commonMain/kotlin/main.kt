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
    navigation.screen("welcome") { welcome(navigation) }
    navigation.screen("player") { player(navigation) }
    navigation.host("welcome")
//    println("App")
//    val author: Author = readAuthor()
//    val avatar: ByteArray = readAvatar(author.authorDto.id)
//    println(author)
//    println("avatar: $avatar ${avatar.size}")
//
//    var text by remember { mutableStateOf("Hello, World!") }
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
//            VideoPlayer(modifier = Modifier.fillMaxSize(), author.fileName)
//            Button(onClick = {
//                text = "Hello!"
//                navigation.navigate("example1")
//            }) { Text(text) }
//            Card(modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//                    .clickable { println("CardView Clicked....") }, elevation = 6.dp, shape = RoundedCornerShape(8.dp), backgroundColor = Color(0xFF00BFA5),) {
//                Row(modifier = Modifier.padding(16.dp)) {
//                    Image(
//                        bitmap = avatar.bitmap(),
//                        contentDescription = "Card Image",
//                        modifier = Modifier.width(29.dp).height(28.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .border(2.dp, Color.White, RoundedCornerShape(8.dp)),)
//                    Image(
//                        bitmap = "drawables/arrows.svg".bitmap(),
//                        contentDescription = "Card Image",
//                        modifier = Modifier.width(29.dp).height(28.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
//                    )
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Column {
//                        Text("Title Text", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
//                        Text("Sample description text.", color = Color.White)
//                    }
//                }
//            }
//        }
//    }
}


@Composable
private fun welcome(navigation: Navigation) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { navigation.navigate("player") }) { Text("Открыть видео блогера") }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun player(navigation: Navigation) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var appState: AppState by remember { mutableStateOf(EmptyAppState()) }
    var sheetStep: SheetStep by remember { mutableStateOf(PAYMENT) }
    if (appState.isEmpty) {
        coroutineScope.launch(Dispatchers.IO) {
            appState = readAppState()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    } else {
        val sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        fun updateState(newState: SheetStep) = coroutineScope.launch {
            sheetState.hide()
            sheetStep = newState
            if (sheetStep != INACTIVE) {
                sheetState.show()
            }
        }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetContent = {
                IconButton(onClick = { updateState(INACTIVE) }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
                when (sheetStep) {
                    INACTIVE -> {
                        // hidden
                    }
                    PAYMENT -> {
                        Text("PAY")
                        Button(onClick = { updateState(MESSAGE) }) { Text("MESSAGE") }
                    }
                    MESSAGE -> {
                        Text("MESSAGE")
                        Button(onClick = { updateState(SUCCESS) }) { Text("SUCCESS") }
                    }
                    SUCCESS -> {
                        Text("SUCCESS")
                        Button(onClick = { updateState(INACTIVE) }) { Text("START") }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                VideoPlayer(modifier = Modifier.fillMaxSize(), appState.author.fileName)
                Button(onClick = { coroutineScope.launch { sheetStep = PAYMENT; sheetState.show() } }) { Text("Заказать обзор у блогера") }
            }
        }
    }
}

private enum class SheetStep {
    INACTIVE,
    PAYMENT,
    MESSAGE,
    SUCCESS,
}
