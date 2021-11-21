package com.example.androidstorage11permission

import android.Manifest.permission
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

import androidx.core.content.ContextCompat

import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.os.Environment

import android.os.Build
import android.os.Build.VERSION

import android.os.Build.VERSION.SDK_INT
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity

import androidx.core.app.ActivityCompat

import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri

import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.view.MenuItem
import java.lang.Exception
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    var file_name_path = ""
    private val PERMISSION_REQUEST_CODE = 2296
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var permission =
        arrayOf<String>(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkPermission()) {
            directoryCreate()
            createpdf()
        } else {
            requestPermission()
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            // perform action when allow permission success
                            Toast.makeText(
                                this,
                                "Permission Granted",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        } else {
                            Toast.makeText(
                                this,
                                "Permission Denied",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }

            }

    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                //startActivityForResult(intent, 2296)
                activityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                //startActivityForResult(intent, 2296)
                activityResultLauncher.launch(intent)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success


                } else {
                    Toast.makeText(
                        this,
                        "Allow permission for storage access!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                try {
                    val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val WRITE_EXTERNAL_STORAGE =
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Allow permission for storage access!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun createpdf() {

        var bankasia_name = "Bank Asia Limited";
        var statementTitle =
            "Account Statement" + "(" + "etFromDate.text.toString()" + " to " + "etToDate.text.toString()" + ")"

        val bounds = Rect()
        val pageWidth = 700
        val pageheight = 470
        val pathHeight = 2
        val fileName = "DateUtil.getVisaCurDateTime()"
        // file_name_path = "/visacard_receit/$fileName.pdf"
        file_name_path =
            "/" + "TextContants.smartapp_document_folder" + "/" + "account statement" + fileName + ".pdf"
        val myPdfDocument = PdfDocument()
        val paint = Paint()
        val paint2 = Paint()
        val path = Path()
        val myPageInfo =
            PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create()
        val documentPage =
            myPdfDocument.startPage(myPageInfo)
        val canvas = documentPage.canvas
        var y = 25 // x = 10,
        //int x = (canvas.getWidth() / 2)
        var x = 10
        paint.getTextBounds(
            bankasia_name,
            0,
            bankasia_name.length,
            bounds
        )
        x = canvas.width / 2 - bounds.width() / 2
        canvas.drawText(bankasia_name, x.toFloat(), y.toFloat(), paint)

        paint.getTextBounds(
            statementTitle,
            0,
            statementTitle.length,
            bounds
        )

        x = canvas.width / 2 - bounds.width() / 2

        y += paint.descent().toInt() - paint.ascent().toInt()
        //(y += paint.descent() - paint.ascent())
        canvas.drawText(statementTitle, x.toFloat(), y.toFloat(), paint)
        y += paint.descent().toInt() - paint.ascent().toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += paint.descent().toInt() - paint.ascent().toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += paint.descent().toInt() - paint.ascent().toInt()
        x = 10
        canvas.drawText("SL", x.toFloat(), y.toFloat(), paint)
        canvas.drawText("Date", 70F, y.toFloat(), paint)
        canvas.drawText("Debit Amount", 170F, y.toFloat(), paint)
        canvas.drawText("Credit Amount", 270F, y.toFloat(), paint)
        canvas.drawText("Balance", 370F, y.toFloat(), paint)
        canvas.drawText("Particurlars", 470F, y.toFloat(), paint)
        y += paint.descent().toInt() - paint.ascent().toInt().toInt()
        x = 10
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //blank space
        y += paint.descent().toInt() - paint.ascent().toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        /*   //  while (accountList)
            for (i in 0 until accountList!!.size) {

                y += paint.descent().toInt() - paint.ascent().toInt()
                canvas.drawText("", x.toFloat(), y.toFloat(), paint)
                y += paint.descent().toInt() - paint.ascent().toInt()
                canvas.drawText("", x.toFloat(), y.toFloat(), paint)

                x = 10
                canvas.drawText(accountList[i].slNo.toString(), x.toFloat(), y.toFloat(), paint)
                canvas.drawText(
                    accountList[i].transactionDate.toString(),
                    70F,
                    y.toFloat(),
                    paint
                )
                canvas.drawText(accountList[i].debtAmount.toString(), 170F, y.toFloat(), paint)
                canvas.drawText(
                    accountList[i].creditAmount.toString(),
                    270F,
                    y.toFloat(),
                    paint
                )
                canvas.drawText(
                    accountList[i].availaleBalance.toString(),
                    370F,
                    y.toFloat(),
                    paint
                )
                canvas.drawText(accountList[i].remarks.toString(), 470F, y.toFloat(), paint)


                y += paint.descent().toInt() - paint.ascent().toInt().toInt()
                x = 10
                canvas.drawText("", x.toFloat(), y.toFloat(), paint)

                //blank space
                y += paint.descent().toInt() - paint.ascent().toInt()
                canvas.drawText("", x.toFloat(), y.toFloat(), paint)
                //horizontal line
                path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
                paint2.color = Color.GRAY
                paint2.style = Paint.Style.STROKE
                path.moveTo(x.toFloat(), y.toFloat())
                canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)
            }
    */

        myPdfDocument.finishPage(documentPage)
//        val file = File(
//            Environment.getExternalStorageDirectory().toString() + file_name_path
//        )

        // val downloadFilePath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)


        var rootPath = this.getExternalFilesDir(null)?.absolutePath;
        try {
            myPdfDocument.writeTo(FileOutputStream(rootPath + file_name_path))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        myPdfDocument.close()

        //  viewPdfFile()

        // openStorageAccess()

        // selectPdfFromStorage()


    }

    fun directoryCreate() {

        try {
//            val file = File(
//                Environment.getExternalStorageDirectory(),
//                TextContants.smartapp_document_folder
//            )

            /*val file = File(
                this.getExternalFilesDir(null)?.absolutePath,
                "StorageCheck"
            )*/

            val dir = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator
                        + applicationContext.resources.getString(R.string.app_name)
                        + File.separator
            )

            if (!dir.exists()) {
                dir.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}