package pedsafe.wifip2p.com;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by aawesh on 5/30/17.
 */

public class ClientThread extends AsyncTask{

    String TAG  = "ClientThread";
    private DataDisplayActivity dataDisplayActivity;

    String result = "";
    String message = "Greetings from client";
    InetAddress hostAddress;
    int port;
    int len;

    //datatransfer activity passes the address of the group host and the port for the use in the thread
    public ClientThread(InetAddress hostAddress, int port,DataDisplayActivity dataDisplayAcrivity){
        this.hostAddress = hostAddress;
        this.port = port;
        this.dataDisplayActivity = dataDisplayAcrivity;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Socket socket = new Socket();
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            byte buf[]  = new byte[1024];
            socket.bind(null);
            socket.connect((new InetSocketAddress(hostAddress, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = new ByteArrayInputStream(message.getBytes());
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }

        /**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d(TAG,"o === " + o);
        if(o != null){
            dataDisplayActivity.d1textView.setText("For now Clent sends only");
            dataDisplayActivity.d2textView.setText("Sent: "+message);
            Log.d(TAG,"Client sent this:"+message);
        }
    }
}
