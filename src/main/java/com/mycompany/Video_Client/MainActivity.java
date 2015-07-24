package com.mycompany.Video_Client;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void ConnectToServer(View view)
    {
        // Get textbox reference
        EditText textObj = (EditText) findViewById(R.id.ServerInfo);
        String s = textObj.getText().toString();
        int ind = s.indexOf(" ");

        // Find port and server address.
        final String serverAddress = s.substring(0, ind).trim();
        String port_s = s.substring(ind).trim();
        final int port = Integer.valueOf(port_s);

        // Start a new thread to avoid NetworkOnMainThreadException
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Socket socket = new Socket(serverAddress, port);

                    // Read the buffer size and send receive confirm
                    DataInputStream dis = new DataInputStream(socket.getInputStream());


                    int i = 0;
                    // TODO: change this while loop to listen to a "disconnect" button event
                    while (i < 5000)
                    {
                        // Read image size and look for line feed to denote the start of image
                        ByteArrayOutputStream c = new ByteArrayOutputStream();
                        int j = dis.readByte();
                        while(j != 10)
                        {
                            c.write(j);
                            j = dis.readByte();
                        }
                        int buffer_size = Integer.parseInt(c.toString());

                        // Read image as stream of bytes
                         byte[] b = new byte[buffer_size];
                        dis.readFully(b);

                        // TODO: find a way to remove the final keyword for bitmap
                        // Convert to bitmap and update UI
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView picture = (ImageView) findViewById(R.id.imageDsp);
                                picture.setImageBitmap(bitmap);
                            }
                        });
                        i++;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}