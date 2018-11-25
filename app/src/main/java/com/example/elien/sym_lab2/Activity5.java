package com.example.elien.sym_lab2;

/**
 * Created by Maxime Vulliens and Elie N'djoli
 * on 20.11.18
 * Description : Activity 5
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


public class Activity5 extends AppCompatActivity implements CommunicationEventListenerString{
    private EditText nbobject = null;
    private EditText log = null;
    private EditText time = null;
    private Button send = null;
    private CheckBox ckb = null;
    private TextView title = null;

    private  AsyncSendRequestJSON req = null;

    private ArrayList<Data> array = new ArrayList<>();

    private long currentTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity5);

        this.title = findViewById(R.id.titre);
        this.nbobject = findViewById(R.id.nbdata);
        this.log = findViewById(R.id.Log);
        this.send = findViewById(R.id.send);
        this.time = findViewById(R.id.curr_time);
        this.ckb = findViewById(R.id.compressed);

        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count;
                // Get nb object to create
                if (nbobject.getText().toString().equals("")){
                    count = 1;
                }else{
                    count = Integer.parseInt(nbobject.getText().toString());
                }


                // Create the request handler
                req = new AsyncSendRequestJSON(Activity5.this,true);

                // Check if we want to send in deflate mode
                if(ckb.isChecked())
                    req.setDeflateMode(true);

                // Create number of default object
                for (int i = 0; i < count; i++){
                    array.add(new Data("Jean-Jeremy-Benjamin","Von den bergen", true, i % 8));
                }

                // Start timer
                currentTime = Calendar.getInstance().getTime().getTime();

                // Start asynch task
                req.execute(array);

            }
        });
    }

    @Override
    public void handleServerResponse(String respone) {


        time.setText("elapsed time" + ( Calendar.getInstance().getTime().getTime() - currentTime));

        log.setText(respone);

    }
}



