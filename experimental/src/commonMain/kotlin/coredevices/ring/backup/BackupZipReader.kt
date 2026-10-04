package coredevices.ring.backup

import kotlinx.io.Sink
import kotlinx.io.files.Path

/**
 * Reads zip entries on demand. Not safe for concurrent reads; serialise callers.
 */
expect class BackupZipReader(inputPath: Path) {
    suspend fun open()
    val entryNames: Set<String>
    suspend fun readEntry(name: String, sink: Sink)
    suspend fun readBytes(name: String): ByteArray
    suspend fun close()
}
