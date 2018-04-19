/*Mwape
 Eros Android App
 Version 1.0
 */

package com.example.mwape.project_eros;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class InterfaceActivity extends AppCompatActivity implements ClockFragment.OnFragmentInteractionListener, LED.OnFragmentInteractionListener, MainScreen.OnFragmentInteractionListener {

    LED led;
    public Boolean D2, D3, D4 = true;      //NodeMCU pins
    public Boolean bSwitch = true;         //switch for turning ON/OFF

    public int networkID; //the network id

    public static WifiManager wifiManager;
    public Context context;
    public WifiConfiguration configuration;
    public Client client;

    public static String ssid="yourid";    //your network name here
    public static String pass="*****";     //passeword of your network goes here
    public byte[] buffer = new byte[1024];//used to sending information to esp is a form of byte


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_activity);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        context=this;

        // this is for thread policy the AOS doesn't allow to transfer data using wifi module so we take the permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void wifi_connect (View v){


        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(bSwitch == true){        //if the switch is on, then turn on the wifi

            wifiSwitch(context, bSwitch);
            bSwitch=false;
            Toast.makeText(getApplicationContext(), "Powering...", Toast.LENGTH_SHORT).show();
            //wifi configuration .. all the code below is to explain the wifi configuration of which type the wifi is
            //if it is a WPA-PSK protocol then it would work
            configuration = new WifiConfiguration();        //Create new object for Wifi Config
            configuration.SSID = "\"" + ssid + "\"";
            configuration.preSharedKey = "\"" + pass + "\"";
            configuration.status = WifiConfiguration.Status.ENABLED;

            //Handlers
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            networkID = wifiManager.addNetwork(configuration);
            wifiManager.disconnect();
            wifiManager.enableNetwork(networkID, true);
            wifiManager.reconnect();


        } else {                            //else turn off the wifi
            wifiSwitch(context, bSwitch);
            bSwitch = true;
            Toast.makeText(getApplicationContext(), "turning off...", Toast.LENGTH_SHORT).show();

        }

    }


    public void GPIO1CONTROL (View v){       //button control for corresponding GPIO port
       // led.GPIO1CONTROL(v);

        if(!D2){            //If GPIO D2 is currently off

            D2 = true;                  //turn it on, create new client and erase buffer
            client = new Client();
            buffer = null;
            buffer = ("5").getBytes(); //allocate value 5 to buffer
            client.run();
            Toast.makeText(InterfaceActivity.this, "OFF", Toast.LENGTH_SHORT).show();
        }

        else{                       //If GPIO D2 is currently off

            D2 = true;
            client = new Client();//object of class client
            buffer = null;
            buffer = ("10").getBytes();
            client.run(); //use run() in class client to send data
            Toast.makeText(InterfaceActivity.this, "ON", Toast.LENGTH_SHORT).show();
        }
    }

    public static void wifiSwitch (Context context, boolean isTurnToOn) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isTurnToOn);
    }

    //used to send data to esp module
    public class Client implements Runnable{
        private final static String SERVER_ADDRESS = "192.168.4.1";//public ip of my server
        private final static int SERVER_PORT = 8888;


        public void run(){

            InetAddress serverAddr;
            DatagramPacket packet;
            DatagramSocket socket;


            try {
                serverAddr = InetAddress.getByName(SERVER_ADDRESS);
                socket = new DatagramSocket(); //DataGram socket is created
                packet = new DatagramPacket(buffer, buffer.length, serverAddr, SERVER_PORT);//Data is loaded with information where to send on address and port number
                socket.send(packet);//Data is send in the form of packets
                socket.close();//Needs to close the socket before other operation... its a good programming
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
