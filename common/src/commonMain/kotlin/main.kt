package site.neotrend

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import site.neotrend.SheetStep.*
import site.neotrend.platform.*

enum class SheetStep(val button: String, val title: String) {
    INITIAL("", ""),
    PAYMENT("Оплатить", "Рейтинг блогера"),
    MESSAGE("Отправить сообщение", "Отправьте сообщение"),
    SUCCESS("Понятно", "Отлично!"),
}

fun SheetStep.next(): SheetStep = when (this) {
    INITIAL -> PAYMENT
    PAYMENT -> MESSAGE
    MESSAGE -> SUCCESS
    SUCCESS -> INITIAL
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

    @Composable
    fun SheetView(step: SheetStep, onClick: () -> Unit, content: @Composable () -> Unit) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(step.title, style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp), modifier = Modifier.align(alignment = Alignment.Center))
                IconButton(onClick = { updateSheetStep(INITIAL) }, modifier = Modifier.align(alignment = Alignment.CenterEnd).size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.DarkGray)
                }
            }
            content()
            Button(
                modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(4.dp)).padding(bottom = 4.dp).keyboardBottomPadding(),
                onClick = {
                    onClick()
                    updateSheetStep(step.next())
                }) {
                Text(step.button)
            }
        }
    }

    if (appState.isEmpty) {
        updateAppState()
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    } else {
        var message: String by remember { mutableStateOf(WELCOME) }
        ModalBottomSheetLayout(sheetState = sheetState, sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp), sheetContent = {
            SheetView(sheetStep, onClick = {
                if (sheetStep == MESSAGE) {
                    println(message)
                }
            }) {
                val author: Author = appState.author
                when (sheetStep) {
                    INITIAL -> {
                        message = WELCOME
                    }
                    PAYMENT -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().background(color = Color(0xffF3F2F8), shape = RoundedCornerShape(12.dp))
                                .padding(end = 8.dp, start = 8.dp, top = 0.dp, bottom = 0.dp), verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                Text("Блогер", style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp))
                                Text(
                                    "@${author.authorDto.name}",
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd),
                                    style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp)
                                )
                            }
                            Divider(startIndent = 8.dp, thickness = 1.dp, color = Color(0xffDEDEDE))
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                Text("Рейтинг", style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp))
                                Row(modifier = Modifier.align(alignment = Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        author.rating.toText(),
                                        style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    val activeStarCount: Int = clamp(author.rating.toInt(), 0, 5)
                                    val inactiveStarCount: Int = 5 - activeStarCount
                                    repeat(activeStarCount) {
                                        Icon("gray_star.svg".bitmap(), contentDescription = null, tint = activeStarColor)
                                    }
                                    repeat(inactiveStarCount) {
                                        Icon("gray_star.svg".bitmap(), contentDescription = null, tint = inactiveStarColor)
                                    }
                                }
                            }
                            Divider(startIndent = 8.dp, thickness = 1.dp, color = Color(0xffDEDEDE))
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                Text("Стоимость обзора", style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp))
                                Text(
                                    "120 BYN",
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd),
                                    style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp)
                                )
                            }
                        }
                    }
                    MESSAGE -> {
                        AnnotatedText("На вашем балансе заморожено 120 рублей, до момента одобрения вами обзора, присланного блогером **@${author.authorDto.name}**. Блогеру отправлен запрос.")
                        Text(
                            "ВАШЕ СООБЩЕНИЕ",
                            style = TextStyle(color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        AnnotatedTextField(
                            message, onValueChange = { message = it },
                            modifier = Modifier.fillMaxWidth().onFocusChanged {
                                if (it.isFocused) {
                                    message = WELCOME_FOCUSED
                                }
                            },
                        )
                    }
                    SUCCESS -> {
                        AnnotatedText("Ваше сообщение **@${author.authorDto.name}** отправлено.")
                    }
                }
            }
        }) {
            Box(modifier = Modifier.fillMaxSize().background(color = Color.Black), contentAlignment = Alignment.Center) {
                val video: Video = remember {
                    Video(appState.author.fileName) {
                        playerClicked()
                    }
                }
                VideoPlayer(video, modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                        if (elementsVisible) {
                            val statistics: AuthorStatistics = appState.author.statistics
                            Row(modifier = Modifier) {
                                CircleImage(appState.avatarBitmap, 64)
                                Spacer(Modifier.fillMaxWidth().weight(1f))
                            }
                            Row(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                                Text(appState.author.fileName, color = Color.White)
                                Text(
                                    if (video.duration.value != 0L) "(${video.duration.value.toTimeText()})" else "",
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Spacer(Modifier.fillMaxWidth().weight(1f))
                                Text(appState.author.createdDate, color = Color.White)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("eye.svg", 29, 28)
                                    Text(statistics.viewsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("comments.svg", 29, 28)
                                    Text(statistics.commentsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("arrows.svg", 29, 28)
                                    Text(statistics.repostsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("bookmark.svg", 29, 28)
                                    Text(statistics.savesCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp))
                                }
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

private fun Long.toTimeText(): String {
    val time: Long = this
    val seconds: Long = time % 60
    val minutes: Long = time / 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

private fun Int.toCountText(): String {
    val count: Int = this
    if (count < 1000) {
        return count.toString()
    } else if (count < 10000) {
        val thousands: Float = count / 1000.0f
        val thousandsText: String = thousands.toText().substring(0, 3)
        return "$thousandsText тыс."
    } else {
        val thousands: Int = count / 1000
        return "$thousands тыс."
    }
}

private fun Float.toText(): String {
    return toString().replace(".", ",")
}