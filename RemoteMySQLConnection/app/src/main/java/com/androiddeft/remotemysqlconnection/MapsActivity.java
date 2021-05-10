package com.androiddeft.remotemysqlconnection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.androiddeft.remotemysqlconnection.helper.HttpJsonParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_MOVIE_NAME = "phn_no";
    private static final String BASE_URL = "http://cse.stereoserver.com/interfacing/";
    private static String movieName = null;
    private static String Led_on = null;
    private static  Integer movieId =0;
    private static Double a = 0.0;
    private static Double b = 0.0;
    private ArrayList<HashMap<String, String>> movieList;
    private ListView movieListView;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker, mCurrLocationMarker1;
    LocationRequest mLocationRequest;
    private GoogleMap mMap, mMap1;
    public static String arrayReceived[];

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        arrayReceived = bundle.getStringArray("MyArray");



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        // Bundle bundle = getIntent().getExtras();
        //  String arrayReceived[] = bundle.getStringArray("MyArray");


        //Place current location marker
      a = Double.valueOf(arrayReceived[0]);
         b = Double.valueOf(arrayReceived[1]);
        Toast.makeText(MapsActivity.this,Double.toString(a)+"_"+Double.toString(b),
                Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(a, b);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));



        //mCurrLocationMarker = mMap.addMarker(line);
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }



            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getSubLocality()).append(",");
                result.append(address.getAddressLine(0)).append("\n");




            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }


    public void send(View v){

        new  Send().execute();

    }



    class Send extends AsyncTask<String, Void, String> {



        @SuppressLint("WrongConstant")
        protected String doInBackground(String... urls) {

            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "fetch_phn.php", "GET", null);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray movies;
                if (success == 1) {
                    movieList = new ArrayList<>();
                    movies = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate movies list
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject movie = movies.getJSONObject(i);
                        movieId = movie.getInt(KEY_MOVIE_ID);
                        movieName = movie.getString(KEY_MOVIE_NAME);

                        HashMap<String, String> map = new HashMap<String, String>(2);
                        map.put(KEY_MOVIE_ID, movieId.toString());
                        map.put(KEY_MOVIE_NAME, movieName);

                        movieList.add(map);
                        String id = String.valueOf(movieId);
                        if(id.equals(arrayReceived[2])){
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


         /*   String Led_on= "1";
            String Id = String.valueOf(movieId);


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://cse.stereoserver.com/interfacing/phpcode.php");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("Led_on", Led_on));
                    nameValuePairs.add(new BasicNameValuePair("Id", Id));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    //phn


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }*/


            String Led_on= "0";
            String Id = String.valueOf(movieId);



            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://cse.stereoserver.com/interfacing/phpcode.php?Led_on=1&Id=5").openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                connection.getResponseCode();
            } catch (IOException e) {
                Toast.makeText(MapsActivity.this.getApplicationContext(), "Unable to connect to the internet", 1).show();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {

        }

        @SuppressLint("WrongConstant")
        protected void onPostExecute(String result) {

            phoneNo = movieName;
            String msg = getAddress(a,b);
            message = "I am in touble at "+msg;
            String Id = String.valueOf(movieId);
            Toast.makeText(getApplicationContext(),
                    phoneNo+"_"+Id, Toast.LENGTH_LONG).show();


           if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                        Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }




                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNo));
                intent.putExtra("sms_body", message);
                startActivity(intent);




        }

    }

    public void sendack(View v){
        Toast.makeText(getApplicationContext(),
                "Final Ack sent", Toast.LENGTH_LONG).show();

        new Sendack().execute();

    }



    @SuppressLint("StaticFieldLeak")
    class Sendack extends AsyncTask<String, Void, String> {



        @SuppressLint("WrongConstant")
        protected String doInBackground(String... urls) {

         /*   String Name= "300";
            String Id = String.valueOf(movieId);




                HttpClient httpclient = new DefaultHttpClient();
               // HttpPost httppost = new HttpPost("http://192.168.0.108/interface/phpcode1.php");
            HttpPost httppost = new HttpPost("http://cse.stereoserver.com/interfacing/button.php?Button_on=0&Id=5");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("Name", Name));
                    nameValuePairs.add(new BasicNameValuePair("Id", Id));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    //phn


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }*/

            String Button_on = "0";
            String Id = String.valueOf(movieId);



            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://cse.stereoserver.com/interfacing/button.php/?Button_on=0&Led_on=0&Id=5").openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                connection.getResponseCode();


            } catch (IOException e) {
                Toast.makeText(MapsActivity.this.getApplicationContext(), "Unable to connect to the internet", 1).show();
            }
            phoneNo = movieName;
            message = "your girl is safe now";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNo));
            intent.putExtra("sms_body", message);
            startActivity(intent);

            return null;
        }







        }
        protected void onProgressUpdate(Integer... progress) {

        }
    @SuppressLint("WrongConstant")
        protected void onPostExecute(String result) {

            phoneNo = "01971156119";
            message = "your girl is safe now";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNo));
            intent.putExtra("sms_body", message);
            startActivity(intent);





        }

    }


