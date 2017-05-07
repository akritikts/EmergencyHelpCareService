package in.silive.emergency.network;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.silive.emergency.listeners.AddressResponse;


public class LocationAddress extends AsyncTask<Double , String ,String> {


    private Context mContext;
    public AddressResponse addressResponse = null;

    //defining constructor
    public LocationAddress(Context context ,AddressResponse Response ){
        addressResponse = Response;
        mContext = context;

    }


    @Override
    protected String doInBackground(Double... params) {

        //getting latitude and longitude
        double latitude = params[0];
        double longitude = params[1];

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String result = null;
        try {
            //getting address from latitude and longitude
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            //if address list is not empty
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                //appending address
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                //appending locality of address
                if(address.getLocality()!=null)
                sb.append(address.getLocality()).append("\n");
                //appending country name of address
                if(address.getCountryName()!=null)
                sb.append(address.getCountryName());
                result = sb.toString();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        try {

            addressResponse.processFinish(s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}