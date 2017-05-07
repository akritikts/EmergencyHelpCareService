package in.silive.emergency.network;


import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import in.silive.emergency.listeners.MapAsynResponse;

public class NearestPlaceDetails  extends AsyncTask<String, Integer, String> {

        HashMap<String, String> placedetail = null;

        public MapAsynResponse delegate = null;

    public NearestPlaceDetails(MapAsynResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

        @Override
        protected String doInBackground(String... params) {


            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(params[0]);
                //making connection with url
                urlConnection = (HttpURLConnection) url.openConnection();
                //connect with url
                urlConnection.connect();
                //taking input from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                //reading from buffered reader
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();
                //closing all connections
                br.close();
                iStream.close();
                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data; // return result
        }

        @Override
        protected void onPostExecute(String result) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONObject jsonPlace = jsonObject.getJSONObject("result");

                placedetail = getPlaceDetails(jsonPlace);

                delegate.processFinish(placedetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }  catch (Exception e){
                e.printStackTrace();
            }
            finally {


            }

        }
        private HashMap<String, String> getPlaceDetails(JSONObject jPlaceDetails){

            HashMap<String, String> hPlaceDetails = new HashMap<String, String>();

            String name = "";
            String vicinity="";
            String latitude="";
            String longitude="";
            String formatted_address="";
            String formatted_phone="";
            String website="";
            String international_phone_number="";


            try {

                if(!jPlaceDetails.isNull("name")){
                    name = jPlaceDetails.getString("name");
                }

                if(!jPlaceDetails.isNull("vicinity")){
                    vicinity = jPlaceDetails.getString("vicinity");
                }

                if(!jPlaceDetails.isNull("formatted_address")){
                    formatted_address = jPlaceDetails.getString("formatted_address");
                }

                if(!jPlaceDetails.isNull("formatted_phone_number")){
                    formatted_phone = jPlaceDetails.getString("formatted_phone_number");
                }

                if(!jPlaceDetails.isNull("website")){
                    website = jPlaceDetails.getString("website");
                }

                if(!jPlaceDetails.isNull("international_phone_number")){
                    international_phone_number = jPlaceDetails.getString("international_phone_number");
                }

                latitude = jPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lng");
                //putting values in hashmap
                hPlaceDetails.put("name", name);
                hPlaceDetails.put("vicinity", vicinity);
                hPlaceDetails.put("lat", latitude);
                hPlaceDetails.put("lng", longitude);
                hPlaceDetails.put("formatted_address", formatted_address);
                hPlaceDetails.put("formatted_phone", formatted_phone);
                hPlaceDetails.put("website", website);
                hPlaceDetails.put("international_phone_number", international_phone_number);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return hPlaceDetails;//return hashmap
        }
    }

