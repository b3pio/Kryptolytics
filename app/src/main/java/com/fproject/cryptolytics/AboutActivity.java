package com.fproject.cryptolytics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.fproject.cryptolytics.converter.ConverterActivity;
import com.fproject.cryptolytics.topCoins.TopCoinsActivity;
import com.fproject.cryptolytics.watchlist.WatchListActivity;

public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MP_CHART_URL        = "https://github.com/PhilJay/MPAndroidChart";
    private static final String CRYPTO_COMPARE_URL  = "https://www.cryptocompare.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //
        // Activity
        //
        setupActivity();
        //
        // Listeners
        //
        setupListeners();
    }

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Configure the activity.
     */
    private void setupActivity(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        //
        // Drawer
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
        // NavigationView
        //
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //
        // MP Android Chart
        //
        LinearLayout llMpChart = findViewById(R.id.ll_mp_chart);
        llMpChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebSite(MP_CHART_URL);
            }
        });
        //
        // Crypto Compare
        //
        LinearLayout llCryptoCompare = findViewById(R.id.ll_crypto_compare);
        llCryptoCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebSite(CRYPTO_COMPARE_URL);
            }
        });
        //
        // FeedBack
        //
        LinearLayout llFeedback = findViewById(R.id.ll_feedback);
        llFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeedbackMail();
            }
        });
    }

    /**
     * Open a website in a browser.
     */
    private void openWebSite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void openFeedbackMail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","levente.szathmary@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CryptoCompare - FeedBack");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Override Methods
    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_home) {

            Intent intent = new Intent(this, TopCoinsActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_watchlist) {

            Intent intent = new Intent(this, WatchListActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_converter) {

            Intent intent = new Intent(this, ConverterActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            // already there

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
