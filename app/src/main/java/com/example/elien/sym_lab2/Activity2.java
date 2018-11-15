package com.example.elien.sym_lab2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;
import static java.security.AccessController.getContext;

public class Activity2 extends AppCompatActivity  implements CommunicationEventListenerString{
    private TextView email = null;
    private TextView imei = null;
    private ImageView image = null;
    private boolean firstStart = true;

    EditText textToSend;
    TextView responseText;

    AsyncSendRequest2 sendRequest = new AsyncSendRequest2(Activity2.this);

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private final int REQUEST_ACCESS_NETWORK_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        //TODO
        //prendre le texte passé dans le textedit et le passer dans la variable message
        textToSend = findViewById(R.id.TextToSendActivity2);
        responseText = findViewById(R.id.ResponseTextActivity2);


        Button mClickButtonActivity2 = findViewById(R.id.buttonActivity2);

        mClickButtonActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textToSend.getText().toString();

                if(firstStart) {
                    sendRequest.execute(message);
                    firstStart = false;
                }
                else{
                    sendRequest.newData(message);
                }
            }
        });
    }

    @Override
    public void handleServerResponse(String response) {
        responseText.setText(response);
    }

}

class AsyncSendRequest2 extends AsyncTask<String, Void, String> {

    AsyncSendRequest2(CommunicationEventListenerString l){
        cel = l;
    }

    CommunicationEventListenerString cel = null;

    List<String> messageList = new ArrayList<String>();

    public void newData(String message){
        messageList.add(message);
    }

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
            urlConnection.setRequestProperty("X-Network", "LTE");


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


            cel.handleServerResponse(content.toString());

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}