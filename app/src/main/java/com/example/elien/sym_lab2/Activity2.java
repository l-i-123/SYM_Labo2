package com.example.elien.sym_lab2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {
    private TextView email = null;
    private TextView imei = null;
    private ImageView image = null;

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);



    }

}
