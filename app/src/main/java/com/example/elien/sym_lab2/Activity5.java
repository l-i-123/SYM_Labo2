package com.example.elien.sym_lab2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity5 extends AppCompatActivity implements CommunicationEventListenerString{
    private TextView title = null;
    private EditText nbobject = null;
    private EditText log = null;
    private Button send = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity5);

        this.title = findViewById(R.id.titre);
        this.nbobject = findViewById(R.id.nbdata);
        this.log = findViewById(R.id.Log);
        this.send = findViewById(R.id.send);


        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new AsyncSendRequestJSON(Activity3.this,true).execute(new Data(name.getText().toString(),
                //        surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
            }
        });
    }

    @Override
    public void handleServerResponse(String response) {
        log.setText(response);
    }
}



