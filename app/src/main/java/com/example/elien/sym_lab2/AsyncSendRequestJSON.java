package com.example.elien.sym_lab2;


import android.os.AsyncTask;
import android.util.Xml;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class AsyncSendRequestJSON extends AsyncTask<Serializable, Void, String> {

    CommunicationEventListenerString cell = null;
    boolean sendJSON;
    boolean deflateMode;  // Used only with JSON

    AsyncSendRequestJSON(CommunicationEventListenerString activity,boolean sendJSON){

        cell = activity;
        this.sendJSON = sendJSON;
        this.deflateMode = false;

    }


    public void setDeflateMode(boolean deflateMode) {
        this.deflateMode = deflateMode;
    }

    protected String doInBackground(Serializable... strings) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder content = new StringBuilder();
        OutputStreamWriter writer;
        String test = "";

        Gson gson = new Gson();

        try {

            if (deflateMode && sendJSON){


                url = new URL("http://sym.iict.ch/rest/json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setRequestProperty("X-Content-Encoding","deflate");
                urlConnection.setRequestProperty("X-Network","CSD");

                for (Serializable str : strings){
                    test += gson.toJson(str);
                }

                // Compress the bytes
                DeflaterOutputStream outputStream = new DeflaterOutputStream(urlConnection.getOutputStream(),
                        new Deflater(9,true));
                outputStream.write(test.getBytes(), 0, test.getBytes().length);
                outputStream.close();

                //Read and decompress the data
                byte[] readBuffer = new byte[5000];
                InflaterInputStream inputStream = new InflaterInputStream(urlConnection.getInputStream(),
                        new Inflater(true));
                int read = inputStream.read(readBuffer);

                byte[] result = Arrays.copyOf(readBuffer, read);

                // Decode the bytes into a String
                String ans = new String(result, "UTF-8");

                //
                if (cell != null){
                    cell.handleServerResponse(test.getBytes().length + "/" + read);
                }

                outputStream.close();
                inputStream.close();


            }else{

                if (sendJSON){

                    url = new URL("http://sym.iict.ch/rest/json");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());

                    for (Serializable str : strings){
                        test += gson.toJson(str);
                    }

                }else{
                    url = new URL("http://sym.iict.ch/rest/xml");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");

                    writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());

                    for (Serializable str : strings){
                        test += writeXml(str);
                    }

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
