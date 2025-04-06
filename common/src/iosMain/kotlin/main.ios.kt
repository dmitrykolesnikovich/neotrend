package site.neotrend.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIEvent
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.posix.memcpy
import site.neotrend.common.examples.example2

@Suppress("unused")
fun MainViewController(): UIViewController = ComposeUIViewController {
//    App()
//    example1()
    example2()
}

@Composable
actual fun drawableToImageBitmap(drawable: String): ImageBitmap {
    val image: UIImage = UIImage.imageNamed(drawable, NSBundle.mainBundle, withConfiguration = null) ?: error("drawable: $drawable")
    val representation: NSData = checkNotNull(UIImagePNGRepresentation(image))
    val bytes: ByteArray = ByteArray(representation.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), representation.bytes, representation.length)
        }
    }
    return bytes.toComposeImageBitmap()
}

actual fun ByteArray.toComposeImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

@ExportObjCClass
class MyViewController : AVPlayerViewController {

    @OverrideInit
    constructor(): super(null, null)

    override fun touchesBegan(touches: Set<*>, withEvent: UIEvent?) {
        val player: AVPlayer = checkNotNull(player)
        if (player.rate == 0f) {
            player.rate = 1f
        } else {
            player.rate = 0f
        }
    }

}

@Composable
actual fun VideoPlayer(modifier: Modifier, fileName: String)  {
    val documentsDirectory: NSURL = (NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as List<NSURL>).first()
    val url: NSURL = NSURL.fileURLWithPath(fileName, relativeToURL = documentsDirectory)
    if (!url.exists()) {
        val bytes: ByteArray  = readVideo(fileName)
        val data: NSData = bytes.toNSData()
        data.writeToURL(url, atomically = true)
    }
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
        modifier = modifier)
}

private fun ByteArray.toNSData(): NSData {
    val data: ByteArray = this
    memScoped {
        return NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
    }
}

private fun NSURL.exists(): Boolean {
    return checkResourceIsReachableAndReturnError(null)
}
