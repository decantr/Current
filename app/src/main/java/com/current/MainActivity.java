package com.current;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    SharedPreferences sharedPref;
    private Button btnNext, btnPrev;
    private ImageView img;
    private TextView txtTitle, txtDesc, txtMore;
    private ProgressBar barPage;
    private HttpRequest r;
    private int c;
    private JSONArray j;
    private ConstraintLayout cl;
    private DBUtil db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        ImageButton btnLike = findViewById(R.id.btnLike);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rtn;
                if (save()) {
                    rtn = R.string.saved;
                } else rtn = R.string.nosaved;
                Snackbar.make(view, rtn, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        btnNext = this.findViewById(R.id.btnNext);
        btnPrev = this.findViewById(R.id.btnPrev);
        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        txtMore = this.findViewById(R.id.txtMore);
        barPage = this.findViewById(R.id.barPage);
        img = this.findViewById(R.id.img);
        cl = this.findViewById(R.id.cl);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        cl.setOnClickListener(this);
        txtDesc.setMovementMethod(new ScrollingMovementMethod());
        db = new DBUtil (this);

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
        } else if (id == R.id.action_feed) {
            opnFeed();
        } else if (id == R.id.action_refresh) {
            r = null;
            req();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) loop(true);
        else if (v == btnPrev) loop(false);
        else if (v == cl) more();
    }

    public void opnPref() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void opnFeed() {
        Intent in = new Intent(this, Feed.class);
        startActivity(in);
    }

    public void loop(boolean a) {
        if (j != null) {
            if (a)
                if (c != j.length() - 1) c++;
                else toast("Last Story!");
            else if (c != 0) c--;
            else toast("First Story!");
            barPage.setMax(j.length() - 1);
            barPage.setProgress(c);
            req();
        } else
            toast("Error in URL parsing");
    }

    void toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    public void req() {
        if (r == null) doJson();
        else try {
            j = r.getResultAsJSON();

            if (j == null) {
                toast("error in request!");
                doDisplay(true);
            } else doDisplay(false);

        } catch (JSONException e) {
            Log.e("JSON", "JSON failed to parse" + e.getMessage());
        }
    }

    private void doDisplay(boolean error) throws JSONException {

        String title = "";
        String desc = "";
        String image = "";
        String more = "";

        if (!error) {
            JSONObject t = j.getJSONObject(c);

            title = t.getString("title");
            if (title == null) title = "";

            desc = t.getString("description");
            if (desc == null) desc = "";

            image = t.getString("urlToImage");
            if (image == null) image = "http://via.placeholder.com/350x150";

            if (t.getString("url") == null) more = "";
        }

        txtTitle.setText(title);
        txtDesc.setText(desc);
        txtMore.setText(more);
        Glide.with(this).load(image).into(img);

    }

    private void doJson() {
        String choice = sharedPref.getString("cat_or_source", "");
        String salt = "country=gb&";

        if (choice.equals("source"))
            salt = "sources=" +
                    sharedPref.getString("source", "") + "&";
        else if (choice.equals("category"))
            salt += "category=" +
                    sharedPref.getString("cat", "") + "&";

        c = 0;

        r = new HttpRequest();
        r.execute("https://newsapi.org/v2/top-headlines?" + salt + "apiKey=088fb1a3c9e3440db5b65f2c48c3f705");

        txtTitle.setText(R.string.loadingText);
        txtDesc.setText(R.string.loadingDesc);
    }

    boolean save() {
        if (j != null) {
            try {
                JSONObject a = j.getJSONObject(c);
                db.saveArticle(
                        a.getJSONObject("source").getString("name"),
                        a.getString("title"),
                        a.getString("description"),
                        a.getString("url"),
                        a.getString("urlToImage")
                );
                return true;
            } catch (Exception e) {
                Log.e("meme", e.getMessage());
                return false;
            }
        } else return false;
    }

    void more() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(j.getJSONObject(c).getString("url"))));
        } catch (Exception e) {
            toast("URL not loaded!");
        }
    }

    public class HttpRequest extends AsyncTask<String, Void, Void> {
        public String re;
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
            if (fi && re != null) {
                JSONObject b = new JSONObject(re);

                if (b.getString("status").equals("ok"))
                    return b.getJSONArray("articles");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            fi = true;
            Log.d("Output", "" + re);
            req();
        }

        @Override
        protected Void doInBackground(String... p) {
            fi = false;
            sendPostRequest(p[0]);
            return null;
        }
    }

}
