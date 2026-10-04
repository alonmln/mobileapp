package coredevices.ring.backup

import kotlinx.io.Source
import kotlinx.io.files.Path

/**
 * Writes zip entries straight to disk; a streamed entry only ever holds one chunk in memory.
 */
expect class BackupZipWriter(outputPath: Path) {
    suspend fun open()
    suspend fun addEntry(name: String, data: ByteArray)
    suspend fun addEntry(name: String, source: Source)
    suspend fun close()
}
