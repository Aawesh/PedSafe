package pedsafe.wifip2p.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    public TextView mTextView;
    public ListView mListView;
    public InetAddress inetAddress;
    public Boolean isHost;

    private Button searchButton, transferButton, disconnectButton;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WiFiDirectBroadcastReceiver mReciever;
    private IntentFilter mIntentFilter;
    private ArrayAdapter<String> wifiP2pArrayAdapter;
    private Intent dataDisplay;

    private final static String TAG = "MainAcrivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mTextView = (TextView) findViewById(R.id.textView1);
        mListView = (ListView)findViewById(R.id.listView);

        wifiP2pArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mListView.setAdapter(wifiP2pArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mReciever.connect(position);
            }
        });

        searchButton = (Button)findViewById(R.id.button1);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(v);
            }
        });

        transferButton = (Button)findViewById(R.id.button2);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer(v);
            }
        });

        disconnectButton = (Button)findViewById(R.id.button3);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReciever.disconnect();
                mTextView.setText("Disconnected");
                wifiP2pArrayAdapter.clear(); // clear all from display
            }
        });

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);
        mReciever = new WiFiDirectBroadcastReceiver(mManager,mChannel,this);
    }

    public void search(View view){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mTextView.setText("Wifi-Direct: Searching ...");
                Log.d(TAG,"onSuccess: Wifi-Direct: Searching ...");
            }

            @Override
            public void onFailure(int reason) {
                mTextView.setText("search() - onFailure() - Error code: "+reason);
                Log.d(TAG,"search() - onFailure() - : Error code: "+reason);
            }
        });
    }

    public void transfer(View v){

        if(inetAddress != null && isHost != null){
            Toast.makeText(getApplicationContext(),"Transfer will start soon ...",Toast.LENGTH_SHORT).show();

            dataDisplay = new Intent(this,DataDisplayActivity.class);
            dataDisplay.putExtra("HostAddress",inetAddress.getHostAddress());
            dataDisplay.putExtra("IsHost",isHost);
            dataDisplay.putExtra("Connected",true);
            startActivity(dataDisplay);

        }else{
            Toast.makeText(getApplicationContext(),"Connection has not been established",Toast.LENGTH_SHORT).show();
        }


    }

    public void displaPeers(WifiP2pDeviceList peerList){
        wifiP2pArrayAdapter.clear();

        for(WifiP2pDevice peer: peerList.getDeviceList()){
            wifiP2pArrayAdapter.add(peer.deviceName + "\n" + peer.deviceAddress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReciever,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReciever);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReciever.disconnect();
    }
}
