package site.neotrend.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

expect fun cacheBytes(fileName: String, readBytes: () -> ByteArray)

@Composable
expect fun VideoPlayer(modifier: Modifier, fileName: String)

@Composable
expect fun String.bitmap(): ImageBitmap

expect fun ByteArray.bitmap(): ImageBitmap
