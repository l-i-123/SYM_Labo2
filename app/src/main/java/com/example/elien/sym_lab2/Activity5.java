package com.example.elien.sym_lab2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


public class Activity5 extends AppCompatActivity implements CommunicationEventListenerString{
    private TextView title = null;
    private EditText nbobject = null;
    private EditText log = null;
    private EditText time = null;
    private Button send = null;
    private CheckBox ckb = null;

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

                req = new AsyncSendRequestJSON(Activity5.this,true);

                if(ckb.isChecked())
                    req.setDeflateMode(true);

                for (int i = 0; i < Integer.parseInt(nbobject.getText().toString()); i++){
                    array.add(new Data("Jean-Jeremy-Benjamin","Von den bergen", true, i % 8));
                }

                currentTime = Calendar.getInstance().getTime().getTime();

                req.execute(array);
                //new AsyncSendRequestJSON(Activity3.this,true).execute(new Data(name.getText().toString(),
                //        surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
            }
        });
    }

    @Override
    public void handleServerResponse(String response) {

        time.setText("elapsed time" + ( Calendar.getInstance().getTime().getTime() - currentTime));
        log.setText("Taille uncompressed / compressed : " + response);

    }
}



