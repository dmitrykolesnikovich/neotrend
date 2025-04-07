package site.neotrend.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import org.jetbrains.skia.Image
import platform.AVFoundation.*
import platform.AVKit.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.QuartzCore.*
import platform.UIKit.*
import platform.posix.*

actual fun cacheBytes(fileName: String, readBytes: () -> ByteArray) {
    val url: NSURL = fileName.toURL()
    if (!url.exists()) {
        val bytes: ByteArray = readBytes()
        val data: NSData = bytes.toNSData()
        data.writeToURL(url, atomically = true)
    }
}

@Composable
actual fun VideoPlayer(modifier: Modifier, fileName: String) {
    val url: NSURL = fileName.toURL()
    check(url.exists())
    val player: AVPlayer = remember { AVPlayer(url) }
    val layer: AVPlayerLayer = remember { AVPlayerLayer() }
    val controller: MyViewController = remember { MyViewController() }
    controller.player = player
    controller.showsPlaybackControls = false
    layer.player = player
    UIKitView(
        factory = {
            UIView().apply {
                addSubview(controller.view)
            }
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            layer.setFrame(rect)
            controller.view.layer.frame = rect
            CATransaction.commit()
        },
        update = {
            player.play()
        },
        modifier = modifier
    )
}

@Composable
actual fun String.bitmap(): ImageBitmap {
    val drawable: String = this
    val image: UIImage = UIImage.imageNamed(drawable, NSBundle.mainBundle, withConfiguration = null) ?: error("drawable: $drawable")
    val representation: NSData = checkNotNull(UIImagePNGRepresentation(image))
    val bytes: ByteArray = ByteArray(representation.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), representation.bytes, representation.length)
        }
    }
    return bytes.bitmap()
}

actual fun ByteArray.bitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

/*internals*/

private fun String.toURL(): NSURL {
    val documentsDirectory: NSURL = (NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as List<NSURL>).first()
    val url: NSURL = NSURL.fileURLWithPath(this, relativeToURL = documentsDirectory)
    return url
}

private fun NSURL.exists(): Boolean {
    return checkResourceIsReachableAndReturnError(null)
}

private fun ByteArray.toNSData(): NSData {
    val data: ByteArray = this
    memScoped {
        return NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
    }
}

@ExportObjCClass
private class MyViewController : AVPlayerViewController {

    @OverrideInit
    constructor() : super(null, null)

    override fun touchesBegan(touches: Set<*>, withEvent: UIEvent?) {
        val player: AVPlayer = checkNotNull(player)
        if (player.rate == 0f) {
            player.rate = 1f
        } else {
            player.rate = 0f
        }
    }

}
