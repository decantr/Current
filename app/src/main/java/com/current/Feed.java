package com.current;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private int c;
    private DBUtil db;
    private ArrayList a;
    private Button btnNext, btnPrev;
    private ImageView img;
    private TextView txtTitle, txtDesc, txtMore;
    private ProgressBar barPage;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtTitle = this.findViewById(R.id.txtTitle);
        txtDesc = this.findViewById(R.id.txtDesc);
        txtMore = this.findViewById(R.id.txtMore);

        img = this.findViewById(R.id.img);

        cl = this.findViewById(R.id.cl);

        btnNext = this.findViewById(R.id.btnNext);
        btnPrev = this.findViewById(R.id.btnPrev);

        db = new DBUtil(this);

        a = db.getArticles();

        doDisplay();
    }

    public void loop(boolean t) {
        if (t)
            if (c != a.size() - 1) c++;
            else toast("Last Story!");
        else if (c != 0) c--;
        else toast("First Story!");
        barPage.setMax(a.size() - 1);
        barPage.setProgress(c);
    }

    void toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    private void doDisplay() {
        String[] b = (String[]) a.get(c);

        String title = "";
        String desc = "";
        String image = "http://via.placeholder.com/350x150";
        String more = "";

        if (b[1] != null) title = b[1];

        if (b[2] != null) desc = b[2];

        if (b[3] != null) more = "Tap to read more";

        if (b[4] != null) image = b[4];

        txtTitle.setText(title);
        txtDesc.setText(desc);
        txtMore.setText(more);
        Glide.with(this).load(image).into(img);
    }

}
