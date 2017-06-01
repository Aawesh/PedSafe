package pedsafe.wifip2p.com;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aawesh on 5/30/17.
 */

public class DataDisplayActivity extends AppCompatActivity {

    public TextView d1textView, d2textView;
    public ClientThread clientThread;
    public ServerThread serverThread;

    public InetAddress hostAddress;
    public String stringHostAddress;
    public Timer timer;
    public TimerTask timerTask;

    public Intent intent;
    public boolean isHost;

    int port = 8888;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datadisplay);

        d1textView = (TextView)findViewById(R.id.textView3);
        d2textView = (TextView)findViewById(R.id.textView4);

        intent = getIntent();

        if(intent.getBooleanExtra("Connected",false)){
            stringHostAddress = intent.getStringExtra("HostAddress");

            try{
                hostAddress = InetAddress.getByName(stringHostAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


            //Determine if the user is the host or the client
            isHost = intent.getBooleanExtra("IsHost",false);

            if(isHost){
                serverThread = new ServerThread(port,this);
                serverThread.execute();
            }else{
                clientThread = new ClientThread(hostAddress,port,this);
                clientThread.execute();
            }

        }

    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isHost){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("serverThread inside runonui= " + serverThread);
                            d1textView.setText("I am a server");
                            d2textView.setText("Device 2: " + serverThread.getDevice2String());
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("clientThread inside runonui= " + clientThread);
                            d1textView.setText("Device 1: " + clientThread.getDevice1String());
                            d2textView.setText("Device 2: " + clientThread.getDevice2String());
                        }
                    });
                }
            }


        };
        timer.schedule(timerTask,10,10);
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
