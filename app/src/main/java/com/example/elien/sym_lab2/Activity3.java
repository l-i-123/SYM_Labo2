package com.example.elien.sym_lab2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;

public class Activity3 extends AppCompatActivity implements CommunicationEventListenerString{

    private EditText name;
    private EditText surname;
    private EditText log;
    private Switch isMajor;
    private RatingBar mark;
    private Button send;

    private Switch sendJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3);

        //we link GUI elements
        this.name = findViewById(R.id.name);
        this.surname = findViewById(R.id.surname);
        this.log = findViewById(R.id.Log);
        this.isMajor = findViewById(R.id.ismajor);
        this.mark = findViewById(R.id.mark);
        this.send = findViewById(R.id.send);
        this.sendJSON = findViewById(R.id.sendjson);

        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendJSON.isChecked())
                    // Send request with JSON
                    new AsyncSendRequestJSON(Activity3.this,true).execute(new Data(name.getText().toString(),
                            surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
                else
                    // Send request with XML
                    new AsyncSendRequestJSON(Activity3.this,false).execute(new Data(name.getText().toString(),
                            surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
            }
        });
    }


    @Override
    public void handleServerResponse(String respone) {

        log.setText(respone);

    }
}

