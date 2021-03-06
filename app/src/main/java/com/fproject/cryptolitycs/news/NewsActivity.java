package com.fproject.cryptolitycs.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fproject.cryptolitycs.AboutActivity;
import com.fproject.cryptolitycs.SettingsActivity;
import com.fproject.cryptolitycs.converter.ConverterActivity;
import com.fproject.cryptolitycs.cryptoapi.CryptoClient;
import com.fproject.cryptolitycs.cryptoapi.CryptoData;
import com.fproject.cryptolitycs.cryptoapi.CryptoNewsArticle;
import com.fproject.cryptolitycs.topCoins.TopCoinsActivity;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.cryptoapi.CryptoCallback;
import com.fproject.cryptolitycs.utility.Settings;
import com.fproject.cryptolitycs.watchlist.WatchListActivity;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // These provide data about the watched items.
    private CryptoClient cryptoClient;

    // List of item displayed in the activity;
    private List<CryptoNewsArticle> cryptoNewsArticles;

    private List<CryptoNewsArticle> newsItems;
    private NewsListAdapter  newsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.getTheme(this));
        setContentView(R.layout.activity_news);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this);
        //
        // Activity
        //
        setupActivity();
        //
        // Listeners
        //
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        // Data
        //
        updateActivity();
    }

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Configure the activity.
     */
    private void setupActivity() {
        newsItems = new ArrayList<>();
        cryptoNewsArticles = new ArrayList<>();
        //
        // Toolbar
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        // recyclerView
        //
        RecyclerView recyclerView = findViewById(R.id.rv_news);
        recyclerView.setHasFixedSize(true);
        //
        // LayoutManager
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        //
        // Adapter
        //
        newsListAdapter = new NewsListAdapter(this, newsItems);
        recyclerView.setAdapter(newsListAdapter);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        //
        // DrawerLayout
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
        //  OnClickListener
        //
        newsListAdapter.setOnClickListener(new NewsListAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                CryptoNewsArticle cryptoNewsArticle = newsListAdapter.getNewsItem(position);
                openWebSite(cryptoNewsArticle.getUrl());
            }
        });
        //
        // SwipeRefreshLayout
        //
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateActivity();
            }
        });
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity() {
        cryptoNewsArticles.clear();
        getCryptoNewsListCallback();
    }

    /**
     * Obtain the {@link CryptoNewsArticle} data.
     */
    private void getCryptoNewsListCallback(){
        cryptoClient.getCryptoNewsArticles(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoNewsArticles = cryptoData.asCryptoNewsArticles();
                onCryptoDataReceived();
            }

            @Override
            public void onFailure(String cryptoError) {
                hideSwipeRefresh();
            }
        });
    }

    /**
     *
     */
    private void onCryptoDataReceived() {
        newsItems.clear();
        newsItems.addAll(cryptoNewsArticles);

        newsListAdapter.notifyDataSetChanged();
        hideSwipeRefresh();
    }

    /**
     * Stop the refresh animation.
     */
    private void hideSwipeRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 1000);
    }

    /**
     * Open a website in a browser.
     */
    private void openWebSite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_coins) {

            switchToActivity(TopCoinsActivity.class);

        } else if (id == R.id.nav_news) {

            // already there

        } else if (id == R.id.nav_watchlist) {

            switchToActivity(WatchListActivity.class);

        } else if (id == R.id.nav_converter) {

            switchToActivity(ConverterActivity.class);

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
