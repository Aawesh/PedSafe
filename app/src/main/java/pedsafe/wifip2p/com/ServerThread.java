package pedsafe.wifip2p.com;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by aawesh on 5/30/17.
 */

public class ServerThread extends AsyncTask {

    String TAG  = "ServerThread";
    int initPort = 0;
    DataDisplayActivity dataDisplayActivity;
    String response = "";


    public ServerThread(int initPort,DataDisplayActivity dataDisplayAcrivity){
        this.initPort = initPort;
        this.dataDisplayActivity = dataDisplayAcrivity;
    }


    @Override
    protected Object doInBackground(Object[] params) {

        try {
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(initPort);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as text (display for now)
             */

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = client.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

            serverSocket.close();
            return response;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d(TAG,"o === " + o);
        if( o != null){
            dataDisplayActivity.d1textView.setText("For now Server Receives only");
            dataDisplayActivity.d2textView.setText("Received: "+response); //This saves the response from the client in the host side
            Log.d(TAG,"Server Received this: "+response);
        }else{
            Log.d(TAG,"Nothing retuned");
        }
    }


}
