package site.neotrend.platform

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
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
actual fun VideoPlayer(modifier: Modifier, fileName: String) {
    Image(bitmap = "video-player.png".bitmap(), contentDescription = "video player placeholder", modifier)
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
