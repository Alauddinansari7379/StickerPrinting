package com.example.tcpipprinter.Report

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tcpipprinter.databinding.ActivityReportBinding
import com.mazenrashed.example.Report.adapter.InsertionDataAdapter
import com.mazenrashed.example.Report.model.InsertionData
import com.opencsv.CSVWriter
import connection.ConnectionClass
import java.io.FileWriter
import java.io.IOException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Report : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private val context: Context = this@Report
    private lateinit var connectionClass: ConnectionClass
    private lateinit var con: Connection
    var mydilaog: Dialog? = null
    var search=""
    private val PERMISSION_REQUEST_CODE = 112
    var startDate=""
    var endDate=""
    private lateinit var statement: Statement
    var date1=""
    var progressDialog: ProgressDialog? = null
    private val pickerList = ArrayList<InsertionData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectionClass = ConnectionClass(context)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()



        Handler(mainLooper).postDelayed({
            progressDialog!!.show()
            con = connectionClass.CONN()!!
            statement = con.createStatement()
           // fetch()
            progressDialog!!.dismiss()
        }, 200)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.tvDateS.text = currentDate
        binding.tvDateE.text = currentDate

        if (!checkPermission()) {
            requestPermission()
        }

        mydilaog?.setCanceledOnTouchOutside(false)
        mydilaog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val newCalendar1 = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                DateFormat.getDateInstance().format(newDate.time)
                // val Date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(newDate.time)
                startDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(newDate.time)
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(newDate.time)
                binding.tvDateS.text = date
                search=""

//                fetch(search, selectedDate)
                Log.e(ContentValues.TAG, "onCreate: >>>>>>>>>>>>>>>>>>>>>>$date")
                Log.e(ContentValues.TAG, "selectedDate: >>$startDate")
            },
            newCalendar1[Calendar.YEAR],
            newCalendar1[Calendar.MONTH],
            newCalendar1[Calendar.DAY_OF_MONTH]
        )
        binding.layoutStartDate.setOnClickListener {
            datePicker.show()

        }

        mydilaog?.setCanceledOnTouchOutside(false)
        mydilaog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val newCalendar2 = Calendar.getInstance()
        val datePicker2 = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                DateFormat.getDateInstance().format(newDate.time)
                // val Date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(newDate.time)
                endDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(newDate.time)
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(newDate.time)
                binding.tvDateE.text = date
                search=""

                //fetch(startDate, endDate)
                Log.e(ContentValues.TAG, "onCreate: >>>>>>>>>>>>>>>>>>>>>>$date")
                Log.e(ContentValues.TAG, "selectedDate: >>$endDate")
            },
            newCalendar2[Calendar.YEAR],
            newCalendar2[Calendar.MONTH],
            newCalendar2[Calendar.DAY_OF_MONTH]
        )
        binding.layoutEndDate.setOnClickListener {
            datePicker2.show()

        }

        binding.imgSearch.setOnClickListener {
            if (binding.tvDateS.text.isEmpty()){
                Toast.makeText(baseContext, "Please Select Date", Toast.LENGTH_SHORT).show()
            }else{
                fetch(startDate,endDate)

            }
        }
        binding.btnSaveFile.setOnClickListener {
            val d=  SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            d.titleText = "Are want to Save to File?"
            d.confirmText = "Yes"
            d.cancelText="No"
            //  .setCancelable(false)
            // .setCanceledOnTouchOutside(false)
            d.setConfirmClickListener { obj: SweetAlertDialog ->
                obj.dismiss()
                finish()
                callMakeExcel()


//            startActivity(intent)
//            startActivity(Intent(this,ContactDeveloper::class.java))
            }
            d.setCancelable(false)
            d.show()

        }

    }
////20230401---date1
//    "select PackagingPartno, InvoicePartno, PackagingQty, InvoiceQty, 'ok' as Confirmation," +
//    " left(date1,4)+'\\'+right(left(date1,6),2)+'\\'+right(date1,2) +' '+RIGHT(LEFT(DATE,17),5) " +
//    "AS DATE from Insertion_tab where date1 between '20230501' and '20230502' order by date1 desc"
    private fun fetch(startDate:String,endDate:String) {

    progressDialog = ProgressDialog(this@Report)
    progressDialog!!.setMessage("Loading..")
    progressDialog!!.setTitle("Please Wait")
    progressDialog!!.isIndeterminate = false
    progressDialog!!.setCancelable(true)
    progressDialog!!.show()
        pickerList.clear()
        val query =
            "select PackagingPartno, InvoicePartno, PackagingQty, InvoiceQty, 'ok' as Confirmation," +
                    " left(date1,4)+'\\'+right(left(date1,6),2)+'\\'+right(date1,2) +' '+RIGHT(LEFT(DATE,17),5) " +
                    "AS DATE from Insertion_tab where date1 between '$startDate' and '$endDate' order by date1 desc"

    Log.e("ccx",query)
        val stmt = con.createStatement()
        val rs: ResultSet = stmt.executeQuery(query)
        if (!rs.isBeforeFirst) {
            Log.e(ContentValues.TAG, "No data found.")
            Toast.makeText(baseContext, "No data found.", Toast.LENGTH_SHORT).show()
            progressDialog?.dismiss()
        } else {
            while (rs.next()) {
                val packsticker = rs.getString("PackagingPartno")
                val invoicesticker = rs.getString("InvoicePartno")
                val packqt = rs.getString("PackagingQty")
                val invoqt = rs.getString("InvoiceQty")
                val confirm = rs.getString("Confirmation")
                val date = rs.getString("date")
               // val time = rs.getString("TIME")

                pickerList.add(
                    InsertionData(
                        packsticker,
                        invoicesticker,
                        packqt,
                        invoqt,
                        confirm,
                        date
                    )
                )
                Log.e(ContentValues.TAG, "ListData$pickerList")
            }
            binding.rvItemList.adapter = InsertionDataAdapter(this, pickerList)
            progressDialog?.dismiss()
            binding.tvcount.text = pickerList.size.toString()
        }
    }
    private fun callMakeExcel() {
        var writer: CSVWriter? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
                val csv =
                    Environment.getExternalStorageDirectory().absolutePath + "/download/Csv_" + System.currentTimeMillis() + ".csv"
                writer = CSVWriter(FileWriter(csv))
                val data: MutableList<Array<String>> = java.util.ArrayList()
                data.add(
                    arrayOf(
                        "Packing Part",
                        "Invoice Part",
                        "Packing Qty",
                        "Invoice Qty",
                        "Ok",
                        "Date"
                    )
                )

            for (i in pickerList) {
                data.add(
                    arrayOf(
                        i.packagingPartno,
                        i.invoicePartno,
                        i.packagingQty,
                        i.invoiceQty,
                        i.confirmation,
                        i.date
                    )

                )
            }
                writer.writeAll(data) // data is adding to csv
                Log.e("Report.TAG", "CSV $csv")
                Log.e("MainActivity", "Writer  " + writer.toString())
                writer.close()
                Toast.makeText(this@Report, "Excel Saved in Download File!", Toast.LENGTH_LONG).show()

        }catch (e: IOException) {
            Log.e("MainActivity", "Error  " + e.message)
            e.printStackTrace()
        }
    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .")
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }
}
