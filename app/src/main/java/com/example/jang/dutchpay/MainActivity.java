package com.example.jang.dutchpay;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;


public class MainActivity extends Activity {

    Socket m_socket;
    Handler mHandler;
    TextView text;
    EditText sendText;
    EditText IPText;
    Button sendButton;
    Button ConnectButton;
    boolean isConnected = false;
    String sendData;
    String ip;
    OutputStream out_stream;
    String MacAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MacAddress = getMacAddress(this);

        mHandler = new Handler();

        text = (TextView) findViewById(R.id.maintext);
        IPText = (EditText) findViewById(R.id.IPInput);
        ConnectButton = (Button) findViewById(R.id.Connect);
        sendText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.button);
        sendData = new String();

        ConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = new String(IPText.getText().toString());
                ConnectButton.setEnabled(false);
                IPText.setEnabled(false);
                new Thread(new Runnable() {
                    public void run() {
                        int port = 7385;


                        m_socket = new Socket();

                        try {
                            SocketAddress sock_addr = new InetSocketAddress(ip, port);

                            mHandler.post(new Runnable() {
                                public void run() {
                                    text.setText("Connecting...");
                                }
                            });

                            m_socket.connect(sock_addr);

                            isConnected = true;

                            mHandler.post(new Runnable() {
                                public void run() {
                                    text.setText("Success!");
                                }
                            });

                            out_stream = m_socket.getOutputStream();

                            out_stream.write(MacAddress.getBytes());


                        } catch (IOException ie) {
                            mHandler.post(new Runnable() {
                                public void run() {
                                    text.setText("Fail...");
                                    ConnectButton.setEnabled(true);
                                    IPText.setEnabled(true);
                                }
                            });

                        }
                    }
                }).start();

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected) {
                    sendData = sendText.getText().toString();
                    try {
                        out_stream.write(sendData.getBytes());
                        mHandler.post(new Runnable() {
                            public void run() {
                                text.setText("Sending Message");
                                sendText.setText("");
                            }
                        });
                    } catch (IOException ioe) {

                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        try {
            if (out_stream != null)
                out_stream.close();
            out_stream = null;
            if (m_socket != null)
                m_socket.close();
            m_socket = null;
        } catch (IOException ioe) {

        }

        super.onStop();
    }

    public String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();

        return macAddress;
    }

}
