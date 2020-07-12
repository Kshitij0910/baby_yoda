package com.example.baby_yoda;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragmentWithRedAlartFragment extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final String TAG = "HomeFragmentWithRedAlar";

    public static final int REQUEST_LOCATION_CODE = 99;
    final int SEND_SMS_PERMISSION_REQUEST_CODE=1000;

    private GoogleApiClient client;
    private  LocationRequest locationRequest;
    private Location lastlocation;
    double latitude,longitude;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    String message;


    Button redAlert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.frag_home_with_red_alart, container, false);

        mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        redAlert=view.findViewById(R.id.red_alert);


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        redAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSMSPermission(Manifest.permission.SEND_SMS)){

                    onSend();
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                }
            }
        });


        if (mapFragment==null){
            FragmentManager fm=getFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            mapFragment=SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
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
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED){
                        if (client == null){
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Permission is required to access Maps!", Toast.LENGTH_SHORT).show();
                }
                return;

            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    onSend();
                }
                else {
                    Toast.makeText(getContext(), "Allow us to send an alert to your loved ones.", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());

        Geocoder geocoder=new Geocoder(getActivity());
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
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();

        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient(){
        client=new GoogleApiClient.Builder(getContext())
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
                Toast.makeText(getActivity(), "Alert sent to your loved ones...hang in there", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onSend: "+phnNumber+", "+message);
            }
            catch (Exception e){
                Toast.makeText(getActivity(), "Alert not sent", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            SmsManager smsManager=SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            PendingIntent sentPI = PendingIntent.getBroadcast(getContext(), 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity(), 0,
                    new Intent(DELIVERED), 0);

            ArrayList<PendingIntent> sendList = new ArrayList<>();
            sendList.add(sentPI);

            ArrayList<PendingIntent> deliverList = new ArrayList<>();
            deliverList.add(deliveredPI);

            smsManager.sendMultipartTextMessage(phnNumber, null, parts, sendList, deliverList);

        }
        else {
            Toast.makeText(getContext(), "Allow us to send an alert to your loved ones.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkSMSPermission(String smsPermission){
        int check=ContextCompat.checkSelfPermission(getActivity(), smsPermission);
        Log.d(TAG, "checkSMSPermission: "+check);
        return (check==PackageManager.PERMISSION_GRANTED);
    }
}