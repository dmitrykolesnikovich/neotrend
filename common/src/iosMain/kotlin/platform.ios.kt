package site.neotrend.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.duration
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMTime
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIEvent
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIView
import platform.posix.memcpy

actual fun cacheBytes(fileName: String, readBytes: () -> ByteArray) {
    val url: NSURL = fileName.toURL()
    if (!url.exists()) {
        val bytes: ByteArray = readBytes()
        val data: NSData = bytes.toNSData()
        data.writeToURL(url, atomically = true)
    }
}

@Composable
actual fun VideoPlayer(modifier: Modifier, video: Video) {
    @ExportObjCClass
    class PlayerController : AVPlayerViewController {

        lateinit var onClick: () -> Unit

        @OverrideInit
        constructor() : super(null, null)

        override fun touchesBegan(touches: Set<*>, withEvent: UIEvent?) {
            onClick()
            val player: AVPlayer = checkNotNull(player)
            if (player.rate == 0f) {
                player.rate = 1f
            } else {
                player.rate = 0f
            }

            val time: CMTime = checkNotNull(player.currentItem).duration.useContents { this }
            println("time.value: ${time.value}")
            println("time.timescale: ${time.timescale}")
            video.duration = if (time.timescale != 0) time.value / time.timescale else time.value
            println("video.duration: ${video.duration}")
        }

    }

    val url: NSURL = video.fileName.toURL()
    check(url.exists())
    val player: AVPlayer = remember { AVPlayer(url) }
    val layer: AVPlayerLayer = remember { AVPlayerLayer() }
    val controller: PlayerController = remember { PlayerController() }
    controller.player = player
    controller.showsPlaybackControls = false
    controller.onClick = video.onClick
    layer.player = player
    UIKitView(
        factory = {
            UIView().apply { addSubview(controller.view) }
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

actual fun epochMillis(): Long = memScoped {
    return NSDate.date().timeIntervalSince1970.toLong() * 1000
}
