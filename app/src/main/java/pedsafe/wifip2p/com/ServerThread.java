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

public class ServerThread implements Runnable{

    String TAG  = "ServerThread";

    String device1String = "HOST";
    String device2String = "";

    InetAddress clientAddress;
    int initPort = 0;

    DatagramSocket socket;

    byte[] sendData = new byte[64];
    byte[] receiveData = new byte[64];

    int sendcount = 1;
    int receiveCount = 0;

    boolean gotPacket = false;

    public ServerThread(int initPort){
        this.initPort = initPort;
    }

    //this is called immediately after the port is established

    @Override
    public void run(){
        while(true){
            //this creates a new socket if not already created, using the given port number and it runs on the first iteration
            try{
                if(socket == null){
                    socket = new DatagramSocket(initPort);
                    socket.setSoTimeout(1); //1 millisecond
                }
            } catch (SocketException e) {
                handleException(e.getMessage());
            }


            //receive here
            //server cannot send until it receives from someone and obtains the client address because server don't know the client address but everyone knows the serveraddress


            //create an empty packet to await incoming packet
            DatagramPacket receivedPacket = new DatagramPacket(receiveData,receiveData.length);
            Log.d(TAG, "Waiting for packet");


            //try for receiving
            try{
                //receive packet and extract data from it
                socket.receive(receivedPacket);
                receivedPacket.getData();

                device2String = new String(receivedPacket.getData(),0,receivedPacket.getLength());
                Log.d(TAG,"Received packet contained: "+device2String);

                receiveCount++;

                //get Client's address once a packet is received
                if(clientAddress == null){
                    clientAddress = receivedPacket.getAddress();
                    gotPacket = true;
                }
            } catch (IOException e) {
                System.out.println("Second try serverthread= " );
                handleException(e.getMessage());
                continue;
            }//end receive


            //start send
            try{
                if(gotPacket){
                    sendData = (device1String + sendcount).getBytes();
                    sendcount++;

                    DatagramPacket packet = new DatagramPacket(sendData,sendData.length,clientAddress,initPort);

                    //store data in UDP packet
                    socket.send(packet);
                    Log.d(TAG,"Server: Packet sent: "+device2String);
                }
            } catch (IOException e) {
                System.out.println("Third try serverthread= " );
                handleException(e.getMessage());
                continue;
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
