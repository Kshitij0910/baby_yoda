package com.example.baby_yoda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhysicalFitnessActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "PhysicalFitnessActivity";
    private DrawerLayout drawer;

    public static final int REQUEST_LOCATION_CODE = 99;
    final int SEND_SMS_PERMISSION_REQUEST_CODE=1000;
    private GoogleApiClient client;
    private LocationRequest locationRequest;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_fitness);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ChipNavigationBar bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(bottomNavListener);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            bottomNavigationView.setItemSelected(R.id.nav_step_counter, true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;



            case R.id.nav_alert:
                //Toast.makeText(this, "Sending alert...hang in there!", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    checkLocationPermission();
                }
                if (checkSMSPermission(Manifest.permission.SEND_SMS)){

                    onSend();
                }
                else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                }
                break;



        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private ChipNavigationBar.OnItemSelectedListener bottomNavListener=new ChipNavigationBar.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int i) {
            Fragment selectedFragment = null;
            switch (i) {
                case R.id.nav_step_counter:
                    selectedFragment = new StepCounterFragment();
                    break;

                case R.id.nav_video:
                    selectedFragment = new SuggestedWorkoutVideosFragment();
                    break;

                /*
                case R.id.nav_note_me:
                    selectedFragment=new NoteTakingFragment();
                    break;
                */

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();


        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return false;
        }
        else
            return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED){
                        if (client == null){
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this,"Permission is required to access Maps!", Toast.LENGTH_SHORT).show();
                }
                return;

            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    onSend();
                }
                else {
                    Toast.makeText(this, "Allow us to send an alert to your loved ones.", Toast.LENGTH_LONG).show();
                }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        locationRequest=new LocationRequest();

        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder=new Geocoder(this);
        List<Address> myCurrentAddressList;
        Address myCurrentAddress=null;

        try {
            myCurrentAddressList=geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            for (int i = 0; i < myCurrentAddressList.size(); i++)
                myCurrentAddress=myCurrentAddressList.get(i);
            message=myCurrentAddress.toString();

            Log.d("My Current Address", "onLocationChanged: "+myCurrentAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient(){
        client=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();

    }

    public void onSend(){
        String phnNumber="tel:9307594108";
        String number=phnNumber.trim();
        Log.d(TAG, "onSend: called");

        if (number == null || number.length()==0 || message ==null || message.length()==0 ){
            Log.d(TAG, "onSend: null");
            return;
        }
        Log.d(TAG, "onSend: "+checkSMSPermission(Manifest.permission.SEND_SMS));
        if (checkSMSPermission(Manifest.permission.SEND_SMS)){
            Log.d(TAG, "onSend: sending");
            try {
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
                Toast.makeText(this, "Alert sent to your loved ones...hang in there", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onSend: "+phnNumber+", "+message);
            }
            catch (Exception e){
                Toast.makeText(this, "Alert not sent", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            SmsManager smsManager=SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            ArrayList<PendingIntent> sendList = new ArrayList<>();
            sendList.add(sentPI);

            ArrayList<PendingIntent> deliverList = new ArrayList<>();
            deliverList.add(deliveredPI);

            smsManager.sendMultipartTextMessage(phnNumber, null, parts, sendList, deliverList);

        }
        else {
            Toast.makeText(this, "Allow us to send an alert to your loved ones.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkSMSPermission(String smsPermission){
        int check=ContextCompat.checkSelfPermission(this, smsPermission);
        Log.d(TAG, "checkSMSPermission: "+check);
        return (check==PackageManager.PERMISSION_GRANTED);
    }
}