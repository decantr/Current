package com.current;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by decanter on 09/01/18.
 */

public class HttpRequest extends AsyncTask<String,Void, Void>
{
    private String returnEntry;
    private boolean finished;


    public void sendPostRequest (String where) {
        URL loc = null;
        HttpURLConnection conn = null;
        InputStreamReader is;
        BufferedReader in;

        try {
            loc = new URL(where);
        }
        catch (MalformedURLException ex) {
            return;
        }

        try {
            conn = (HttpURLConnection)loc.openConnection();
            is = new InputStreamReader (conn.getInputStream(), "UTF-8");
            in = new BufferedReader (is);

            readResponse (in);
        }
        catch (IOException ex) {

        }
        finally {
            conn.disconnect();
        }

    }

    public String getReturnEntry() {
        if (!finished) {
            return "Hold tight!";
        }

        return returnEntry;
    }

    public void readResponse(BufferedReader in) {
        String tmp = "";
        StringBuffer response = new StringBuffer();

        do {
            try {
                tmp = in.readLine();
            }
            catch (IOException ex) {

            }

            if (tmp != null) {
                response.append(tmp);
            }
        } while (tmp != null);

        returnEntry = response.toString();
    }

    @Override
    protected void onPostExecute (Void result) {
        finished = true;

        Log.d("Output", returnEntry);

    }
    @Override
    protected Void doInBackground(String... params) {
        finished = false;
        sendPostRequest (params[0]);
        return null;
    }

    public JSONArray getResultAsJSON() {
        JSONObject job;
        JSONArray jar = null;

        if (!finished) return null;

        try {
            job = new JSONObject(returnEntry);
            jar = job.getJSONArray("articles");
        }
        catch (JSONException ex) {
            Log.d ("Output", "Error is " + ex.getMessage());
        }
        return jar;
    }
}
