package com.current;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPref, btnHome, btnFeed;
    HttpRequest r;
    TextView txtOut;
    JSONArray j;
    int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = this.findViewById(R.id.btnPref);
        btnHome = this.findViewById(R.id.btnHome);
        btnFeed = this.findViewById(R.id.btnFeed);
        txtOut = this.findViewById(R.id.txtOut);
        btnPref.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnFeed.setOnClickListener(this);
        txtOut.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        if (v == btnPref) opnPref();
        else if (v == btnFeed) opnFeed();
        else if (v == btnHome) req(v);
    }

    public void opnPref() {
        Intent in = new Intent(this, SettingsActivity.class);
        startActivity(in);
    }

    public void opnFeed() {
        Intent in = new Intent(this, null);
        startActivity(in);
    }

    public void req(View v) {
        if (r == null) {
            r = new HttpRequest();
            r.execute("https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=088fb1a3c9e3440db5b65f2c48c3f705");
            c = 0;
        }

        c++;

        String[] s;

        try {
            j = r.getResultAsJSON();
            s = new String[1000];

            txtOut.setText(R.string.loadingText);

            if (j == null) return;

            for (int i = 0; i < j.length(); i++)
                s[i] = j.getJSONObject(i).toString();

            txtOut.setText(s[c]);
        } catch (JSONException ignored) {
        }

    }
}
