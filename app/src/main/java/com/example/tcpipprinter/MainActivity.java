package com.example.tcpipprinter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
TcpConnectionExample tcpConnectionExample;
Button print,Ok;
ImageView imgBack;
EditText ip;
String printerip;
    TextToSpeech tts;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print=findViewById(R.id.print);
        imgBack=findViewById(R.id.imgBack);
        Ok=findViewById(R.id.Ok);
        ip=findViewById(R.id.iptxt);

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        try {
            // sendZplOverTcp(printerip);
            sendZplOverTcp("10.0.4.28");
            //  ConvertTextToSpeech();
        }
        catch (ConnectionException e) {
            e.printStackTrace();

        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerip=ip.getText().toString();
                Log.d("printerippp1", "onClick: "+printerip);
                try {
                   // sendZplOverTcp(printerip);
                    sendZplOverTcp("10.0.4.28");
                  //  ConvertTextToSpeech();
                }
                catch (ConnectionException e) {
                    e.printStackTrace();

                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });



//        tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
//
//            @Override
//            public void onInit(int status) {
//                // TODO Auto-generated method stub
//                if(status == TextToSpeech.SUCCESS){
//                    int result=tts.setLanguage(Locale.US);
//                    if(result==TextToSpeech.LANG_MISSING_DATA ||
//                            result==TextToSpeech.LANG_NOT_SUPPORTED){
//                        Log.e("error", "This Language is not supported");
//                    }
//                    else{
//                        ConvertTextToSpeech();
//                    }
//                }
//                else
//                    Log.e("error", "Initilization Failed!");
//            }
//        });


    }

    public  void sendZplOverTcp(String theIpAddress) throws ConnectionException {
        // Instantiate connection for ZPL TCP port at given address
        Connection thePrinterConn = new TcpConnection(theIpAddress, TcpConnection.DEFAULT_ZPL_TCP_PORT);

        try {
            // Open the connection - physical connection is established here.
            thePrinterConn.open();

            // This example prints "This is a ZPL test." near the top of the label.
            /////shiii
           //String zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL   Dikskaskskas   test.^FS^XZ";
            ///////Barcode
            //String zplData="^XA\\n\\r^MNM\\n\\r^FO050,50\\n\\r^B8N,100,Y,N\\n\\r^FD1234567\\n\\r^FS\\n\\r^PQ3\\n\\r^XZ";
///////
//            String zplData="'^XA',\n" + "'^CF01,40,20',\n" +
//                    "'^FO40,70^FWN^FD' PRODUCT '^FS',\n" +
//                    "'^BY1,2,60',\n" +
//                    " '^FO70,130^BC^FD'1234567'^FS',\n" +
//                    "'^XZ',";

            String zplData="\u0010CT~CD,~CC^~CT\n" +
                    "^XA\n" +
                    "~TA000\n" +
                    "~JSN\n" +
                    "^LT0\n" +
                    "^MNW\n" +
                    "^MTT\n" +
                    "^PON\n" +
                    "^PMN\n" +
                    "^LH0,0\n" +
                    "^JMA\n" +
                    "^PR4,4\n" +
                    "~SD19\n" +
                    "^JUS\n" +
                    "^LRN\n" +
                    "^CI27\n" +
                    "^PA0,1,1,0\n" +
                    "^XZ\n" +
                    "^XA\n" +
                    "^MMT\n" +
                    "^PW400\n" +
                    "^LL200\n" +
                    "^LS0\n" +
                    "^FT133,125^A0N,106,106^FH\\^CI28^FDOK^FS^CI27\n" +
                    "^FT60,181^A0N,40,41^FH\\^CI28^FDFOR SHIPMENT^FS^CI27\n" +
                    "^PQ1,0,1,Y\n" +
                    "^XZ";
//
//            String zplData1=  "\u001B##\u0001À\b\u0006ï»¿\u0010CT~CD,~CC^~CT\n" +
//                    "^XA\n" +
//                    "~TA000\n" +
//                    "~JSN\n" +
//                    "^LT0\n" +
//                    "^MNW\n" +
//                    "^MTT\n" +
//                    "^PON\n" +
//                    "^PMN\n" +
//                    "^LH0,0\n" +
//                    "^JMA\n" +
//                    "^PR6,6\n" +
//                    "~SD15\n" +
//                    "^JUS\n" +
//                    "^LRN\n" +
//                    "^CI27\n" +
//                    "^PA0,1,1,0\n" +
//                    "^XZ\n" +
//                    "\u001B##\u0003\u001C     \u0001                       ^XA\n" +
//                    "^MMT\n" +
//                    "^PW799\n" +
//                    "^LL200\n" +
//                    "^LS0\n" +
//                    "^FT122,102^A@N,104,103,TT0003M_^FH\\^CI28^FDOK^FS^CI27\n" +
//                    "^PQ1,0,1,Y\n" +
//                    "^XZ\n" +
//                    "\u001B##\u0004  \u001B##";




            // Send the data to printer as a byte array.
            thePrinterConn.write(zplData.getBytes());
            Toast.makeText(MainActivity.this, "Printed", Toast.LENGTH_SHORT).show();
            //ConvertTextToSpeech();
            Log.d("sssdds1", "sendZplOverTcp: "+"toastt1");
        } catch (ConnectionException e) {
            // Handle communications error here.
            e.printStackTrace();
            Log.d("sssdds2", "sendZplOverTcp: "+e.toString());
            Toast.makeText(MainActivity.this,"Printedd",Toast.LENGTH_LONG).show();
        } finally {
            // Close the connection to release resources.
            thePrinterConn.close();
            Log.d("sssdds3", "sendZplOverTcp: "+"toastt");
        }
    }

    void sendCpclOverTcp(String theIpAddress) throws ConnectionException {
        // Instantiate connection for CPCL TCP port at given address
        Connection thePrinterConn = new TcpConnection(theIpAddress, TcpConnection.DEFAULT_CPCL_TCP_PORT);

        try {
            // Open the connection - physical connection is established here.
            thePrinterConn.open();

            // This example prints "This is a CPCL test." near the top of the label.
            String cpclData = "! 0 200 200 210 1\r\n"
                    + "TEXT 4 0 30 40 This is a CPCL Saloni test.\r\n"
                    + "FORM\r\n"
                    + "PRINT\r\n";

            // Send the data to printer as a byte array.
            thePrinterConn.write(cpclData.getBytes());
            Toast.makeText(MainActivity.this, "firstblock1", Toast.LENGTH_SHORT).show();
        } catch (ConnectionException e) {
            // Handle communications error here.
            e.printStackTrace();
        } finally {
            // Close the connection to release resources.
            thePrinterConn.close();
        }
    }

    void printConfigLabelUsingDnsName(String dnsName) throws ConnectionException {
        Connection connection = new TcpConnection(dnsName, 9100);
        try {
            connection.open();
            ZebraPrinter p = ZebraPrinterFactory.getInstance(connection);
            p.printConfigurationLabel();
            Toast.makeText(MainActivity.this, "firstblock3", Toast.LENGTH_SHORT).show();
        } catch (ConnectionException e) {
            e.printStackTrace();
            Log.d("ssdsdsd1", "printConfigLabelUsingDnsName: " + e.getMessage().toString());
        } catch (ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
            Log.d("ssdsdsd2", "printConfigLabelUsingDnsName: " + e.getMessage().toString());
        } finally {
            // Close the connection to release resources.
            connection.close();
        }

    }
    //////////////////////////////////
    private void ConvertTextToSpeech() {
        // TODO Auto-generated method stub
        String text = "Helloooo";
        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak("Printed", TextToSpeech.QUEUE_FLUSH, null);
    }

    }