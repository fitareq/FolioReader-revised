package org.group.dird.sheiboitest

import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.FolioReader.OnClosedListener
import com.folioreader.model.HighLight
import com.folioreader.model.locators.ReadLocator
import com.folioreader.util.AppUtil.Companion.getSavedConfig
import com.folioreader.util.OnHighlightListener
import com.folioreader.util.ReadLocatorListener
import kotlinx.coroutines.DelicateCoroutinesApi


class MainActivity : AppCompatActivity(), OnHighlightListener, ReadLocatorListener, OnClosedListener{
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var config = getSavedConfig(applicationContext)
        if (config == null) config = Config()
        config.allowedDirection = Config.AllowedDirection.VERTICAL_AND_HORIZONTAL
        config.setThemeColorInt(ContextCompat.getColor(this, R.color.black))

        FolioReader.get()
            .setOnHighlightListener(this)
            .setReadLocatorListener(this)
            .setOnClosedListener(this)
            .setConfig(config, true)
            .setScreenShotEnabled(false)
            .setCopyEnabled(false)
            .openBook("file:///android_asset/adventures.epub")

        /*imageView = findViewById(R.id.pdf_view)

        val file =
            File("/storage/emulated/0/Download/Jasmine_Akter-01616813273-jasminetripti5gmail.com.pdf")


        //get data from intent
        intent.data?.let {

            val fileDescriptor = contentResolver.openFileDescriptor(it, "r") ?: return@let

            val pdfRender = PdfRenderer(fileDescriptor)
            val currentPage = pdfRender.openPage(0)
            val bitmap =
                Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            imageView.setImageBitmap(bitmap)
        }

        GlobalScope.launch(Dispatchers.Main) {
            loadAllFilesToDatabase()
        }*/
    }


    private fun getAllMediaFilesCursor(): Cursor? {
        val projections =
            arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE
            )

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        return this.contentResolver.query(
            collection,
            projections,
            null,
            null,
            null
        )
    }

    private fun loadAllFilesToDatabase() {
        val cursor = getAllMediaFilesCursor()

        if (true == cursor?.moveToFirst()) {
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val pathCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            val nameCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val mimeType = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
            val sizeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)

            do {
                val id = cursor.getLong(idCol)
                val path = cursor.getStringOrNull(pathCol) ?: continue
                val name = cursor.getStringOrNull(nameCol) ?: continue
                val dateTime = cursor.getLongOrNull(dateCol) ?: continue
                val type = cursor.getStringOrNull(mimeType) ?: continue
                val size = cursor.getLongOrNull(sizeCol) ?: continue
                val contentUri = ContentUris.appendId(
                    MediaStore.Files.getContentUri("external").buildUpon(),
                    id
                ).build()

                val media =
                    "Uri:$contentUri,\nPath:$path,\nFileName:$name,\nFileSize:$size,\nDate:$dateTime,\ntype:$type"

                Log.v("Media", media)

            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

    override fun onHighlight(highlight: HighLight?, type: HighLight.HighLightAction?) {
        Toast.makeText(this, "onHighlight method from main", Toast.LENGTH_SHORT).show()
    }

    override fun onFolioReaderClosed() {
        Toast.makeText(this, "onFolioReaderClosed method from main", Toast.LENGTH_SHORT).show()
    }

    override fun saveReadLocator(readLocator: ReadLocator?) {
        Toast.makeText(this, "saveReadLocator method from main", Toast.LENGTH_SHORT).show()
    }


}