package com.md.Trasic.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.md.Trasic.DetailsActivity
import com.md.Trasic.R
import com.md.Trasic.adapter.ListTrasicAdapter
import com.md.Trasic.data.TrasicResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    const val MAX_VIEW_CACHE = 50

    fun makeTempFile(context: Context): File {
        val timeStamp =
            SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(System.currentTimeMillis())
        return File.createTempFile(
            timeStamp, ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    fun clearTempFiles(context: Context) {
        try {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun convertUriToFile(selectedImg: Uri, ctx: Context): File {
        val resolver = ctx.contentResolver
        val file = makeTempFile(ctx)

        val inputStream = resolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int

        while (inputStream.read(buffer).also { length = it } > 0)
            outputStream.write(buffer, 0, length)

        outputStream.close()
        inputStream.close()

        return file
    }

    fun stabilizeRotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(0f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            matrix.postRotate(0f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    fun convertBitmapToFile(bitmap: Bitmap, file: File): File {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        return file
    }

    fun getCircularProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 8f
            centerRadius = 24f
            start()
        }
    }

    fun setupGridListView(
        context: Context,
        gridRv: RecyclerView,
        itemList: List<TrasicResponse>,
        errorView: View,
        mainView: View,
        launcher: ActivityResultLauncher<Intent>? = null,
    ) {
        if (itemList.isEmpty()) {
            errorView.isVisible = true
            mainView.isVisible = false
        } else {
            errorView.isVisible = false
            mainView.isVisible = true

            gridRv.adapter = ListTrasicAdapter(itemList).apply {
                setOnItemClickCallback(object : ListTrasicAdapter.OnItemClickCallback {
                    override fun onItemClicked(item: TrasicResponse, position: Int) {
                        if (launcher != null) {
                            launcher.launch(Intent(context, DetailsActivity::class.java).apply {
                                putExtra(DetailsActivity.TRASIC_ID_EXTRA, item.id)
                            })
                        } else {
                            context.startActivity(
                                Intent(
                                    context,
                                    DetailsActivity::class.java
                                ).apply {
                                    putExtra(DetailsActivity.TRASIC_ID_EXTRA, item.id)
                                })
                        }
                    }
                })
            }
        }
    }

    class CompressImageTask(private var file: File, private val listener: ICompressListener) :
        Runnable {
        private val handler = Handler(Looper.getMainLooper())

        override fun run() {
            val bitmap = BitmapFactory.decodeFile(file.path)
            var bitmapStream: ByteArrayOutputStream
            var compressQuality = 100
            var streamLength: Int

            do {
                bitmapStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bitmapStream)
                streamLength = bitmapStream.toByteArray().size
                compressQuality -= 5
            } while (streamLength > 1000000)

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

            handler.post { listener.onComplete(file) }
        }

        interface ICompressListener {
            fun onComplete(compressedFile: File)
        }
    }

    fun setHtmlText(textView: TextView, str: String) {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            textView.text = Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
        else
            textView.text = Html.fromHtml(str)
    }

    fun toastNetworkError(ctx: Context) {
        Toast.makeText(
            ctx,
            ctx.resources.getString(R.string.network_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}