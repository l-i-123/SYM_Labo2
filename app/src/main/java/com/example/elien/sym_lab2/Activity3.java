package com.example.elien.sym_lab2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity3 extends AppCompatActivity implements CommunicationEventListenerString{

    private EditText name;
    private EditText surname;
    private EditText log;
    private Switch isVegan;
    private RatingBar mark;
    private Button send;


    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3);

        //we link GUI elements
        this.name = findViewById(R.id.name);
        this.surname = findViewById(R.id.surname);
        this.log = findViewById(R.id.Log);
        this.isVegan = findViewById(R.id.isvegan);
        this.mark = findViewById(R.id.mark);
        this.send = findViewById(R.id.send);

        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncSendRequestJSON(Activity3.this).execute(new Data(name.getText().toString(),
                        surname.getText().toString(),isVegan.getShowText(),mark.getNumStars()));
            }
        });
    }

    @Override
    public void handleServerResponse(String response) {
        log.setText(response);
    }
}

class Data implements Serializable {

    private static final long serialVersionUID = -7676157349813018600L;

    private String name;
    private String surname;
    private boolean isvegan;
    private int mark;

    public Data(String name, String surname, boolean isvegan, int mark) {
        this.name = name;
        this.surname = surname;
        this.isvegan = isvegan;
        this.mark = mark;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isIsvegan() {
        return isvegan;
    }
    public void setIsvegan(boolean isvegan) {
        this.isvegan = isvegan;
    }

    public int getMark() {
        return mark;
    }
    public void setMark(int mark) {
        this.mark = mark;
    }
}



class AsyncSendRequestJSON extends AsyncTask<Serializable, Void, String> {

    CommunicationEventListenerString cell = null;

    AsyncSendRequestJSON(CommunicationEventListenerString activity){

        cell = activity;

    }


    protected String doInBackground(Serializable... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        String test;
        Gson gson = new Gson();


        try {
            url = new URL("http://sym.iict.ch/rest/json");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());

            test = gson.toJson(strings[0]);

            writer.write(String.valueOf(test));
            writer.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the ele
            // via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
                System.out.println(line);
            }
            bufferedReader.close();

            if (cell != null){
                cell.handleServerResponse(content.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }


    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Long result) {

    }
}


