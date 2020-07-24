package de.ka.jamit.tcalc.roboelectric.base

import java.io.*

/**
 * Handy method for assigning an output stream.
 */
fun outputStream(output: (ByteArray) -> Unit): OutputStream {
    return ApplicationOutputStream(output)
}

/**
 * Abstraction for output stream usage.
 */
class ApplicationOutputStream(private val output: (ByteArray) -> Unit) : OutputStream() {

    private val outputBytes = mutableListOf<Byte>()

    override fun write(b: Int) {
        outputBytes.add(b.toByte())
    }

    override fun close() {
        output(outputBytes.toByteArray())
        super.close()
    }
}