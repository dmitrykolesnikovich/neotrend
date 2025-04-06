package site.neotrend.common

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

@Composable
actual fun VideoPlayer(modifier: Modifier, fileName: String) {
    val bitmap: ImageBitmap = "video-player.png".bitmap()
    Image(bitmap = bitmap, contentDescription = "video player placeholder", modifier = modifier)
}

@Composable
actual fun drawableToImageBitmap(drawable: String): ImageBitmap {
    val painter: Painter = painterResource("drawables/$drawable")
    return painter.toImageBitmap(painter.intrinsicSize, Density(1.0f, 1.0f), LayoutDirection.Ltr)
}

actual fun ByteArray.toComposeImageBitmap(): ImageBitmap {
    try {
        val bytes: ByteArray = this
        val image: Image = Image.makeFromEncoded(bytes)
        val bitmap: ImageBitmap = Bitmap.makeFromImage(image).asComposeImageBitmap()
        return bitmap
    } catch (e: Exception) {
        println("breakpoint")
        throw e
    }
}

@Composable
fun String.bitmap(): ImageBitmap {
    val painter: Painter = painterResource(this)
    return painter.toImageBitmap(painter.intrinsicSize, Density(1.0f, 1.0f), LayoutDirection.Ltr)
}

fun Painter.toImageBitmap(size: Size, density: Density, layoutDirection: LayoutDirection): ImageBitmap {
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = Canvas(bitmap)
    CanvasDrawScope().draw(density, layoutDirection, canvas, size) {
        draw(size)
    }
    return bitmap
}
