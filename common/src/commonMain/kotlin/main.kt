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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import site.neotrend.SheetStep.*
import site.neotrend.platform.*

enum class SheetStep(val button: String, val title: String) {
    INITIAL("", ""),
    PAYMENT(R.strings.pay, R.strings.bloggerRating),
    MESSAGE(R.strings.sendMessage, R.strings.sendYourMessage),
    SUCCESS(R.strings.gotIt, R.strings.great),
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
        Button(onClick = { navigation.navigate("player") }) { Text(R.strings.openVideo) }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(end = 16.dp, start = 16.dp, top = 12.dp, bottom = 32.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Text(step.title, style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp), modifier = Modifier.align(alignment = Alignment.Center))
                IconButton(onClick = { updateSheetStep(INITIAL) }, modifier = Modifier.align(alignment = Alignment.CenterEnd).size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.DarkGray)
                }
            }
            content()
            Button(
                modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(8.dp)).padding(bottom = 4.dp, top = 16.dp).height(48.dp).keyboardBottomPadding(),
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
        var message: String by remember { mutableStateOf(R.strings.welcome) }
        ModalBottomSheetLayout(sheetState = sheetState, sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp), sheetContent = {
            SheetView(sheetStep, onClick = {
                if (sheetStep == MESSAGE) {
                    println(message)
                }
            }) {
                val author: Author = appState.author
                when (sheetStep) {
                    INITIAL -> {
                        message = R.strings.welcome
                    }
                    PAYMENT -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().background(color = Color(0xffF3F2F8), shape = RoundedCornerShape(12.dp)),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)) {                                
                                Text(
                                    R.strings.blogger,
                                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                )
                                Text(
                                    "@${author.authorDto.name}",
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd),
                                    style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp)
                                )
                            }
                            Divider(startIndent = 8.dp, thickness = 1.dp, color = Color(0xffDEDEDE))
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)) {                                
                                Text(
                                    R.strings.rating,
                                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                )
                                Row(modifier = Modifier.align(alignment = Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        author.rating.toText(),
                                        style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    val activeStarCount: Int = clamp(author.rating.toInt(), 0, 5)
                                    val inactiveStarCount: Int = 5 - activeStarCount
                                    repeat(activeStarCount) {
                                        Icon("gray_star.svg".bitmap(), contentDescription = null, tint = R.colors.activeStar)
                                    }
                                    repeat(inactiveStarCount) {
                                        Icon("gray_star.svg".bitmap(), contentDescription = null, tint = R.colors.inactiveStar)
                                    }
                                }
                            }
                            Divider(startIndent = 8.dp, thickness = 1.dp, color = Color(0xffDEDEDE))
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)) {                                
                                Text(
                                    R.strings.reviewPrice,
                                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                                    style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                )
                                Text(
                                    "120 BYN",
                                    modifier = Modifier.align(alignment = Alignment.CenterEnd),
                                    style = TextStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray, fontSize = 14.sp)
                                )
                            }
                        }
                    }
                    MESSAGE -> {
                        AnnotatedText(R.strings.moneyFrozen(author))
                        Text(
                            R.strings.yourMessage,
                            style = TextStyle(color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        AnnotatedTextField(
                            message, onValueChange = { message = it },
                            modifier = Modifier.fillMaxWidth().onFocusChanged {
                                if (it.isFocused) {
                                    message = R.strings.welcomeFocused
                                }
                            },
                        )
                    }
                    SUCCESS -> {
                        AnnotatedText(R.strings.yourMessageSend(author))
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
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                        if (elementsVisible) {
                            val statistics: AuthorStatistics = appState.author.statistics
                            Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircleImage(appState.avatarBitmap, 48)
                                Text(appState.author.authorDto.name, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                                Spacer(Modifier.fillMaxWidth().weight(1f))
                            }
                            Row(modifier = Modifier.padding(top = 8.dp, bottom = 32.dp, start = 8.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(appState.author.fileName, color = Color.White, style = TextStyle(fontSize = 20.sp))
                                Spacer(Modifier.fillMaxWidth().weight(1f))
                                Text(
                                    "${appState.author.createdDate} ${if (video.duration.value != 0L) " (${video.duration.value.toTimeText()})" else ""}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceAround) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("eye.svg", 29, 28, Modifier.scale(1.25f))
                                    Text(statistics.viewsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 20.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("comments.svg", 29, 28, Modifier.scale(1.25f))
                                    Text(statistics.commentsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 20.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("arrows.svg", 29, 28, Modifier.scale(1.25f))
                                    Text(statistics.repostsCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 20.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image("bookmark.svg", 29, 28, Modifier.scale(1.25f))
                                    Text(statistics.savesCount.toCountText(), color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 20.sp)
                                }
                            }
                        }
                        Button(
                            onClick = { updateSheetStep(PAYMENT) },
                            modifier = Modifier.fillMaxWidth().height(80.dp).padding(bottom = 20.dp).clip(shape = RoundedCornerShape(8.dp))
                        ) {                            
                            Text(R.strings.makeOrder, fontSize = 20.sp)
                        }
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
private fun Image(drawable: String, width: Int, height: Int, modifier: Modifier = Modifier) {
    Image(drawable.bitmap(), contentDescription = null, modifier = modifier.width(width.dp).height(height.dp))
}
