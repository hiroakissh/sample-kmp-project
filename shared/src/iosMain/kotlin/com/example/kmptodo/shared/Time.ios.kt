package com.example.kmptodo.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long = memScoped {
    val currentTime = alloc<timeval>()
    gettimeofday(currentTime.ptr, null)
    currentTime.tv_sec * 1_000L + currentTime.tv_usec / 1_000L
}
