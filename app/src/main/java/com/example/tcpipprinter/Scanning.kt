package com.example.tcpipprinter

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.print.PrintHelper
import com.example.tcpipprinter.Report.Report
import com.example.tcpipprinter.databinding.ActivityScanningBinding
import connection.ConnectionClass
import java.io.OutputStream
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class Scanning : AppCompatActivity() {
    private lateinit var binding: ActivityScanningBinding

    private val context: Context = this@Scanning
    private lateinit var connectionClass: ConnectionClass
    private lateinit var con: Connection
    private lateinit var statement: Statement
    var progressDialog: ProgressDialog? = null

    // Store the values of the packaging and invoice part numbers as they are scanned
    var packagingPartNumber = ""
    var invoicePartNumber = ""
    var invoiceQty = ""

    // Store the values of the packaging and invoice quantities as they are scanned
    var packagingQuantity = ""
    var invoiceQuantity = ""
    var packagingQty = ""
    var okay = "okay"
    var dateorg = ""
    var date1 = ""
    private val PERMISSIONS_REQUEST_BLUETOOTH = 1
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothDevice: BluetoothDevice
    private lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var mOutputStream: OutputStream
    private var mIsBluetoothConnected = false
    private val printerIpAddress = "192.168.3.100" // Replace with your printer's IP address
    private val printerPort = 9100 // Replace with your printer's port number
    private val barcodeData = "1234567890" // Replace with the barcode data you want to print

    companion object {
        const val MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 1
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectionClass = ConnectionClass(context)
        // Initialize the Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        binding.btnGenerateSticker.isEnabled = false

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()
        binding.edtpacks.requestFocus()
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        Handler(mainLooper).postDelayed({
            progressDialog!!.show()
            con = connectionClass.CONN()!!
            statement = con.createStatement()
            progressDialog!!.dismiss()
        }, 200)
        dateorg = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        date1 = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())


//        val multiFormatReader = MultiFormatReader()
//
//// Set the possible formats to read
//        val hints = mutableMapOf<DecodeHintType, Any>()
//        hints[DecodeHintType.POSSIBLE_FORMATS] = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128)
//
//// Decode the scanned data
//        val rawResult: Result = multiFormatReader.decodeWithState(com.google.zxing.common.BinaryBitmap(com.google.zxing.common.HybridBinarizer(com.google.zxing.common.DecoderResult(data, width, height))))
//
//// Check the format of the scanned code
//        val barcodeFormat = rawResult.barcodeFormat
//        if (barcodeFormat == BarcodeFormat.QR_CODE) {
//
//            // The scanned code is a QR code
//        } else if (barcodeFormat == BarcodeFormat.CODE_128) {
//            // The scanned code is a barcode
//        } else {
//            // The scanned code is neither a barcode nor a QR code
//        }


// Request runtime permissions for Bluetooth
        if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                ), PERMISSIONS_REQUEST_BLUETOOTH
            )
        } else {
            // Bluetooth permissions are already granted
        }


        //function
        binding.buttonsave.setOnClickListener {
            save()
        }
        binding.btnShowMenu.setOnClickListener {
            showPopupMenu(it) // pass the button view as an argument
        }
        // Check if the app has permission to access Wi-Fi state
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Request the permission from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE
            )
        }
        binding.btnGenerateSticker.setOnClickListener {
           // startActivity(Intent(this, MainActivity::class.java))

//            // Generate the barcode bitmap
//            val barcodeBitmap = generateBarcodeBitmap("12345")
//
//            // Connect to the printer
//            val printerAddress = "192.168.3.100"
//            val isConnected = connectToPrinter(printerAddress)
//
//            if (isConnected) {
//                // Send the print job to the printer
//                printBitmap(barcodeBitmap, printerAddress)
//            } else {
//                // Handle the case where the printer is not connected
//                Log.e("Print Error", "Failed to connect to printer")
//            }
        }

        binding.edtpacks.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                packagingPartNumber = binding.edtpacks.text.toString().trim()
                binding.edtinvo.requestFocus()
                if (packagingPartNumber.isEmpty()) {
                    binding.edtpacks.setError("Please fill all fields")
                    return@setOnKeyListener true
                } else {
                    // binding.edtinvo.requestFocus()
                }
                return@setOnKeyListener true
            }
            false
        }

        binding.edtinvo.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                invoicePartNumber = binding.edtinvo.text.toString().trim()
                if (packagingPartNumber == invoicePartNumber) {
//                binding.edtpacks.setBackgroundColor(Color.GREEN)
//                binding.edtinvo.setBackgroundColor(Color.GREEN)
                    Toast.makeText(this, "Model number match", Toast.LENGTH_SHORT).show()
                    binding.edtpacksqty.requestFocus()

                } else {
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(500)

                    Toast.makeText(this, "Model number do not match", Toast.LENGTH_LONG).show()
                    binding.edtinvo.text?.clear()
                    binding.edtpacks.text?.clear()
                    binding.edtpacks.requestFocus()

//                binding.edtinvo.setError("Model number do not match")
//                binding.edtpacks.setError("Model number do not match")
                }
                return@setOnKeyListener true
            }
            false
        }
        binding.edtpacksqty.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                packagingQty = binding.edtpacksqty.text.toString().trim()
                binding.edtinvoqt.requestFocus()

                if (packagingQty.isEmpty()) {
                    binding.edtpacks.error = "Please fill all fields"
                    return@setOnKeyListener true
                } else {
                }
                return@setOnKeyListener true
            }
            false
        }
        binding.edtinvoqt.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                invoiceQty = binding.edtinvoqt.text.toString().trim()

                if (packagingQty == invoiceQty) {
//                binding.edtpacksqty.setBackgroundColor(Color.YELLOW)
//                binding.edtinvoqt.setBackgroundColor(Color.YELLOW)
                    Toast.makeText(this, "Qty match", Toast.LENGTH_SHORT).show()
                    binding.buttonsave.isEnabled = true
                    binding.btnGenerateSticker.isEnabled = true

                } else {
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(500)
                    Toast.makeText(this, "Qty do not match", Toast.LENGTH_LONG).show()
                    binding.edtpacksqty.requestFocus()
                    binding.edtinvoqt.text?.clear()
                    binding.edtpacksqty.text?.clear()

//                binding.edtinvoqt.setError("Qty do not match")
//                binding.edtpacksqty.setError("Qty do not match")
                }
                return@setOnKeyListener true
            }
            false
        }


    }

    private fun save() {
        try {
            var n = 0
            val sql =
                "insert into Insertion_tab values (?,?,?,?,?,?,?)"
            val statement = con.prepareStatement(sql)
            statement.setString(1, packagingPartNumber)
            statement.setString(2, invoicePartNumber)
            statement.setString(3, packagingQty)
            statement.setString(4, invoiceQty)
            statement.setString(5, okay)
            statement.setString(6, dateorg)
            statement.setString(7, date1)
            n = statement.executeUpdate()
            if (n > 0) {
                binding.buttonsave.isEnabled = false

                progressDialog!!.show()
                binding.edtinvo.text?.clear()
                binding.edtpacks.text?.clear()
                binding.edtinvoqt.text?.clear()
                binding.edtpacksqty.text?.clear()
                Toast.makeText(this, "successfully Inserted", Toast.LENGTH_SHORT).show()
                binding.edtpacks.requestFocus()
                startActivity(Intent(this, MainActivity::class.java))

                progressDialog!!.dismiss()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

            }
        } catch (e: java.lang.Exception) {
            Log.e(ContentValues.TAG, "error: " + e.message)
            e.printStackTrace()
        }

    }

    private fun enterclick() {


    }


    private fun checkqt(packagingQuantity: String, invoiceQty: String) {



    }

    private fun checkmodel(invoicePartNumber: String, packagingPartNumber: String?) {



    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE -> {
                // If the user granted the permission, do your Wi-Fi-related tasks here
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                }
                return
            }
            // Handle other permissions that your app requests here
            else -> {
                // Permission denied
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_report -> {
                // Handle report menu item click
                return true
            }
            R.id.menu_create_user -> {
                // Handle create user menu item click
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_report -> {
                    reporty()
                    true
                }
                R.id.menu_create_user -> {
                    createu()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun createu() {

        val intent = Intent(this, Signup::class.java)
        // start your next activity
        startActivity(intent)

    }

    private fun reporty() {
        val intent = Intent(this, Report::class.java)
        // start your next activity
        startActivity(intent)

    }

    // Create a function to generate a barcode image
    fun generateBarcodeBitmap(barcodeContent: String): Bitmap {
        val width = 400
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the barcode using the Paint object
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText(barcodeContent, 10f, 150f, paint)

        return bitmap
    }

    // Create a function to connect to the printer
    fun connectToPrinter(printerAddress: String): Boolean {
        val printerAddress = printerIpAddress

        // TODO: Use the printerAddress to connect to the printer
        // Simulate connection success/failure for demonstration purposes
        val isConnected = true // Set this to false to simulate connection failure

        // Return true if the connection is successful, false otherwise
        return isConnected
    }

    fun printBitmap(bitmap: Bitmap, printerAddress: String) {
        // Get the PrintHelper and set the orientation and scale mode
        val printHelper = PrintHelper(context)
        printHelper.orientation = PrintHelper.ORIENTATION_LANDSCAPE
        printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT

        // Print the bitmap
        printHelper.printBitmap("Barcode", bitmap)
    }

}
