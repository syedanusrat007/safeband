package com.androiddeft.remotemysqlconnection;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends android.support.v7.app.AppCompatActivity {
    private Context context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        context = this;
        intUi();
    }

    private void intUi() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);

                Intent intent = new Intent(context, RemoteMySQLActivity.class);
                startActivity(intent);
                finish();


            }
        }, 3000);
    }
}


