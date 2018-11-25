package com.example.elien.sym_lab2;

/**
 * Created by Maxime Vulliens and Elie N'djoli
 * on 20.11.18
 * Description : Activity 4
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Activity4 extends AppCompatActivity implements CommunicationEventListenerString{

    String stringResponse = "";
    Gson gson = new Gson();
    ListView listView = null;
    TextView textView = null;
    ScrollView scrollView = null;

    final ArrayList<String> name_list = new ArrayList<String>();
    private int getCategoryPos(String category) {
        return name_list.indexOf(category);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4);

        listView = findViewById(R.id.listViewActivity4);
        textView = findViewById(R.id.textViewActivity4);
        textView.setMovementMethod(new ScrollingMovementMethod());

        //Envoie de la requete demandant la liste des id, nom et prénom de tous les auteurs
        String message = "{\"query\":\"{allAuthors{id first_name last_name}}\"}";
        new AsyncSendRequest4(Activity4.this).execute(message);

        //Attente d'une réponse
        while(stringResponse == "");

        //Converion du json reçu vers la class DataAuthors
        DataAuthors authors = gson.fromJson(stringResponse, DataAuthors.class);

        //Extraction de l'objet authors des prénom et nom de chaque auteur pour le mettre dans la
        //list "name_lit"
        for (int i = 0; i < authors.data.allAuthors.length; ++i) {
            AllAuthors temp = authors.data.allAuthors[i];
            name_list.add(temp.first_name + " " + temp.last_name);
        }

        //Ajout des données au list view
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, name_list);
        listView.setAdapter(adapter);

        //lors de l'appuie sur un auteur se trouvant dans le liste view l'ensemble de ses ouvrages est chargé
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                stringResponse = "";

                //Requête de récupération des publication d'un auteur via son id
                String message = "{\"query\":\"{author(id: " + (getCategoryPos(item) + 1) + "){posts{title content}}}\"}";
                new AsyncSendRequest4(Activity4.this).execute(message);
                while(stringResponse == ""){
                    Log.d("Log Info","wait response");
                };

                //Conversion du json contenu dans stringResponse en object de type DataContent
                DataContent content = gson.fromJson(stringResponse, DataContent.class);

                //Contruction de la réponse
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < content.data.author.posts.length; ++i) {
                    Post temp = content.data.author.posts[i];
                    builder.append(temp.title + ":\n");
                    builder.append(temp.content + "\n\n");
                }

                //Affichage des publication sur l'interface graphique
                textView.setText(builder.toString());

                view.setAlpha(1);

            }
        });
    }
    @Override
    public void handleServerResponse(String response) {
        stringResponse = response;
    }
}

//Tâche asynchrone similaire à celle de l'activité 1
class AsyncSendRequest4 extends AsyncTask<String, Void, String> {

    AsyncSendRequest4(CommunicationEventListenerString l){
        cel = l;
    }

    CommunicationEventListenerString cel = null;

    protected String doInBackground(String... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        try {
            url = new URL("http://sym.iict.ch/api/graphql");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());
            writer.write(String.valueOf(strings[0]));
            writer.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

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

}


//Ensemble de classe permettant de convertire le Json reçu en objet

class AllAuthors{
    String id;
    String first_name;
    String last_name;
}

class Post{
    String title;
    String content;
}

class DataAct4{
    AllAuthors[] allAuthors;
}

class DataAuthors{
    DataAct4 data;
}

class DataContent{
    DataAuthorPost data;
}

class DataAuthorPost{
    AuthorPost author;
}

class AuthorPost{
    Post posts[];
}