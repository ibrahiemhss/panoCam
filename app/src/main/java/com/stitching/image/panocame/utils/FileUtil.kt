package com.stitching.image.panocame.utils

import android.content.Context
import android.net.Uri
import android.os.Environment.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileUtil(private val context: Context) {

    @Throws(IOException::class)
    fun urisToFiles(uris: List<Uri>): List<File> {
        val files = ArrayList<File>(uris.size)
        for (uri in uris) {
            val file = createFile(requireTemporaryDirectory(), FILENAME, PHOTO_EXTENSION)
            writeUriToFile(uri, file)
            files.add(file)
        }
        return files
    }

    fun createResultFile(): File {
        val pictures = context.getExternalFilesDir(DIRECTORY_PICTURES)!!
        return createFile(File(pictures, FILENAME), FILENAME, PHOTO_EXTENSION)
    }

    fun cleanUpWorkingDirectory() {
        requireTemporaryDirectory().remove()
    }


    @Throws(IOException::class)
    private fun writeUriToFile(target: Uri, destination: File) {
        val inputStream = context.contentResolver.openInputStream(target)!!
        val outputStream = FileOutputStream(destination)
        inputStream.use { input ->
            outputStream.use { out ->
                input.copyTo(out)
            }
        }
    }

    private fun requireTemporaryDirectory(): File {
        // don't need read/write permission for this directory starting from android 19
        val pictures = context.getExternalFilesDir(DIRECTORY_PICTURES)!!
        return File(pictures, FILENAME)
    }

    // there is no build in function for deleting folders <3
    private fun File.remove() {
        if (isDirectory) {
            val entries = listFiles()
            if (entries != null) {
                for (entry in entries) {
                    entry.remove()
                }
            }
        }
        delete()
    }

    companion object {

        public const val TAG = "CameraXBasic"
        public const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        public const val PHOTO_EXTENSION = ".jpg"
        public const val RATIO_4_3_VALUE = 4.0 / 3.0
        public const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        public fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.US)
                        .format(System.currentTimeMillis()) + extension)
    }
   /* companion object {
        private const val TEMPORARY_DIRECTORY_NAME = "Temporary"
        private const val RESULT_DIRECTORY_NAME = "Results"
        private const val DATE_FORMAT_TEMPLATE = "yyyyMMdd_HHmmss"
        private const val IMAGE_NAME_TEMPLATE = "IMG_%s_"
        private const val JPG_EXTENSION = ".jpg"
    }*/
}
