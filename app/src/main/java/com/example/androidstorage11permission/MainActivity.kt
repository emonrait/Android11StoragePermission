package com.example.androidstorage11permission

import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.core.content.ContextCompat
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Build.VERSION.SDK_INT
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Button
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
    //creating a PdfWriter variable. PdfWriter class is available at com.itextpdf.text.pdf.PdfWriter
    // private val pdfWriter: PdfWriter? = null

    //we will add some products to arrayListRProductModel to show in the PDF document
    private val arrayListRProductModel = ArrayList<ProductModel>()

    var file_name_path = ""
    private val PERMISSION_REQUEST_CODE = 2296
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var save: Button
    var permission =
        arrayOf<String>(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )

    lateinit var dir: File
    private var isGenerating = false

    private var accountList: java.util.ArrayList<AccountStatementAdapterModel> =
        java.util.ArrayList<AccountStatementAdapterModel>()

    private lateinit var dummyInfo: PdfGeneratorModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        save = findViewById(R.id.save)

        val builder =
            StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val model = AccountStatementAdapterModel(
            "1",
            "25/01/2021",
            "02",
            "Test",
            "250",
            "270",
            "300"
        )
        accountList.add(model)

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

        if (checkPermission()) {
            directoryCreate()
            dummyInfo = dummyModel()

        } else {
            requestPermission()
        }
        createpdf(false) //just ask permission for first time

        save.setOnClickListener {
            if (checkPermission()) {
                directoryCreate()
                createpdf(true)

            } else {
                requestPermission()
            }
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


    fun directoryCreate() {

        try {

            dir = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator
                        + applicationContext.resources.getString(R.string.pdfFile)
                        + File.separator
            )


            if (!dir.exists()) {
                dir.mkdir()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //End card transaction List
    fun createpdf1() {
        try {
            dir = File(
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

        val bounds = Rect()
        val pageWidth = 300
        val pageheight = 500
        val pathHeight = 2
        val fileName: String = System.currentTimeMillis().toString()
        file_name_path = "/visacard_receit/$fileName.pdf"
        val myPdfDocument = PdfDocument()
        var paint = Paint()
        val paint2 = Paint()
        val path = Path()
        val myPageInfo = PageInfo.Builder(pageWidth, pageheight, 1).create()
        val documentPage = myPdfDocument.startPage(myPageInfo)
        val canvas = documentPage.canvas
        var y = 25 // x = 10,
        //int x = (canvas.getWidth() / 2);
        var x = 10
        paint.getTextBounds(
            "tv_digital_receipt_thank_title.getText().toString()",
            0,
            "tv_digital_receipt_thank_title.getText().toString()".length,
            bounds
        )
        x = canvas.width / 2 - bounds.width() / 2
        canvas.drawText(
            " tv_digital_receipt_thank_title.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_date.getText().toString()" + " " + "tv_digital_receipt_time.getText()",
            x.toFloat(),
            y.toFloat(),
            paint
        )


        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            " tv_digital_receipt_payment_amount_label.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_inr.getText().toString() " + " " + " tv_digital_receipt_payment_amount.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )


        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText("Merchant Name(MN)", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_merchant_name.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            " tv_digital_receipt_merchant_id.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        paint.color = Color.RED
        canvas.drawText(
            " tv_digital_receipt_sent.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        paint = Paint()

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_paid_with.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_paid_card.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            " tv_digital_receipt_payment_type.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_scan_to_pay.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_location.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_city.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )


        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_transaction_code.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(
            "tv_digital_receipt_transaction_no.getText().toString()",
            x.toFloat(),
            y.toFloat(),
            paint
        )


        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        val res = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.about_us)
        val b = Bitmap.createScaledBitmap(bitmap, 30, 30, false)
        canvas.drawBitmap(b, x.toFloat(), y.toFloat(), paint)
        val bitmap2 = BitmapFactory.decodeResource(res, R.drawable.about_us)
        val b2 = Bitmap.createScaledBitmap(bitmap2, 50, 30, false)
        canvas.drawBitmap(b2, 235f, y.toFloat(), paint)
        y += 25
        canvas.drawText(getString(R.string.app_name), 60f, y.toFloat(), paint)
        myPdfDocument.finishPage(documentPage)

        /* val file = File(Environment.getExternalStorageDirectory().toString() + file_name_path)
         if (!file.exists()) {
             file.mkdir()
         }*/
        try {
            myPdfDocument.writeTo(FileOutputStream(dir))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        myPdfDocument.close()
        // viewPdfFile()
        Toast.makeText(applicationContext, "Last", Toast.LENGTH_SHORT).show()
    }

    private fun dummyModel(): PdfGeneratorModel {
        val list = accountList
        val header = getString(R.string.app_name)
        val daterange = "25/02/2020"
        val accountno = "12345678"
        val dummy =
            PdfGeneratorModel(list, header, daterange, accountno)
        return dummy
    }

    fun createpdf(download: Boolean) {
        try {
            val permissionHelper = PermissionHelper(
                this,
                arrayOf(
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE

                ),
                100
            )
            permissionHelper.denied {
                if (it) {
                    Log.d("Permissioncheck", "Permission denied by system")
                    permissionHelper.openAppDetailsActivity()
                } else {
                    Log.d("Permissioncheck", "Permission denied")
                }
            }

//Request all permission
            permissionHelper.requestAll {
                Log.d("Permissioncheck", "All permission granted")

                if (!isGenerating && download) {
                    isGenerating = true
                    PdfGenerator.generatePdf(this, dummyInfo)
                    PdfGenerator.scanMediaFile(this)

                    val handler = Handler()
                    val runnable = Runnable {
                        //to avoid multiple generation at the same time. Set isGenerating = false on some delay
                        isGenerating = false
                    }
                    handler.postDelayed(runnable, 2000)

                    // viewPdfFile()
                    PdfGenerator.viewPdfFile(this)
                } else {
                    Log.v("errotsg", "Pdf not generate")
                }
            }

//Request individual permission
            permissionHelper.requestIndividual {
                Log.d("Permissioncheck", "Individual Permission Granted")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("errotsg", e.message.toString())
        }


    }

}