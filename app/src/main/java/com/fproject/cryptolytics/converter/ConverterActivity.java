package com.fproject.cryptolytics.converter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.fproject.cryptolytics.AboutActivity;
import com.fproject.cryptolytics.HomeActivity;
import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.database.DatabaseManager;
import com.fproject.cryptolytics.watchlist.WatchListActivity;

import java.util.ArrayList;
import java.util.List;

public class ConverterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // These provide data about the watched items.
    private CryptoClient cryptoClient       = null;
    private DatabaseManager databaseManager = null;

    // List of converter items.
    private List<ConverterItem> converterItems = null;

    private ConverterItem       newConverterItem    = null;
    private ConvertListAdapter  convertListAdapter  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this);
        databaseManager = new DatabaseManager(this);
        //
        //
        //
        setupListeners();
        //
        //
        updateActivity();;
    }

    private void initDummyData(){
        converterItems = new ArrayList<>();
        converterItems.add(new ConverterItem(0,"BTC", "100"));
        converterItems.add(new ConverterItem(1,"ETH", "100"));
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity(){
        initDummyData();
        convertListAdapter = new ConvertListAdapter(this, converterItems);
        ListView listView = (ListView) findViewById(R.id.lv_converter);
        listView.setAdapter(convertListAdapter);

        getCryptoCurrenciesCallback();
    }

    /**
     * Obtain the {@link CryptoCurrency} data.
     */
    private void getCryptoCurrenciesCallback() {
        for(ConverterItem item: converterItems) {
            cryptoClient.getCrytpoCurrency(item.getSymbol(), "EUR", new CryptoCallback() {
                @Override
                public void onSuccess(CryptoData cryptoData) {
                    item.setCryptoCurrency(cryptoData.getAsCryptoCurrency());

                }

                @Override
                public void onFailure(String cryptoError) {

                }
            });
        }
    }

    private void setupListeners(){
        //
        // DrawerLayout
        //
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
        // NavigationView
        //
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_watchlist) {

            Intent intent = new Intent(this, WatchListActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_converter) {

            // already there

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(this, AboutActivity.class);
            finish();
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
