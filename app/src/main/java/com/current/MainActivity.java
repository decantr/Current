package com.current;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    private Button btnNext, btnPrev;
    private ImageView img;
    private TextView txtTitle, txtDesc;
    private ProgressBar barPage;
    private HttpRequest r;
    private int c;
    private JSONArray j;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnLike = findViewById(R.id.btnLike);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.saved, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        btnNext = this.findViewById(R.id.btnNext);
        btnPrev = this.findViewById(R.id.btnPrev);
        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        barPage = this.findViewById(R.id.barPage);
        img = this.findViewById(R.id.img);
        cl = this.findViewById(R.id.cl);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        cl.setOnClickListener(this);
        txtDesc.setMovementMethod(new ScrollingMovementMethod());

        req();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            opnPref();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override public void onClick(View v) {
        if (v == btnNext) loop(true);
        else if (v == btnPrev) loop(false);
        else if (v == cl) more();
    }

    public void opnPref() {
        startActivity(new Intent(this, Preferences.class));
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
        barPage.setMax(j.length() - 1);
        barPage.setProgress(c);
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
            txtDesc.setText(R.string.loadingDesc);
        } else try {
            j = r.getResultAsJSON();

            if (j == null) return;

            txtTitle.setText(j.getJSONObject(c).getString("title"));
            txtDesc.setText(j.getJSONObject(c).getString("description"));
            Glide.with(this).load(j.getJSONObject(c).getString("urlToImage")).into(img);
        } catch (JSONException e) {
            Log.e("JSON","JSON failed to parse" + e.getMessage());
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
