package site.neotrend

import kotlin.math.max
import kotlin.math.min

fun Long.toTimeText(): String {
    val time: Long = this
    val seconds: Long = time % 60
    val minutes: Long = time / 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

fun Int.toCountText(): String {
    val count: Int = this
    if (count < 1000) {
        return count.toString()
    } else if (count < 10000) {
        val thousands: Float = count / 1000.0f
        val thousandsText: String = thousands.toText()
        return "$thousandsText тыс."
    } else {
        val thousands: Int = count / 1000
        return "$thousands тыс."
    }
}

fun Float.toText(): String {
    return toString().replace(".", ",").substring(0, 3)
}

fun clamp(value: Int, min: Number = 0f, max: Number = 1f): Int {
    return max(min.toInt(), min(max.toInt(), value))
}