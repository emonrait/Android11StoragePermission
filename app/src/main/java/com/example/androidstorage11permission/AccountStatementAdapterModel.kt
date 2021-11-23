package com.example.androidstorage11permission

class AccountStatementAdapterModel(
    slNo: String,
    transactionDate:String,
    checkNo:String,
    remarks:String,
    debtAmount:String,
    creditAmount:String,
    availaleBalance:String
) {
    var slNo : String? = ""
    var transactionDate : String? = ""
    var checkNo : String? = ""
    var remarks : String? = ""
    var debtAmount : String? = ""
    var creditAmount : String? = ""
    var availaleBalance : String? = ""

    init {
        this.slNo = slNo
        this.transactionDate = transactionDate
        this.checkNo = checkNo
        this.remarks = remarks
        this.debtAmount = debtAmount
        this.creditAmount = creditAmount
        this.availaleBalance = availaleBalance

    }
}
