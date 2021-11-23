package com.example.androidstorage11permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.action.PdfAction
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue


import java.io.*
import java.lang.Exception


object PdfGenerator {

    private val linkSample = "Raihan Mahamud"
    var filePath = ""

    private lateinit var outputPath: String
    private lateinit var error: String
    val buffer = ByteArray(1024)

    // Output stream to write file
    var outputStream: OutputStream? = null


    fun generatePdf(context: Context, info: PdfGeneratorModel) {

        val FILENAME = "account_statement" + DateUtil.getVisaCurDateTime() + ".pdf"
        filePath = getAppPath(context) + FILENAME

        if (File(filePath).exists()) {
            File(filePath).delete()
        }

        val fOut = FileOutputStream(filePath)
        val pdfWriter = PdfWriter(fOut)

        // Creating a PdfDocument
        val pdfDocument =
            PdfDocument(pdfWriter)
        val layoutDocument = Document(pdfDocument)


        // title
        addTitle(layoutDocument, info.header)

        //add empty line
        // addEmptyLine(layoutDocument, 1)

        //Add sub heading
        val appName = "Account Statement of - " + info.accountNo
        addSubHeading(layoutDocument, "${appName}")
        //addLink(layoutDocument, linkSample)

        //add empty line
        // addEmptyLine(layoutDocument, 1)

        // customer reference information
        // addDebitCredit(layoutDocument, info)

        //add empty line
        //  addEmptyLine(layoutDocument, 1)

        //Add sub heading
        addSubHeading(layoutDocument, info.dateRange)

        //Add list
        addTable(layoutDocument, info.list)

        layoutDocument.close()
        Toast.makeText(context, "Pdf saved successfully to location $filePath", Toast.LENGTH_LONG)
            .show()
        Log.e("filePath", filePath.toString())

        // FileUtils.openFile(context, File(filePath))


    }


    fun getAppPath(context: Context): String {
        var newdir = " "
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i("TAG", "Downloading -> Android 11 or Above")
            try {

                val dir = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator
                            + context.resources.getString(R.string.app_name)
                            + File.separator
                )

                if (!dir.exists()) {
                    dir.mkdir()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

            val dir = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator
                        + context.resources.getString(R.string.app_name)
                        + File.separator
            )
            newdir = dir.path + File.separator
            if (!dir.exists()) {
                dir.mkdir()
            }
        }


        /*val dir = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator
                    + context.resources.getString(R.string.app_name)
                    + File.separator
        )
        if (!dir.exists()) {
            dir.mkdir()
        }*/

        // return dir.path + File.separator
        return newdir + File.separator
    }

    private fun addTable(layoutDocument: Document, items: List<AccountStatementAdapterModel>) {

        val table = Table(
            UnitValue.createPointArray(
                floatArrayOf(
                    80f,
                    80f,
                    85f,
                    80f,
                    190f
                )
            )
        )

        // headers
        //table.addCell(Paragraph("S.N.O.").setBold())
        table.addCell(
            Paragraph("Date").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
        )
        table.addCell(
            Paragraph("Debit Amount").setBold().setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
        )
        table.addCell(
            Paragraph("Credit Amount").setBold().setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
        )
        table.addCell(
            Paragraph("Balance").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
        )
        table.addCell(
            Paragraph("Particulars").setBold().setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
        )
        //table.addCell(Paragraph("Date").setBold())

        // items
        for (a in items) {
//            table.addCell(Paragraph(a.SNO.toString() + ""))
            table.addCell(
                Paragraph(a.transactionDate + "").setFontSize(10f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(
                Paragraph(a.debtAmount.toString() + "").setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10f)
            )
            table.addCell(
                Paragraph(a.creditAmount.toString() + "").setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10f)
            )
            table.addCell(
                Paragraph(a.availaleBalance.toString() + "").setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10f)
            )
            table.addCell(Paragraph((a.remarks).toString() + "").setFontSize(10f))
            //table.addCell(Paragraph(a.transactionDateStr + ""))
        }
        layoutDocument.add(table)
    }

    private fun addEmptyLine(layoutDocument: Document, number: Int) {
        for (i in 0 until number) {
            layoutDocument.add(Paragraph(" "))
        }
    }

    private fun addDebitCredit(layoutDocument: Document, info: PdfGeneratorModel) {

        val table = Table(
            UnitValue.createPointArray(
                floatArrayOf(
                    100f,
                    160f
                )
            )
        )

        /*  table.addCell(Paragraph("Total Credit").setBold())
          table.addCell(Paragraph(info.totalCredit + ""))
          table.addCell(Paragraph("Total Debit").setBold())
          table.addCell(Paragraph(info.totalDebit + ""))
          table.addCell(Paragraph("Current Balance").setBold())
          table.addCell(Paragraph(info.balance + ""))
          table.addCell(Paragraph("Date Range" + "").setBold())
          table.addCell(Paragraph(info.dateRange + ""))

          layoutDocument.add(table)*/
    }

    private fun addSubHeading(layoutDocument: Document, text: String) {
        layoutDocument.add(
            Paragraph(text).setBold()
                .setTextAlignment(TextAlignment.CENTER)
        )
    }

    private fun addLink(layoutDocument: Document, text: String) {

        val blueText: Text = Text(text)
            .setFontColor(ColorConstants.BLUE)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))

        layoutDocument.add(
            Paragraph(blueText)
                .setAction(PdfAction.createURI(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setUnderline()
                .setItalic()
        )
    }

    private fun addTitle(layoutDocument: Document, text: String) {
        layoutDocument.add(
            Paragraph(text).setBold().setUnderline()
                .setTextAlignment(TextAlignment.CENTER)
        )
    }

    fun viewPdfFile(context: Context) {


        val file = File(filePath)

        val intent =
            Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)

    }

    fun scanMediaFile(context: Context) {
        val file = File(filePath)
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }


}