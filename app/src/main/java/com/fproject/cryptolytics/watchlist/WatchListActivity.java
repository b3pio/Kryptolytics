package com.fproject.cryptolytics.watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fproject.cryptolytics.AboutActivity;
import com.fproject.cryptolytics.ConverterActivity;
import com.fproject.cryptolytics.HomeActivity;
import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.database.DatabaseManager;
import com.fproject.cryptolytics.details.DetailsActivity;
import com.fproject.cryptolytics.searchCoin.SearchCoinActivity;
import com.fproject.cryptolytics.searchCurrency.SearchCurrencyActivity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WatchListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int SEARCH_COIN_REQUEST      = 1;
    private final static int SEARCH_CURRENCY_REQUEST  = 2;

    // These provide data about the watched items.
    private CryptoClient     cryptoClient   = null;
    private DatabaseManager  databaseManager = null;

    // List of items that are being watched.
    private List<WatchedItem>       watchedItems     = null;

    private Map<String,CryptoCoin>    cryptoCoins      = null;
    private Map<Long,CryptoCurrency>  cryptoCurrencies = null;

    private WatchedItem       newItem          = null;
    private WatchListAdapter  watchListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        //
        // Toolbar
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this);
        databaseManager = new DatabaseManager(this);
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
     * Hook up the event listeners.
     */
    private void setupListeners(){
        //
        // FloatingActionButton
        //
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchCoinsActivity();
            }
        });
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
        //
        // ListView
        //
        ListView listView = (ListView) findViewById(R.id.lv_watchlist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetailsActivity(watchListAdapter.getWatchedItem(i));
            }
        });
        registerForContextMenu(listView);
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity(){
        if (watchedItems == null) {
            watchedItems = databaseManager.getWatchListTable().getItems();
            watchListAdapter = new WatchListAdapter(this, watchedItems);

            ListView listView = (ListView) findViewById(R.id.lv_watchlist);
            listView.setAdapter(watchListAdapter);
        }

        cryptoCoins      = new HashMap<String,CryptoCoin>();
        cryptoCurrencies = new HashMap<Long,CryptoCurrency>();

        getCryptoCoinsCallback();
        getCryptoCurrenciesCallback();
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinsCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCoins = cryptoData.getAsCryptoCoins();
                updateActivityData();
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
        for(WatchedItem item:watchedItems) {
            cryptoClient.getCrytpoCurrency(item.getFromSymbol(), item.getToSymbol(), new CryptoCallback() {
                @Override
                public void onSuccess(CryptoData cryptoData) {
                    cryptoCurrencies.put(item.getItemId(), cryptoData.getAsCryptoCurrency());
                    updateActivityData();
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
    private void updateActivityData(){
        if ((cryptoCurrencies.size() != watchedItems.size()) || (cryptoCoins.size() == 0))
            return;

        for (WatchedItem item:watchedItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getFromSymbol());
            item.setCryptoCoin(cryptoCoin);

            CryptoCurrency cryptoCurrency = cryptoCurrencies.get(item.getItemId());
            item.setCryptoCurrency(cryptoCurrency);
        }

        watchListAdapter.notifyDataSetChanged();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.watch_list_cab,menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        WatchedItem item = watchListAdapter.getWatchedItem(info.position);
        menu.setHeaderTitle(item.getFromSymbol() + " - " + item.getToSymbol());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_remove:
                    removeWatchedItem(watchListAdapter.getWatchedItem(info.position));
                    break;

            case R.id.action_details:
                    openWatchedItemDetails(watchListAdapter.getWatchedItem(info.position));
                    break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Add the item to the WatchList.
     */
    private void addWatchedItem(WatchedItem item){
        long itemId = databaseManager.getWatchListTable().add(item.getFromSymbol(), item.getToSymbol());

        newItem = new WatchedItem(itemId, item.getFromSymbol(), item.getToSymbol());
        watchedItems.add(newItem);
        watchListAdapter.notifyDataSetChanged();

        ListView listView = (ListView) findViewById(R.id.lv_watchlist);
        listView.smoothScrollToPosition(watchListAdapter.getCount() - 1);
        updateActivity();
    }

    /**
     * Remove the item from the WatchList.
     */
    private void removeWatchedItem(WatchedItem item){
        databaseManager.getWatchListTable().remove(item.getItemId());

        watchedItems.remove(item);
        watchListAdapter.notifyDataSetChanged();
    }

    private void openWatchedItemDetails(WatchedItem item){
        openDetailsActivity(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.watch_list, menu);

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

        if (id == R.id.nav_home) {

            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_watchlist) {

            // already there

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

    /**
     * Open Activity where the user can select the FromSymbol.
     */
    public void openSearchCoinsActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchCoinActivity.class);
        startActivityForResult(intent, SEARCH_COIN_REQUEST);
    }

    /**
     * Open Activity where the user can select the ToSymbol.
     */
    public void openSearchCurrencyActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchCurrencyActivity.class);
        startActivityForResult(intent, SEARCH_CURRENCY_REQUEST);
    }

    /**
     * Open activity with item details.
     */
    public void openDetailsActivity(WatchedItem watchedItem) {

        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);

        intent.putExtra("fromSymbol",watchedItem.getFromSymbol());
        intent.putExtra("toSymbol",watchedItem.getToSymbol());

        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == SEARCH_COIN_REQUEST) && resultCode == (RESULT_OK)){
            String coinName = data.getStringExtra("CoinName");

            if (coinName != null) {
                newItem = new WatchedItem();
                newItem.setFromSymbol(coinName);

                openSearchCurrencyActivity();
            }
        }

        if ((requestCode == SEARCH_CURRENCY_REQUEST) && resultCode == (RESULT_OK)){
            String currencyName = data.getStringExtra("CurrencyName");

            if (currencyName != null) {
                newItem.setToSymbol(currencyName);

                addWatchedItem(newItem);
            }
        }
    }
}
