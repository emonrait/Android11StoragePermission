package com.example.androidstorage11permission

import com.example.androidstorage11permission.AccountStatementAdapterModel
import java.util.ArrayList


class PdfGeneratorModel(
    list: ArrayList<AccountStatementAdapterModel>,
    header: String,
    daterange: String,
    accountNo: String
) {

    var list = emptyList<AccountStatementAdapterModel>()
    var header = ""

    //var totalCredit = ""
    // var totalDebit = ""
    // var balance = ""
    var dateRange = ""
    var accountNo = ""

    init {
        this.list = list
        this.header = header
        // calculateTotal(list)
        this.dateRange = daterange
        this.accountNo = accountNo
    }

    /* private fun calculateTotal(items: List<AccountStatementAdapterModel>) {
         val totalPlus = items.map {
             ValidationUtil.replacecomma(it.debtAmount!!).toDouble()

         }.sum()

         val totalMinus = items.map {
             ValidationUtil.replacecomma(it.creditAmount!!).toDouble()

         }.sum()

         val totalBal = items.map {
             ValidationUtil.replacecomma(it.availaleBalance!!).toDouble()

         }.sum()

       //  totalDebit = "-$totalMinus"
       //  totalCredit = totalPlus.toString()
       //  balance = totalBal.toString()

     }*/
}