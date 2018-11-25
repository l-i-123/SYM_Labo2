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

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Activity2 extends AppCompatActivity  implements CommunicationEventListenerString{

    EditText textToSend;
    TextView responseText;

    final ArrayList<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        textToSend = findViewById(R.id.TextToSendActivity2);
        responseText = findViewById(R.id.ResponseTextActivity2);



        Button mClickButtonActivity2 = findViewById(R.id.buttonActivity2);

        final boolean[] waitConnection = {false};

        //Envoie du texte lors de l'appui sur le bouton de l'interface graphique
        mClickButtonActivity2.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("debug info", "1");

                //Ajout du nouveau message dans la liste de message
                messages.add(textToSend.getText().toString());

                //Vérification de la disponibilité du réseau
                if(isNetworkAvailable(Activity2.this) && waitConnection[0] == false){
                    //si le réseau est disponible appel de la tâche asynchrone afin d'envoyer le message
                    new AsyncSendRequest2(Activity2.this).execute(messages);
                }
                else if(waitConnection[0] == false){
                    waitConnection[0] = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Contrôle periodique de la disponibilité du réseau jusqu'à se qu'il revienne
                            while(!isNetworkAvailable(Activity2.this)){
                                try {
                                    //Affichage du nombre de message en attente
                                    responseText.setText("Nombre de message en attente : " + messages.size());
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            //Lorsque le réseau est de retour appel de la tâche d'envoie des message
                            new AsyncSendRequest2(Activity2.this).execute(messages);
                            waitConnection[0] = false;
                        }
                    }).start();
                }
            }
        });
    }

    //méthode de vérification de la disponibilité du réseau
    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Lorsque la tâche asychrone donne sa réponse le message ets supprimé et le nombre de message
    //en attente est mis à jour
    @Override
    public void handleServerResponse(String response) {
        messages.remove(0);
        responseText.setText("Nombre de message en attente : " + messages.size());
        Log.d("Lab3", "HandleServerResponse");
    }

}

//Equivalent à AsyncSendRequest de l'activité 1 mais envoie des message tant que la liste messages
//n'est pas vide
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


                cel.handleServerResponse("");
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