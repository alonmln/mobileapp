package coredevices.ring.backup

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import java.util.zip.ZipFile

actual class BackupZipReader actual constructor(private val inputPath: Path) {
    private lateinit var zip: ZipFile

    actual suspend fun open() {
        zip = ZipFile(inputPath.toString())
    }

    actual val entryNames: Set<String>
        get() = buildSet {
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (!entry.isDirectory) add(entry.name)
            }
        }

    actual suspend fun readEntry(name: String, sink: Sink) {
        val entry = requireNotNull(zip.getEntry(name)) { "Zip entry not found: $name" }
        zip.getInputStream(entry).use { input ->
            val buffer = ByteArray(CHUNK_SIZE)
            while (true) {
                val count = input.read(buffer)
                if (count <= 0) break
                sink.write(buffer, 0, count)
            }
        }
    }

    actual suspend fun readBytes(name: String): ByteArray {
        val buffer = Buffer()
        readEntry(name, buffer)
        return buffer.readByteArray()
    }

    actual suspend fun close() {
        zip.close()
    }

    private companion object {
        const val CHUNK_SIZE = 64 * 1024
    }
}
