package com.cse.mist.users;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Main2Activity extends AppCompatActivity {

    EditText etname,etmail,etmobile;
    Button addData,btnn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;
        setContentView(R.layout.activity_main2);
        etname=(EditText)findViewById(R.id.etName);
        etmail=(EditText)findViewById(R.id.etemail);
        etmobile=(EditText)findViewById(R.id.etphn);
        addData=(Button)findViewById(R.id.tvRegBtn);



        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(valid()){
                    saveinfo();
                }
                else {
                    //  Toast.makeText(context,"Invalid info ",Toast.LENGTH_SHORT).show();
                }


            }
        });



    }

    public void saveinfo(){


        String Name,Email,Mobile;

        Name=etname.getText().toString().trim();
        Email= etmail.getText().toString().trim();
        Mobile=etmobile.getText().toString().trim();

        BackgroundTask backgroundTask =new BackgroundTask();
        backgroundTask.execute(Name,Email,Mobile);
        Intent intent = new Intent(context, Welcome.class);
        //String name = etname.getText().toString();
        intent.putExtra("name",Name);
        startActivity(intent);
        finish();



    }


    class BackgroundTask extends AsyncTask<String,Void,String>{

        String add_info_url;

        @Override
        protected void onPreExecute() {
            add_info_url="http://cse.stereoserver.com/interfacing/add_user_info.php";
        }

        @Override
        protected String doInBackground(String... args) {

            String name,email,mobile;
            name = args[0];
            email =args[1];
            mobile =args[2];
            try {
                URL url = new URL(add_info_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data_string = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"+
                        URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("mobile","UTF-8")+"="+URLEncoder.encode(mobile,"UTF-8");

                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //get response from server
                InputStream inputStream=httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "inserted";
                //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }



        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

        }

    }



    public boolean valid(){
        String Name,Email,Mobile;

        Name=etname.getText().toString().trim();
        Email= etmail.getText().toString().trim();
        Mobile=etmobile.getText().toString().trim();

        if(Name.isEmpty()||Email.isEmpty()||Mobile.isEmpty()){

            Toast.makeText(context,"Fill up all the field",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    public void back(){


    }
}
