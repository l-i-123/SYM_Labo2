package com.example.elien.sym_lab2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity3 extends AppCompatActivity implements CommunicationEventListenerString{

    private EditText name;
    private EditText surname;
    private EditText log;
    private Switch isMajor;
    private RatingBar mark;
    private Button send;

    private Switch sendJSON;


    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

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
                    new AsyncSendRequestJSON(Activity3.this,true).execute(new Data(name.getText().toString(),
                            surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
                else
                    new AsyncSendRequestJSON(Activity3.this,false).execute(new Data(name.getText().toString(),
                            surname.getText().toString(),isMajor.isChecked(),mark.getRating()));
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
    private boolean ismajor;
    private float mark;

    public Data(String name, String surname, boolean ismajor, float mark) {
        this.name = name;
        this.surname = surname;
        this.ismajor = ismajor;
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

    public boolean getIsmajor() {
        return ismajor;
    }
    public void setIsmajor(boolean isvegan) {
        this.ismajor = isvegan;
    }

    public float getMark() {
        return mark;
    }
    public void setMark(float mark) {
        this.mark = mark;
    }
}



class AsyncSendRequestJSON extends AsyncTask<Serializable, Void, String> {

    CommunicationEventListenerString cell = null;
    boolean sendJSON;

    AsyncSendRequestJSON(CommunicationEventListenerString activity,boolean sendJSON){

        cell = activity;
        this.sendJSON = sendJSON;

    }


    protected String doInBackground(Serializable... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        String test = "";

        Gson gson = new Gson();

        try {
            if (sendJSON)
                url = new URL("http://sym.iict.ch/rest/json");
            else
                url = new URL("http://sym.iict.ch/rest/xml");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            if (sendJSON)
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            else
                urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");

            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());

            if (sendJSON)
                test = gson.toJson(strings[0]);
            else{
                test = writeXml(strings[0]);
            }


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

    private String writeXml(Serializable message){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        Data msg = (Data) message;
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", null);
            serializer.docdecl(" directory SYSTEM \"http://sym.iict.ch/directory.dtd\"");

            serializer.startTag("", "directory");
            serializer.startTag("", "person");

            serializer.startTag("", "name");
            serializer.text(msg.getName());
            serializer.endTag("", "name");

            serializer.startTag("", "firstname");
            serializer.text(msg.getSurname());
            serializer.endTag("", "firstname");

            serializer.startTag("", "gender");
            if(msg.getIsmajor())
                serializer.text("Male");
            else
                serializer.text("Female");

            serializer.endTag("", "gender");

            serializer.startTag("", "phone");
            serializer.attribute("","type","home");
            serializer.text("+41 09876543");
            serializer.endTag("", "phone");

            serializer.endTag("", "person");
            serializer.endTag("", "directory");
            serializer.endDocument();

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Long result) {

    }
}


