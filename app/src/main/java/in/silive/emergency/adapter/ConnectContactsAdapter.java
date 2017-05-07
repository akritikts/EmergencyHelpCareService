package in.silive.emergency.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import in.silive.emergency.model.Contact;
import in.silive.emergency.service.CurrentLocation;
import in.silive.emergency.network.LocationAddress;
import in.silive.emergency.R;
import in.silive.emergency.listeners.AddressResponse;


public class ConnectContactsAdapter extends ArrayAdapter{
    Context context;

    double latitude =0;
    double longitude=0;
    boolean isGPSEnabled = false;
    String address ="";

    ArrayList<Contact> list = new ArrayList<>();
    public ConnectContactsAdapter(Context context, int resource) {
        super(context, resource);
        this.context =context;
    }



    private class ViewHolder {
        TextView nameView;
        TextView phoneView;
        ImageButton callButton;
        ImageButton smsButton;
    }

    @Override
    public void add(Object object) {
        list.add((Contact)object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;         // Each row of the listView
        ViewHolder holder;

        if(row == null){
            /** create holder and row **/
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.connect_contact_row, null);
            holder = new ViewHolder();

            /** Initialize holder **/
            holder.nameView = (TextView)row.findViewById(R.id.tv_connect_contactname);
            holder.phoneView = (TextView)row.findViewById(R.id.tv_connect_contactphone);
            holder.callButton = (ImageButton)row.findViewById(R.id.bt_call);
            holder.smsButton = (ImageButton)row.findViewById(R.id.bt_sendmessage);
            row.setTag(holder);         // can be used to store data within a view
        }
        else        holder = (ViewHolder) row.getTag();

        /** storing data into the holder **/
        final Contact contact = (Contact) this.getItem(position); // get contact
        holder.nameView.setText(contact.getName());
        holder.phoneView.setText(contact.getPhoneNumber());     // save data into respective views
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyCallPermission();
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contact.getPhoneNumber().trim()));

                    /** Validate phone number before calling **/
                    context.startActivity(intent);

                }
                catch(Exception e){
                    Log.e("Call", e.getMessage(), e);
                }
            }
        });

        holder.smsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /** CALL SMS ACTIVITY **/
                insertDummyMessagePermission();
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    String phoneNumber = contact.getPhoneNumber().trim();
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    getCurrentLocation();

                    if (isGPSEnabled == true){
                        if (latitude != 0 && longitude != 0) {
                            final LocationAddress locationAddress = new LocationAddress(getContext(), new AddressResponse() {
                                @Override
                                public void processFinish(String output) {

                                    address = output;
                                    if (address != null) {

                                        intent.putExtra("sms_body", createEmergencyMessage().toString());
                                        context.startActivity(intent);
                                    } else {

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                        alertDialog.setTitle("Error");

                                        alertDialog.setMessage("Service not available. Reboot your device");

                                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.setCancelable(true);
                                            }
                                        });


                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    }
                                }
                            });
                            locationAddress.execute(latitude, longitude);

                        } else {

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            alertDialog.setTitle("Location");

                            alertDialog.setMessage("Cannot access location! Try again");

                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.setCancelable(true);
                                }
                            });


                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                }
            }
                else {

                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Sorry, your device doesn't connect to internet!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           alertDialog.setCancelable(true);
                        }
                    });
                    alertDialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }

            }
        });

        return row;
    }

    /**
     * The method validates the phone number before calling
     * @param number
     * @return
     */
    private boolean isValidPhoneNumber(String number){
        if(number.length()!= 0)     return false;
        return    Patterns.PHONE.matcher(number).matches();

    }

     public void getCurrentLocation(){

         CurrentLocation currentLocation = new CurrentLocation(getContext());

         Location location =currentLocation.getLocation(LocationManager.NETWORK_PROVIDER);

         isGPSEnabled = currentLocation.isGPSEnable();
if(isGPSEnabled == true) {
    if (location != null) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
         else {
             final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

             alertDialog.setTitle("GPS is settings");

             alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

             alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog,int which) {
                     Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                     context.startActivity(intent);
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
    }

    private StringBuilder createEmergencyMessage(){
        StringBuilder builder = new StringBuilder(context.getResources().getString(R.string.emergency_message));


        SharedPreferences preferences = context.getSharedPreferences("Profile", context.MODE_PRIVATE);
        /** add location before name **/

        String Slocation = "Latitude: "+ String.valueOf(latitude) + " , "+"Longitude: "+String.valueOf(longitude)+"\n";
        builder.append(Slocation+"\n");
        builder.append("Address:\n"+address+"\n");
        String name = preferences.getString("Name", null);
        if(name != null)        builder.append("\n"+name);
        return builder;
    }


   private class GeocoderHandler extends Handler {
       @Override
       public void handleMessage(Message message) {

           switch (message.what) {
               case 1:
                   Bundle bundle = message.getData();
                   address = bundle.getString("address");
                   break;
               default:
                   address = null;
           }

       }
   }
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private void insertDummyCallPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.CALL_PHONE)) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[] {android.Manifest.permission.CALL_PHONE},
                        REQUEST_CODE_ASK_PERMISSIONS);

                return;
            }
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {android.Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

    }
    private void insertDummyMessagePermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.SEND_SMS)) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[] {android.Manifest.permission.SEND_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS);

                return;
            }
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {android.Manifest.permission.SEND_SMS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

    }

}
