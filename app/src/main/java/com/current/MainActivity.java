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
import org.json.JSONObject;
import org.w3c.dom.NodeList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPref, btnHome, btnFeed;
    HttpRequest task;
    TextView txtOut;
    JSONObject jarr;
//    JSONArray jarr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPref = (Button) this.findViewById(R.id.btnPref);
        btnPref.setOnClickListener(this);
        btnFeed = this.findViewById(R.id.btnFeed);
        btnFeed.setOnClickListener(this);
        btnHome = this.findViewById(R.id.btnHome);
        btnHome.setOnClickListener(this);
        txtOut = this.findViewById(R.id.txtOut);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPref) opnPref();
//        else if (v == btnFeed) opnFeed();
//        else if (v == btnHome) req(v);
        else req(v);
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
        String str;
        NodeList n;

        if (task == null) {
            task = new HttpRequest();
            task.execute("https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=088fb1a3c9e3440db5b65f2c48c3f705");
        }

        if (v == btnHome) {
            str = task.getReturnEntry();

            txtOut.setText(str);
        }
        else if (v == btnFeed) {
            JSONObject tmp;
            try {
                jarr = task.getResultAsJSON();

                if (jarr == null) {
                    txtOut.setText ("Still loading JSON");
                    return;
                }

                str = "";

                for (int i = 0; i < jarr.length(); i++) {
                    tmp = jarr.getJSONObject(i);
                    str += tmp.getString ("name") + "\n";

                }
                txtOut.setText(str);
                txtOut.setMovementMethod(new ScrollingMovementMethod());


            }
            catch (JSONException ex) {
                txtOut.setText("Some horror: " + ex.getMessage());
            }

        }
    }
}
