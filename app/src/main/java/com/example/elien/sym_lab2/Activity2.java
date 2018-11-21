package com.example.elien.sym_lab2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Activity2 extends AppCompatActivity  implements CommunicationEventListenerString{
    private TextView email = null;
    private TextView imei = null;
    private ImageView image = null;


    EditText textToSend;
    TextView responseText;

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private final int REQUEST_ACCESS_NETWORK_STATE = 1;
    final ArrayList<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        textToSend = findViewById(R.id.TextToSendActivity2);
        responseText = findViewById(R.id.ResponseTextActivity2);



        Button mClickButtonActivity2 = findViewById(R.id.buttonActivity2);
        Log.d("debug info", "0");

        final boolean[] waitConnection = {false};

        mClickButtonActivity2.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("debug info", "1");
                messages.add(textToSend.getText().toString());
                if(isNetworkAvailable(Activity2.this) && waitConnection[0] == false){
                    Log.d("debug info", "2");
                    Log.d("Lab3", "add message");
                    Log.d("Lab3", "Taille de message" + messages.size());
                    new AsyncSendRequest2(Activity2.this).execute(messages);
                    //messages.clear();
                }
                else if(waitConnection[0] == false){
                    waitConnection[0] = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(!isNetworkAvailable(Activity2.this)){

                            }
                            new AsyncSendRequest2(Activity2.this).execute(messages);
                            waitConnection[0] = false;
                        }
                    }).start();
                }
            }
        });
    }

    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void handleServerResponse(String response) {
        responseText.setText(response);
        messages.remove(0);
        Log.d("Lab3", "HandleServerResponse");
    }

}

class AsyncSendRequest2 extends AsyncTask<ArrayList<String>, Void, String> {

    AsyncSendRequest2(CommunicationEventListenerString l){
        cel = l;
    }

    CommunicationEventListenerString cel = null;

    protected String doInBackground(ArrayList<String>... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        try {
            int size = strings[0].size();
            for(int i = 0; i < size; i++){
                Log.d("debug info", "3");
                url = new URL("http://sym.iict.ch/rest/txt");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                urlConnection.setRequestProperty("X-Network", "LTE");

                OutputStreamWriter writer = new OutputStreamWriter(
                        urlConnection.getOutputStream());
                writer.write(String.valueOf(strings[0].get(0)));
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