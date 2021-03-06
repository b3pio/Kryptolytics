package com.fproject.cryptolitycs.converter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fproject.cryptolitycs.SettingsActivity;
import com.fproject.cryptolitycs.topCoins.TopCoinsActivity;
import com.fproject.cryptolitycs.AboutActivity;
import com.fproject.cryptolitycs.news.NewsActivity;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.utility.Settings;
import com.fproject.cryptolitycs.watchlist.WatchListActivity;

public class ConverterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConverterListFragment.OnConvertItemSelectionListener,
        KeyboardFragment.OnTextChangedListener {

    // Meet our child fragments
    private KeyboardFragment    keyboardFragment    = null;
    private ConverterListFragment converterListFragment = null;

    // The item that was clicked in the convert list fragment.
    private ConverterItem selectedItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.getTheme(this));
        setContentView(R.layout.activity_converter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        //
        //
        keyboardFragment = (KeyboardFragment) getSupportFragmentManager().findFragmentById(R.id.fgt_keyboard);
        converterListFragment = (ConverterListFragment) getSupportFragmentManager().findFragmentById(R.id.fgt_convertlist);
        //
        // Listeners
        //
        setupListeners();
    }

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        //
        // DrawerLayout
        //
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
        // NavigationView
        //
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Public Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Hooked up to the {@link ConverterListFragment}.
     */
    @Override
    public void onConvertItemSelected(ConverterItem converterItem){

        selectedItem = converterItem;
        keyboardFragment.setText(converterItem.getValue());
    }

    /**
     * Hooked up to the {@link KeyboardFragment}.
     */
    @Override
    public void onTextChanged(String str) {
        if (selectedItem == null)
            return;

        selectedItem.setValue(str);
        converterListFragment.valueChanged();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Override Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // These will be handled by the child fragment.
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_coins) {

            switchToActivity(TopCoinsActivity.class);

        } else if (id == R.id.nav_news) {

            switchToActivity(NewsActivity.class);

        } else if (id == R.id.nav_watchlist) {

            switchToActivity(WatchListActivity.class);

        } else if (id == R.id.nav_converter) {

            // already there

        } else if (id == R.id.nav_settings) {

            switchToActivity(SettingsActivity.class);

        } else if (id == R.id.nav_about) {

            switchToActivity(AboutActivity.class);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Close the current activity and open an activity of the specified type.
     */
    private void switchToActivity(Class activity) {

        Intent intent = new Intent(this, activity);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
