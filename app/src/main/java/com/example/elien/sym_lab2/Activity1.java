package com.example.elien.sym_lab2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;



public class Activity1 extends AppCompatActivity implements CommunicationEventListenerString {

    EditText textToSend;
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        textToSend = findViewById(R.id.TextToSend);
        responseText = findViewById(R.id.ResponseText);

        Button mClickButtonActivity1 = findViewById(R.id.buttonActivity1);

        //Envoie du texte lors de l'appuie sur le bouton de l'interface graphique
        mClickButtonActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textToSend.getText().toString();
                new AsyncSendRequest(Activity1.this).execute(message);
            }
        });
    }

    //Retour de la réponse à afficher sur l'interface graphique
    @Override
    public void handleServerResponse(String response) {
        responseText.setText(response);
    }

}

//Tâche asynchrone s'occupant d'envoyer le texte et de lire la réponse retourné par le serveur
class AsyncSendRequest extends AsyncTask<String, Void, String> {

    AsyncSendRequest(CommunicationEventListenerString l){
        cel = l;
    }

    CommunicationEventListenerString cel = null;

    protected String doInBackground(String... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            url = new URL("http://sym.iict.ch/rest/txt");
            urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            InetAddress adresse = InetAddress.getByName("sym.iict.ch");
            Socket socket = new Socket(adresse.getHostAddress(), 80);

            //Vérification de la disponibilité du serveur (Question 4.1)
            if(socket.isConnected()){
                OutputStreamWriter writer = new OutputStreamWriter(
                        urlConnection.getOutputStream());
                writer.write(String.valueOf(strings[0]));
                writer.flush();

                //Analyse de la réponse du serveur pour vérifier qu'il n'y ai pas d'erreur (Question 4.1)
                int responseCode = urlConnection.getResponseCode();
                if (responseCode >= 400 && responseCode <= 499) {
                    content.append("Bad request");
                }
                else {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;

                    // lecture du bufferReader
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        content.append(line + "\n");
                        System.out.println(line);
                    }
                    bufferedReader.close();
                }

            }
            else{
                content.append("Server unavailible\n");
            }

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

}
