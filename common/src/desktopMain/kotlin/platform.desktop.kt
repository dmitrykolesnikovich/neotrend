package site.neotrend.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import java.io.File

actual fun ByteArray.bitmap(): ImageBitmap {
    val bytes: ByteArray = this
    val image: Image = Image.makeFromEncoded(bytes)
    val bitmap: ImageBitmap = Bitmap.makeFromImage(image).asComposeImageBitmap()
    return bitmap
}

@Composable
actual fun String.bitmap(): ImageBitmap {
    val resourcePath: String = this
    val painter: Painter = painterResource(resourcePath)
    val size: Size = painter.intrinsicSize
    val bitmap: ImageBitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    CanvasDrawScope().draw(Density(1.0f, 1.0f), LayoutDirection.Ltr, Canvas(bitmap), size) {
        with(painter) {
            draw(size)
        }
    }
    return bitmap
}

@Composable
actual fun VideoPlayer(video: Video, modifier: Modifier) {
    Box(
        modifier = Modifier
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { video.onClick() }
        .fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(bitmap = "video-player.png".bitmap(), contentDescription = "video player placeholder", modifier)
    }
    remember {
        val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            delay(30)
            video.duration.value = 123
        }
    }
}

actual fun cacheBytes(fileName: String, readBytes: () -> ByteArray) {
    val file: File = File("${System.getProperty("user.dir")}/.neotrend/videos/$fileName")
    if (!file.exists()) {
        file.parentFile.mkdirs()
        file.createNewFile()
        val bytes: ByteArray = readBytes()
        file.writeBytes(bytes)
    }
}

// for jvm
actual fun epochMillis(): Long = System.currentTimeMillis()