package com.example.tcpipprinter

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.tcpipprinter.databinding.ActivitySignupBinding
import connection.ConnectionClass
import java.sql.Connection
import java.sql.Statement

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val context: Context = this@Signup
    private lateinit var connectionClass: ConnectionClass
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private var pressedTime: Long = 0
    private val INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Invalid username or password"
    private val PLEASE_ENTER_USERNAME_MESSAGE = "Please enter username"
    private val PLEASE_ENTER_PASSWORD_MESSAGE = "Please enter password"
    var username=""
    var password=""
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
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
            progressDialog!!.dismiss()
        }, 200)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonLogin.setOnClickListener {
            username = binding.editTextUsername.text.toString()
            password = binding.editTextPassword.text.toString()
            checkData(username, password)



        }
        binding.editTextUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the error message when the user types in the username field
                binding.editTextUsername.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the error message when the user types in the password field
                binding.editTextPassword.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkData(username: String, password: String) {
        if (username.isBlank()) {
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(500)
            Toast.makeText(this, PLEASE_ENTER_USERNAME_MESSAGE, Toast.LENGTH_LONG).show()
            return
        }
        if (password.isBlank()) {
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(500)
            Toast.makeText(this, PLEASE_ENTER_PASSWORD_MESSAGE, Toast.LENGTH_LONG).show()
            return
        }
        try {
            var n = 0
            val sql =
                "insert into login_Info values (?,?)"
            val statement = con.prepareStatement(sql)
            statement.setString(1, username)
            statement.setString(2,password )
            n = statement.executeUpdate()
            if (n > 0)
            {
                progressDialog!!.show()
                binding.editTextUsername.text?.clear()
                binding.editTextPassword.text?.clear()

                Toast.makeText(this, "successfully Inserted", Toast.LENGTH_SHORT).show()

                progressDialog!!.dismiss()
            }
            else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

            }
        } catch (e: java.lang.Exception) {
            Log.e(ContentValues.TAG, "error: " + e.message)
            e.printStackTrace()
        }


    }


}