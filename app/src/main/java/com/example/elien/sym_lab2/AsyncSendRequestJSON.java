package com.example.elien.sym_lab2;


import android.os.AsyncTask;
import android.util.Xml;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
