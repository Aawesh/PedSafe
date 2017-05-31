package pedsafe.wifip2p.com;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by aawesh on 5/30/17.
 */

public class ClientThread implements Runnable{

    String TAG  = "ClientThread";

    String device1String = "CLIENT";
    String device2String = "";

    InetAddress hostAddress;
    int port = 0;

    DatagramSocket socket;

    byte[] sendData = new byte[64];
    byte[] receiveData = new byte[64];

    int sendcount = 1;
    int receiveCount = 0;


    //datatransfer activity passes the address of the group host and the port for the use in the thread
    public ClientThread(InetAddress hostAddress, int port){
        this.hostAddress = hostAddress;
        this.port = port;
    }

    //called when theread is kicked off by datatransferdisplay activity

    @Override
    public void run(){

        if(hostAddress != null && port !=0){
            //send packet non stop
            while(true){
                //this creates a new socket using the given port number and it runs on the first iteration
                try{
                    if(socket == null){
                        socket = new DatagramSocket(port);
                        socket.setSoTimeout(1); //1 millisecond
                    }
                } catch (SocketException e) {
                    handleException(e.getMessage());
                }

                //Ready to send
                //send a packet containing the message "CLIENT"
                try{
                    sendData  = (device1String + sendcount).getBytes();
                    sendcount++;

                    DatagramPacket packet = new DatagramPacket(sendData,sendData.length,hostAddress,port);
                    socket.send(packet);
                    Log.d(TAG,"Client: packet sent");
                } catch (IOException e) {
                    System.out.println("Second try clientthread= " );

                    handleException(e.getMessage());
                }//end send



                //receive packets
                try{
                    //create a packet to send the incoming packet
                    DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
                    socket.receive(receivePacket);
                    receivePacket.getData();//extract the data from the packet

                    //convert the packet back to string
                    device2String = new String(receivePacket.getData(),0,receivePacket.getLength());
                    receiveCount++;
                } catch (IOException e) {
                    System.out.println("Third try clientthread= " );
                    handleException(e.getMessage());
                    continue;
                } //end receive

            }
        }
    }

    public void handleException(String message){
        if(message == null){
            Log.d(TAG, "Unknown Message");
        }else{
            Log.d(TAG, message);
        }

    }


    //used by datatransferdisplay
    public String getDevice1String(){
        return (device1String + receiveCount);
    }

    public String getDevice2String(){
        return device2String;
    }


}
