package com.androiddeft.remotemysqlconnection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AckActivity extends AppCompatActivity {
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_MOVIE_NAME = "phn_no";
    private static final String BASE_URL = "http://192.168.0.4/interface/";
    private ArrayList<HashMap<String, String>> movieList;
    private ListView movieListView;
    private ProgressDialog pDialog;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        movieListView = (ListView) findViewById(R.id.movieList);


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




            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://cse.stereoserver.com/interfacing/button.php/?Button_on=0&Led_on=0&Id=5").openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                connection.getResponseCode();


            } catch (IOException e) {
                Toast.makeText(AckActivity.this.getApplicationContext(), "Unable to connect to the internet", 1).show();
            }
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







