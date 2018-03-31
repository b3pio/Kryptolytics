package com.fproject.cryptolytics.topCoins;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fproject.cryptolytics.AboutActivity;
import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.converter.ConverterActivity;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.details.DetailsActivity;
import com.fproject.cryptolytics.searchCurrency.SearchCurrencyActivity;
import com.fproject.cryptolytics.watchlist.WatchListActivity;
import com.fproject.cryptolytics.watchlist.WatchedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopCoinsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int TOP_COINS_COUNT = 20;
    private final static int SEARCH_CURRENCY_REQUEST  = 2;

    // These provide data about the watched items.
    private CryptoClient cryptoClient   = null;

    // List of items that are being watched.
    private List<TopCoin>   topCoins        = new ArrayList<>();
    private TopCoinsAdapter topCoinsAdapter = null;

    private List<CryptoCoin>            cryptoCoins      = null;
    private Map<Integer,CryptoCurrency> cryptoCurrencies = null;

    // The currency to which to convert the coins.
    private String  toSymbol  = "EUR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_coins);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        //
        //
        cryptoClient = new CryptoClient(this);
        //
        // Component
        //
        setupComponents();
        //
        // Listeners
        //
        setupListeners();
        //
        //
        //
        updateActivity();
    }

    /**
     * Setup the components of the activity.
     */
    private void setupComponents(){
        RecyclerView recyclerView = findViewById(R.id.rv_top_coins);
        recyclerView.setHasFixedSize(true);
        //
        // LayoutManager
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Disable auto measure, otherwise the item will not size correctly.
        layoutManager.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        //
        // ItemDecoration
        //
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        //
        // Adapter
        //
        topCoinsAdapter = new TopCoinsAdapter(this, topCoins);
        recyclerView.setAdapter(topCoinsAdapter);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners() {
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
        // Navigation View
        //
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //
        // Adapter
        //
        topCoinsAdapter.setOnClickListener(
                (view, position) -> { openDetailsActivity(topCoinsAdapter.getItem(position));}
        );
        //
        // SwipeRefreshLayout
        //
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    updateActivity();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity(){
        topCoins.clear();
        topCoinsAdapter.notifyDataSetChanged();

        cryptoCoins      = new ArrayList<>();
        cryptoCurrencies = new HashMap<>();

        getCryptoCoinsCallback();
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinsCallback(){
        cryptoClient.getTopCrytpoCoins(TOP_COINS_COUNT, toSymbol, new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {

                cryptoCoins = cryptoData.getAsTopCoins();
                getCryptoCurrenciesCallback();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    /**
     * Obtain the {@link CryptoCurrency} data.
     */
    private void getCryptoCurrenciesCallback() {
        for(CryptoCoin cryptoCoin:cryptoCoins) {
            cryptoClient.getCrytpoCurrency(cryptoCoin.getSymbol(), toSymbol, new CryptoCallback() {
                @Override
                public void onSuccess(CryptoData cryptoData) {

                    cryptoCurrencies.put(cryptoCoin.getSortOrder(), cryptoData.getAsCryptoCurrency());
                    onCryptoCurrencyReceived();
                }

                @Override
                public void onFailure(String cryptoError) {

                }
            });
        }
    }

    /**
     * Determines weather all the requested data has arrived and updates the WatchList.
     */
    private void onCryptoCurrencyReceived(){
        if (cryptoCurrencies.size() != cryptoCoins.size())
            return;

        for(CryptoCoin cryptoCoin:cryptoCoins) {
            int sortOrder   = cryptoCoin.getSortOrder();
            TopCoin topCoin = new TopCoin(sortOrder, cryptoCoin.getSymbol(), toSymbol);

            topCoin.setCryptoCoin(cryptoCoin);
            topCoin.setCryptoCurrency(cryptoCurrencies.get(sortOrder));

            topCoins.add(topCoin);
        }

        topCoinsAdapter.notifyDataSetChanged();
    }

    /**
     * Open Activity where the user can select the ToSymbol.
     */
    public void openSearchCurrencyActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchCurrencyActivity.class);
        intent.putExtra("showBackButton", true);
        startActivityForResult(intent, SEARCH_CURRENCY_REQUEST);
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        menuItem.setTitle(toSymbol);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSearchCurrencyActivity();
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

            // already there

        } else if (id == R.id.nav_watchlist) {

            Intent intent = new Intent(this, WatchListActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_converter) {

            Intent intent = new Intent(this, ConverterActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(this, AboutActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == SEARCH_CURRENCY_REQUEST) && resultCode == (RESULT_OK)){
            String currencyName = data.getStringExtra("CurrencyName");

            if (currencyName != null) {
                toSymbol = currencyName;

                invalidateOptionsMenu();
                updateActivity();
            }
        }
    }

    /**
     * Open activity with item details.
     */
    public void openDetailsActivity(TopCoin topCoin) {

        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);

        intent.putExtra("fromSymbol", topCoin.getFromSymbol());
        intent.putExtra("toSymbol", topCoin.getToSymbol());

        startActivityForResult(intent, 3);
    }
}
