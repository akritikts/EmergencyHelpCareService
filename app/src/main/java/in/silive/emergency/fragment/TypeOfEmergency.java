package in.silive.emergency.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import in.silive.emergency.R;
import in.silive.emergency.activity.MapsActivity;
import in.silive.emergency.listeners.AddressResponse;
import in.silive.emergency.network.LocationAddress;
import in.silive.emergency.service.CurrentLocation;


public class TypeOfEmergency extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button hospital, police, pharmacy, blood;
    TextView address;
    double latitude = 0, longitude = 0;
    String currentaddress = "";
    android.support.v7.widget.CardView cardView;

    Context context;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    public TypeOfEmergency() {
        // empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.typeofemergency, container, false);

        context = getActivity();
        hospital = (Button) linearLayout.findViewById(R.id.bthospital);
        police = (Button) linearLayout.findViewById(R.id.btpolice);
        pharmacy = (Button) linearLayout.findViewById(R.id.btpharmacy);
        address = (TextView) linearLayout.findViewById(R.id.tvaddress);
        blood = (Button)linearLayout.findViewById(R.id.btblood);
        cardView = (android.support.v7.widget.CardView)linearLayout.findViewById(R.id.cardviewlocation);


        hospital.setOnClickListener(this);
        police.setOnClickListener(this);
        pharmacy.setOnClickListener(this);
        blood.setOnClickListener(this);

        buildGoogleApiClient();
        try {
            displayLocation();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        getAddress();

        //object of current location
        CurrentLocation getcurrentLocation = new CurrentLocation(getContext());

        boolean isNetworkenable = getcurrentLocation.isNetworkEnable();

        if (!isNetworkenable) {
            //showing alertDialog if gps is off
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

            alertDialog.setTitle("GPS is settings");

            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getContext().startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.setCancelable(true);
                }
            });


            alertDialog.setCancelable(false);
            alertDialog.show();

        }

        return linearLayout;

    }

    private void getAddress() {
        final LocationAddress locationAddress = new LocationAddress(getContext(), new AddressResponse() {
            @Override
            public void processFinish(String output) {

                    currentaddress = output;
                    if (currentaddress != null) {
                        //if address is not null then make it visible in textview
                        cardView.setVisibility(View.VISIBLE);
                        address.setVisibility(View.VISIBLE);
                        address.setText("Your Location:\n" + currentaddress);
                    }
            }
        });

        locationAddress.execute(latitude, longitude);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bthospital:

                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("type", "hospital");
                startActivity(intent);

                break;
            case R.id.btpharmacy:

                intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("type", "pharmacy");
                startActivity(intent);

                break;
            case R.id.btpolice:

                intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("type", "police");
                startActivity(intent);

                break;
            case R.id.btblood:
                intent = new Intent(getContext(),MapsActivity.class);
                intent.putExtra("type","blood");
                startActivity(intent);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void displayLocation() {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        }


        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

        }else{
            currentlocation();
        }

    }

    private void currentlocation() {

        //object of current location
        CurrentLocation currentLocation = new CurrentLocation(getContext());
        //accessing location
        Location location = currentLocation.getLocation(LocationManager.NETWORK_PROVIDER);
        //if location is not null
        if (location != null) {
            //get latitude from location
            latitude = location.getLatitude();
            //get longitude from location
            longitude = location.getLongitude();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
