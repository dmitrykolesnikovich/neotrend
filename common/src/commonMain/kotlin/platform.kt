package site.neotrend.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

expect fun cacheBytes(fileName: String, readBytes: () -> ByteArray)

@Composable
expect fun VideoPlayer(video: Video, modifier: Modifier)

@Composable
expect fun String.bitmap(): ImageBitmap

expect fun ByteArray.bitmap(): ImageBitmap

expect fun epochMillis(): Long

class Video(val fileName: String, val onClick: () -> Unit) {
    val duration: MutableState<Long> = mutableStateOf(0)
    val ended: MutableState<Boolean> = mutableStateOf(false)
}