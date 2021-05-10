package com.cse.mist.users;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class Welcome extends AppCompatActivity {

    EditText etname;
    String arrayReceived;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle bundle = getIntent().getExtras();
        arrayReceived = bundle.getString("name");

        etname = (EditText) findViewById(R.id.etName);

        etname.setText(arrayReceived);
    }


}
