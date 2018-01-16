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
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //    vars for class
    private int c;
    private JSONArray j;
    private DBUtil db;
    private JSONObject curDisplay;
    private SharedPreferences sharedPref;
    private HttpRequest r;

    //    ui elements
    private Button btnNext, btnPrev;
    private TextView txtTitle, txtDesc, txtMore;
    private ProgressBar barPage;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        boiler plate
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        PreferenceManager.setDefaultValues( this, R.xml.pref_main, false );
        sharedPref = PreferenceManager.getDefaultSharedPreferences( this );

//        database
        db = new DBUtil( this );

//        buttons
        btnNext = this.findViewById( R.id.btnNext );
        btnNext.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loop( true );
                doDisplay();
            }
        } );
        btnPrev = this.findViewById( R.id.btnPrev );
        btnPrev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loop( false );
                doDisplay();
            }
        } );
        ImageButton btnLike = this.findViewById( R.id.btnLike );
        btnLike.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rtn;
                if ( save() ) {
                    rtn = R.string.saved;
                } else rtn = R.string.nosaved;
                Snackbar.make( view, rtn, Snackbar.LENGTH_SHORT )
                        .setAction( "Action", null ).show();
            }
        } );

//        clickable body
        ConstraintLayout cl = this.findViewById( R.id.cl );
        cl.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity( new Intent( Intent.ACTION_VIEW,
                            Uri.parse( curDisplay.getString( "url" ) ) ) );
                } catch ( Exception e ) {
                    toast( "No URL for this article" );
                }
            }
        } );

//        text views
        txtTitle = this.findViewById( R.id.txtTitle );
        txtDesc = this.findViewById( R.id.txtDesc );
        txtMore = this.findViewById( R.id.txtMore );
//        paging bar
        barPage = this.findViewById( R.id.barPage );
//        image view
        img = this.findViewById( R.id.img );

//        request json
        req();
    }

    /*
        ensure that c, our article counter, is always between zero
        and the amount of articles we got back from the api
        @t indicates where we want to increase or decrease the counter
    */
    public void loop(boolean t) {

        if ( t && c != j.length() - 1 ) c++;
        else if ( c != 0 ) c--;

        btnSwtch();
    }

    /*
        turn off the correct button when we reach max or hit zero
    */
    private void btnSwtch() {

        btnPrev.setEnabled( true );
        btnPrev.setEnabled( true );

        if ( c == 0 )
            btnPrev.setEnabled( false );
        if ( c == j.length() - 1 )
            btnNext.setEnabled( false );

    }

    /*
        helper method to display the text passed in
    */
    void toast(String t) {
        Toast.makeText( this, t, Toast.LENGTH_SHORT ).show();
    }

    /*
        if there isn't current a response loaded, then run doJson() which will get
        request our json file, if there is then attempt to parse it,
        if parsing fails reset the response and display and error
        if parsing doesn't fail update the display
    */
    public void req() {

        if ( r == null ) doJson();

        else try {
            j = r.getResultAsJSON();

            if ( j == null ) {
                toast( "Error In Request!" );
                return;
            }

            doDisplay();

        } catch ( Exception ignored ) {
        }

    }

    /*
        handle displaying the article to the main page
        first creates all default values then creates t inside
        a try catch incase of failure.
        if any values are returned as null from the API then they
        are left blank.
    */
    private void doDisplay() {

        String title = "";
        String desc = "";
        String image = "http://via.placeholder.com/350x150";
        String more = "";
        int max = 1;

        try {
            curDisplay = j.getJSONObject( c );

            if ( curDisplay.getString( "title" ) != null )
                title = curDisplay.getString( "title" );

            if ( curDisplay.getString( "description" ) != null )
                desc = curDisplay.getString( "description" );

            if ( curDisplay.getString( "url" ) != null )
                more = "Tap to read more";

            if ( curDisplay.getString( "urlToImage" ) != null )
                image = curDisplay.getString( "urlToImage" );

            if ( j.length() > 1 )
                max = j.length() - 1;

        } catch ( Exception ignored ) {
        }

        barPage.setMax( max );
        barPage.setProgress( c );

        txtTitle.setText( title );
        txtDesc.setText( desc );
        txtMore.setText( more );

        Glide.with( this ).load( image ).into( img );

    }

    /*
        handle our json request
        choice indicates whether we are asking the api for categories or source
            which we retrieve form the settings menu
        salt indicates what we will be adding to the request url
    */
    private void doJson() {
        String choice = sharedPref.getString( "cat_or_source", "" );
        String salt = "country=gb&";

        if ( choice.equals( "source" ) )
            salt = "sources=" +
                   sharedPref.getString( "source", "" ) + "&";
        else if ( choice.equals( "category" ) )
            salt += "category=" +
                    sharedPref.getString( "cat", "" ) + "&";

        c = 0;

        r = new HttpRequest();
        r.execute( "https://newsapi.org/v2/top-headlines?" + salt
                   + "apiKey=088fb1a3c9e3440db5b65f2c48c3f705" );

        txtTitle.setText( R.string.loadingText );
        txtDesc.setText( R.string.loadingDesc );
    }

    /*
        handles saving current article to the db
    */
    boolean save() {
        if ( j != null ) {
            try {
                return db.saveArticle(
                        curDisplay.getJSONObject( "source" ).getString( "name" ),
                        curDisplay.getString( "title" ),
                        curDisplay.getString( "description" ),
                        curDisplay.getString( "url" ),
                        curDisplay.getString( "urlToImage" )
                ) > 0;
            } catch ( Exception e ) {
                return false;
            }
        } else return false;
    }

    /*
        handles deleting the current article from the db
            if it's saved
    */
    boolean delete() {

        try {
            if ( db.deleteArticle( curDisplay.getString( "description" ) ) == 1 )
                return true;
        } catch ( Exception ignored ) {
        }

        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            startActivity( new Intent( this, SettingsActivity.class ) );
        } else if ( id == R.id.action_feed ) {
            startActivity( new Intent( this, Feed.class ) );
        } else if ( id == R.id.action_refresh ) {
            r = null;
            req();
        }

        return super.onOptionsItemSelected( item );
    }

    /*
        HttpRequest to handle the async http get
        this class is inside our main so i can easlily callback the req method
    */
    public class HttpRequest extends AsyncTask <String, Void, Void> {

        private String re;
        private boolean fi;

        /*
            read the response in
        */
        private void readResponse(BufferedReader in) {

            String t = "";
            StringBuilder r = new StringBuilder();

            do {

                try {
                    t = in.readLine();
                    r.append( t );
                } catch ( IOException ignored ) {
                }

            } while (t != null);

            re = r.toString();

        }

        /*
            send our http get request
        */
        void sendPostRequest(String w) {

            HttpURLConnection c = null;
            InputStreamReader is;
            BufferedReader in;

            try {

                c = (HttpURLConnection) new URL( w ).openConnection();
                is = new InputStreamReader( c.getInputStream(), "UTF-8" );
                in = new BufferedReader( is );
                readResponse( in );

            } catch ( IOException ignored ) {
            } finally {
                if (c != null) c.disconnect();
            }

        }

        /*
            convert our response to a json array
        */
        JSONArray getResultAsJSON() throws JSONException {

            if ( fi && re != null ) {

                JSONObject b = new JSONObject( re );

                if ( b.getString( "status" ).equals( "ok" ) )
                    return b.getJSONArray( "articles" );

            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            fi = true;
            req();
        }

        @Override
        protected Void doInBackground(String... p) {
            fi = false;
            sendPostRequest( p[0] );
            return null;
        }
    }

}
