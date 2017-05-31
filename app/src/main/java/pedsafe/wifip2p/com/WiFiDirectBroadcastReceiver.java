package pedsafe.wifip2p.com;

/**
 * Created by aawesh on 5/30/17.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
        * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
        */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private List<WifiP2pDevice> mPeerList;
    private List<WifiP2pConfig> mConfigList;
    private WifiP2pDevice mDevice;
    private boolean isGrouoFormed = false;

    private final static String TAG = "WiFiDirectBroadcastReceiver";


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                mActivity.mTextView.setText("Wifi-Direct Enabled");
            } else {
                // Wi-Fi P2P is not enabled
                mActivity.mTextView.setText("Wifi-Direct Disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            mPeerList = new ArrayList<WifiP2pDevice>();
            mConfigList = new ArrayList<WifiP2pConfig>();

            if(mManager != null){
                WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener(){

                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        mPeerList.clear();
                        mPeerList.addAll(peers.getDeviceList());

                        mActivity.displaPeers(peers);

                        mPeerList.addAll(peers.getDeviceList()); //TODO reduntant

                        for(int i=0;i<peers.getDeviceList().size();i++){
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = mPeerList.get(i).deviceAddress;
                            mConfigList.add(config);
                        }

                    }
                };
                mManager.requestPeers(mChannel,peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                mManager.requestConnectionInfo(mChannel,connectionInfoListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    public void connect(int position){
        WifiP2pConfig config = mConfigList.get(position);
        mDevice = mPeerList.get(position);

        //connects the two and displays the toast message
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mActivity.mTextView.setText("Connected to: "+mDevice.deviceName + " " + mDevice.deviceAddress);
                Log.d(TAG,"Connected to: "+mDevice.deviceName + " " + mDevice.deviceAddress);

                //TODO not sure about this
                //Device is connected so extract group info from the ConnectionInfoListener
                //mManager.requestConnectionInfo(mChannel,connectionInfoListener); //TODO not sure, remove if it works
            }

            @Override
            public void onFailure(int reason) {
                mActivity.mTextView.setText("connect() - onFailure() - Error: "+ reason);
                Log.d(TAG,"connect() - onFailure() - Error: "+ reason);
            }
        });
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if(info.groupFormed){
                isGrouoFormed = true;
                if(info.isGroupOwner){
                    mActivity.mTextView.setText("HOST" ); // TODO make the activity do this setText task, just send the messave and view info from here
//                    mActivity.inetAddress = groupOwnerAddress;
//                    mActivity.isHost = true;
                    mActivity.transfer(groupOwnerAddress,true);
                }else{
                    mActivity.mTextView.setText("CLIENT"); // TODO make the activity do this setText task, just send the messave and view info from here
//                    mActivity.inetAddress = groupOwnerAddress;
//                    mActivity.isHost = false;
                    mActivity.transfer(groupOwnerAddress,false);

                }
            }
        }
    };

    public void disconnect(){
        if(isGrouoFormed){
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG,"Removed group");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG,"Remove group failed: Error: "+reason);
                }
            });
        }
    }
}
