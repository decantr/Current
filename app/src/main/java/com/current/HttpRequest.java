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
    private String re;
    private boolean fi;

    private void readResponse(BufferedReader in) {
        String t = "";
        StringBuilder r = new StringBuilder();

        do {
            try {
                t = in.readLine();
            } catch (IOException ignored) {}

            if (t != null) r.append(t);
        } while (t != null);

        re = r.toString();
    }

    public void sendPostRequest (String w) {
        URL l;
        HttpURLConnection c = null;
        InputStreamReader is;
        BufferedReader in;

        try {
            l = new URL(w);
        } catch (MalformedURLException e) {
            return;
        }

        try {
            c = (HttpURLConnection) l.openConnection();
            is = new InputStreamReader(c.getInputStream(), "UTF-8");
            in = new BufferedReader(is);
            readResponse(in);
        } catch (IOException ignored) {
        } finally {
            c.disconnect();
        }

    }

    String getReturnEntry() {
        if (!fi) return "Hold tight my man!";
        return re;
    }

    public JSONArray getResultAsJSON() {
        JSONArray j = null;

        try {
            if (fi) j = new JSONObject(re).getJSONArray("articles");
        } catch (JSONException e) {
            Log.d ("Output", "Error is " + e.getMessage());
        }
        return j;
    }

    @Override
    protected void onPostExecute (Void result) {
        fi = true;
        Log.d("Output", re);
    }
    @Override
    protected Void doInBackground(String... p) {
        fi = false;
        sendPostRequest(p[0]);
        return null;
    }
}
