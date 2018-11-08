package com.example.elien.sym_lab2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Activity1 extends AppCompatActivity {
    private TextView email = null;
    private TextView imei = null;
    private ImageView image = null;

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    EditText textToSend;
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        //TODO
        //prendre le texte passé dans le textedit et le passer dans la variable message
        textToSend = findViewById(R.id.TextToSend);
        responseText = findViewById(R.id.ResponseText);


        final String message = "";
        Button mClickButtonActivity1 = findViewById(R.id.buttonActivity1);

        mClickButtonActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncSendRequest().execute(message);
            }
        });
    }

    void setCommunicationEventListener(CommunicationEventListener l){
        //Todo
        //cette methode sera appelé lorsqu'asychsendRequest aura recu une réponse


    }

}

class AsyncSendRequest extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        try {
            url = new URL("http://sym.iict.ch/rest/txt");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());
            writer.write(String.valueOf(strings[0]));
            writer.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {

                content.append(line + "\n");
                System.out.println(line);
            }
            bufferedReader.close();



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


