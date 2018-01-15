package com.current;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPref, btnFeed, btnMore, btnNext, btnPrev;
    private ImageView img;
    private TextView txtTitle, txtDesc;
    private HttpRequest r;
    private int c;
    private JSONArray j;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = this.findViewById(R.id.btnPref);
        btnFeed = this.findViewById(R.id.btnFeed);
        btnMore = this.findViewById(R.id.btnMore);
        btnNext = this.findViewById(R.id.btnNext);
        btnPrev = this.findViewById(R.id.btnPrev);
        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        img = this.findViewById(R.id.img);
        btnPref.setOnClickListener(this);
        btnFeed.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        txtDesc.setMovementMethod(new ScrollingMovementMethod());

        req();
    }

    @Override public void onClick(View v) {
        if (v == btnPref) opnPref();
        else if (v == btnFeed) opnFeed();
        else if (v == btnMore) more();
        else if (v == btnNext) loop(true);
        else if (v == btnPrev) loop(false);
    }

    public void opnPref() {
        Intent in = new Intent(this, SettingsActivity.class);
        startActivity(in);
    }

    public void opnFeed() {
        Intent in = new Intent(this, null);
        startActivity(in);
    }

    public void loop(boolean a) {
        if (a)
            if (c != j.length() - 1) c++;
            else toast("Last Story!");
        else if (c != 0) c--;
        else toast("First Story!");
        req();
    }

    void toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    public void req() {
        if (r == null) {
            r = new HttpRequest();
            r.execute("https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=088fb1a3c9e3440db5b65f2c48c3f705");
            c = 0;
            txtTitle.setText(R.string.loadingText);
        } else try {
            j = r.getResultAsJSON();

            if (j == null) return;

            txtTitle.setText(j.getJSONObject(c).getString("title"));
            txtDesc.setText(j.getJSONObject(c).getString("description"));
            Glide.with(this).load(j.getJSONObject(c).getString("urlToImage")).into(img);
        } catch (JSONException e) {
            Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAAa","AAAAAAAAAAAAAAAAAAAA" + e.getMessage());
        }
    }

    void more() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(j.getJSONObject(c).getString("url"))));
        } catch (Exception e) {
            toast("URL not loaded yet!");
        }
    }

    public class HttpRequest extends AsyncTask<String,Void, Void> {
        private String re;
        private boolean fi;

        private void readResponse(BufferedReader in) {
            String t = "";
            StringBuilder r = new StringBuilder();

            do {
                try {
                    t = in.readLine();
                } catch (IOException ignored) {
                }

                if (t != null) r.append(t);
            } while (t != null);

            re = r.toString();
        }

        void sendPostRequest(String w) {
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

        JSONArray getResultAsJSON() throws JSONException {
            return fi ? new JSONObject(re).getJSONArray("articles") : null;
        }

        @Override protected void onPostExecute(Void result) {
            fi = true;
            Log.d("Output", re);
            req();
        }

        @Override protected Void doInBackground(String... p) {
            fi = false;
            sendPostRequest(p[0]);
            return null;
        }
    }
}
