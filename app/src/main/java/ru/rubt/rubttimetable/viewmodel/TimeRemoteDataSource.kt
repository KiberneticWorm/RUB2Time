package ru.rubt.rubttimetable.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import ru.rubt.rubttimetable.R
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.util.*

class TimeRemoteDataSource(
        private val ctx: Context,
        private val client: OkHttpClient,
        private val appSettings: SharedPreferences
) {

    private val requestBuilder = Request.Builder()

    object Constants {
        const val XLS_PATH = "http://rmt.e4u.ru/docs/raspisanie/timetable1.xls"
        const val SHA_HASH = "sha_hash"
        const val PREFIX_LINK = "http://rmt.e4u.ru"
    }

    fun updatedPdfFiles() {
        // get a WEB page with schedule of lessons
        val document = Jsoup.connect(ctx.getString(R.string.rubt_url)).get()
        val links = document.select(".contentpaneopen:has(td:contains(Замены в расписании)) + .contentpaneopen td > p > a")
        // divides PDF files into three groups - need to delete, need to stay, need to add
        val pdfFiles = HashMap<String, State>()
        val pdfHeaders = HashSet<String>(appSettings.getStringSet(ctx.getString(R.string.pdf_headers), HashSet()))
        val pdfHeadersSize = pdfHeaders.size

        if (pdfHeadersSize != 0) { // We have pdf files

            // to avoid ConcurrentModificationException
            val arrayOfHeaders = pdfHeaders.toTypedArray()
            for (header in arrayOfHeaders) { // We are looking saved headers of PDF files
                for (link in links) {
                    if (TextUtils.equals(link.text().trim(), header)) { // If saved headers equal headers from site than it will not remove
                        pdfFiles[header] = State.BEING
                        break
                    }
                }
                if (pdfFiles[header] != State.BEING) {
                    pdfFiles[header] = State.NEED_TO_DELETE // Else saved headers will remove!
                    pdfHeaders.remove(header)
                }
            }
        }
        val pdfHeadersToUrls = HashMap<String, String>()
        var header: String
        var url: String
        // adding new files
        for (link in links) {
            header = link.text().trim()
            if (!pdfHeaders.contains(header)) {
                url = Constants.PREFIX_LINK + link.attr("href")
                pdfHeadersToUrls[header] = url // saving header with URL for access in future
                pdfHeaders.add(header)
                pdfFiles[header] = State.NEED_TO_DOWNLOAD
            }
        }

        with (appSettings.edit()) {
            putStringSet(ctx.getString(R.string.pdf_headers), pdfHeaders)
            commit()
        }

        for (key in pdfFiles.keys) {
            if (pdfFiles[key] == State.NEED_TO_DELETE) {
                deletePdfFile(key) // delete PDF files
            }
            if (pdfFiles[key] == State.NEED_TO_DOWNLOAD) {
                loadPdfFile(key, pdfHeadersToUrls[key], requestBuilder)
            }
        }
    }

    fun getUpdatedXlsStream() : ByteArray? {

        val shaHash = appSettings.getString(Constants.SHA_HASH,
                DigestUtils.sha256Hex(ctx.assets.open(ctx.getString(R.string.local_xls_file)).readBytes()))
        val responseBody = client.newCall(requestBuilder.url(Constants.XLS_PATH).build()).execute().body()!!
        val bytes = responseBody.bytes()

        val shaXLSHash = DigestUtils.sha256Hex(bytes)

        // compare old has with new hash
        if (!TextUtils.equals(shaHash, shaXLSHash)) { // compare old 256-bit hashcode with a new hashcode
            with (appSettings.edit()) {
                putString(Constants.SHA_HASH, shaXLSHash)
                commit()
            }

            return bytes
        }
        return null
    }

    // loading PDF file named "Schedule lessons on Friday 05 of September"
    private fun loadPdfFile(header: String, url: String?, requestBuilder: Request.Builder) {
        val request = requestBuilder.url(url).build()
        ctx.openFileOutput(translateHeaderToFilename(header),
                Context.MODE_PRIVATE).use { out -> out.write(client.newCall(request).execute().body()!!.bytes()) }
    }

    // deleting by header from the previous procedure
    private fun deletePdfFile(header: String) {
        File(ctx.filesDir, translateHeaderToFilename(header)).delete()
    }
    // return filename in format "Расписаниезанятийнасубботу05сентября2020.pdf" to save file at the device
    private fun translateHeaderToFilename(header: String): String {
        return header.replace("\\s".toRegex(), "") + ".pdf"
    }
}