package connection

import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class ConnectionClass(private val context: Context)
{
    companion object {
        private const val ip = "43.230.201.20" //server-> 43.230.201.20 // 192.168.1.60 //sahil 192.168.1.62 //Amee 192.168.1.24
        private const val port = "1232" //server-> 1232 // 1433
        private const val classes = "net.sourceforge.jtds.jdbc.Driver"
        private const val database = "Stotcp" //Stotcp //BCP KrishnaPaper //WayinfotechInventory
        //LatMayank // KINSS // //Mayank //Mayank //WayinfotechInventory
        private const val username = "sa" //DESKTOP-G4GMIEB //sa //DESKTOP-B369L9E
        private const val password = "Yu6SBA5s4u#zcT6%e" // 1322 server -> Yu6SBA5s4u#zcT6%e //root
        private const val url = "jdbc:jtds:sqlserver://$ip:$port/$database"
    }

    @SuppressLint("NewApi")
    fun CONN(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        try {
            Class.forName(classes)
            conn = DriverManager.getConnection(url, username, password)
            Log.e("Success", "Server Connected")
        }
        catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
//          Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
        }
        return conn
    }
}
