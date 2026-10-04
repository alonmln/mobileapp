package coredevices.ring.backup

import com.oldguy.common.io.File
import com.oldguy.common.io.FileMode
import com.oldguy.common.io.ZipFile
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.files.Path
import kotlinx.io.readByteArray

actual class BackupZipReader actual constructor(inputPath: Path) {
    private val zip = ZipFile(File(inputPath.toString()), FileMode.Read)

    actual suspend fun open() = zip.open()

    actual val entryNames: Set<String>
        get() = zip.entries.mapTo(LinkedHashSet()) { it.name }

    actual suspend fun readEntry(name: String, sink: Sink) {
        zip.readEntry(name) { _, content, count, _ ->
            sink.write(content, 0, count.toInt())
        }
    }

    actual suspend fun readBytes(name: String): ByteArray {
        val buffer = Buffer()
        readEntry(name, buffer)
        return buffer.readByteArray()
    }

    actual suspend fun close() = zip.close()
}
