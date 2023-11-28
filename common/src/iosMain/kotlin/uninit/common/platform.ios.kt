package uninit.common

actual fun getSystemMillis(): Long = platform.Foundation.NSDate().timeIntervalSince1970.toLong() * 1000L