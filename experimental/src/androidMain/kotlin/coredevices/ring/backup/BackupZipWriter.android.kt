package coredevices.ring.backup

import kotlinx.io.Source
import kotlinx.io.files.Path
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual class BackupZipWriter actual constructor(private val outputPath: Path) {
    private lateinit var zip: ZipOutputStream

    actual suspend fun open() {
        zip = ZipOutputStream(FileOutputStream(outputPath.toString()))
    }

    actual suspend fun addEntry(name: String, data: ByteArray) {
        zip.putNextEntry(ZipEntry(name))
        try {
            zip.write(data)
        } finally {
            zip.closeEntry()
        }
    }

    actual suspend fun addEntry(name: String, source: Source) {
        zip.putNextEntry(ZipEntry(name))
        try {
            val buffer = ByteArray(CHUNK_SIZE)
            while (true) {
                val count = source.readAtMostTo(buffer)
                if (count <= 0) break
                zip.write(buffer, 0, count)
            }
        } finally {
            zip.closeEntry()
        }
    }

    actual suspend fun close() {
        zip.close()
    }

    private companion object {
        const val CHUNK_SIZE = 64 * 1024
    }
}
