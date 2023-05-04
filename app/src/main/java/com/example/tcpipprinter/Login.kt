package com.example.tcpipprinter

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tcpipprinter.databinding.ActivityLoginBinding
import connection.ConnectionClass
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class Login : AppCompatActivity() {
    private val context: Context = this@Login
    private lateinit var connectionClass: ConnectionClass
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private var progressDialog: ProgressDialog? = null
    private val INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Invalid username or password"
    private val PLEASE_ENTER_PASSWORD_MESSAGE = "Please enter password"
    private var pressedTime: Long = 0
    var conditionDate=0
    private var isPasswordVisible = false
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectionClass = ConnectionClass(context)

        Handler(mainLooper).postDelayed({
            con = connectionClass.CONN()!!
            statement = con.createStatement()
           // selectDate()
        }, 200)

        condition()



//20230502  < dt
//
//        try {
//            val query = "SELECT dt FROM dt "
//            val preparedStatement = con.prepareStatement(query)
//            preparedStatement.setString(1, dt)
//            val resultSet = preparedStatement.executeQuery()
//            if (resultSet.next()) {
//                val usernamedb = resultSet.getString("username")
//                val passdb = resultSet.getString("password")
//                Log.d("LOGIN", "username: $usernamedb")
//                Log.d("LOGIN", "password: $passdb")
//            }
//        }


        binding.buttonLogin1.setOnClickListener {
            if ( binding.editTextUsername.text.toString().isEmpty()) {
                val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(300)
                binding.editTextUsername.error="Please enter username"
                binding.editTextUsername.requestFocus()
                //  Toast.makeText(this, "Please enter username", Toast.LENGTH_LONG).show()
               // return@setOnClickListener
            }
            if ( binding.editTextPassword.text.toString().isEmpty()) {
                val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(300)
                binding.editTextPassword.error="Please enter Password"
                binding.editTextPassword.requestFocus()
                //Toast.makeText(this, "Please enter Password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val username =  binding.editTextUsername.text.toString()
            val password =  binding.editTextPassword.text.toString()
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

private fun condition(){
    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    Log.e("currentDate",currentDate)
    Log.e("conditionDate",conditionDate.toString())
    if ("20230512".toInt()<currentDate.toInt()) {
        val d=  SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        d.titleText = "Please Contact Software Support!"
        d.confirmText = "Ok"
        //  .setCancelable(false)
        // .setCanceledOnTouchOutside(false)
        d.setConfirmClickListener { obj: SweetAlertDialog ->
            obj.dismiss()
            finish()

//            startActivity(intent)
//            startActivity(Intent(this,ContactDeveloper::class.java))
        }
        d.setCancelable(false)
        d.show()
    }
}
    fun togglePasswordVisibility(view: View) {
        isPasswordVisible = !isPasswordVisible
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        if (isPasswordVisible) {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT
            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0,
                R.drawable.baseline_visibility_24, 0
            )
        } else {
            passwordEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0,
                R.drawable.baseline_visibility_off_24, 0
            )
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis()
    }


    private fun selectDate() {
        progressDialog = ProgressDialog(this@Login)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
       // progressDialog!!.show()
        try {
            val query = "SELECT dt FROM dt"
            val preparedStatement = con.prepareStatement(query)
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                 conditionDate = resultSet.getString("dt").toString().toInt()
                Log.e("Date", "Date: $conditionDate")
               // condition()
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Unknown error", e)
        }
    }
    private fun checkData(username: String, password: String) {
        progressDialog = ProgressDialog(this@Login)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()
        try {
            val query = "SELECT * FROM login_info WHERE username = ? AND password = ?"
            val preparedStatement = con.prepareStatement(query)
            preparedStatement.setString(1, username)
            preparedStatement.setString(2, password)
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                val usernamedb = resultSet.getString("username")
                val passdb = resultSet.getString("password")
                Log.d("LOGIN", "username: $usernamedb")
                Log.d("LOGIN", "password: $passdb")
                if (usernamedb == username && passdb == password) {
                    progressDialog!!.dismiss()
                    val intent = Intent(this, Scanning::class.java)
                    // val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    progressDialog!!.dismiss()
                    Toast.makeText(this@Login, "Login Successfully", Toast.LENGTH_LONG).show()
                    return
                } else {
                    binding.editTextUsername.text?.clear()
                    binding.editTextPassword.text?.clear()
                    val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(300)
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "Invalid Username or password", Toast.LENGTH_LONG).show()
                }
            }
            binding.editTextUsername.text?.clear()
            binding.editTextPassword.text?.clear()
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(300)
            binding.editTextUsername.requestFocus()

            progressDialog!!.dismiss()
            Toast.makeText(this, "Invalid Username or password", Toast.LENGTH_LONG).show()


        } catch (e: SQLException) {
            Log.e("LOGIN", "Error executing SQL query", e)
            Toast.makeText(this, INVALID_USERNAME_OR_PASSWORD_MESSAGE, Toast.LENGTH_LONG).show()
            binding.editTextUsername.text?.clear()
            progressDialog!!.dismiss()

            binding.editTextPassword.text?.clear()
        } catch (e: Exception) {
            Log.e("LOGIN", "Unknown error", e)
            Toast.makeText(this, INVALID_USERNAME_OR_PASSWORD_MESSAGE, Toast.LENGTH_LONG).show()
            binding.editTextUsername.text?.clear()
            progressDialog!!.dismiss()
            binding.editTextPassword.text.clear()
        }
    }


}



