package com.fproject.cryptolytics.searchCoin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity for searching between the list of available {@link CryptoCoin}s.
 */
public class SearchCoinActivity extends AppCompatActivity {

    // Provides the list of available coins
    private CryptoClient cryptoClient = null;

    /// The actual list of available coins.
    private List<CryptoCoin>    cryptoCoins =  null;
    private SearchCoinAdapter   searchCoinAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_coin);
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
        Boolean showBackButton = getIntent().getBooleanExtra("showBackButton", false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity(){
        cryptoCoins = new ArrayList<>();

        getCryptoCoinsCallback();
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinsCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {

                cryptoCoins = new ArrayList<>(cryptoData.getAsCryptoCoins().values());
                Collections.sort(cryptoCoins, CryptoCoin.SortOrderComparator);

                onCryptoDataReceived();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    /**
     *
     */
    private void onCryptoDataReceived(){
        searchCoinAdapter = new SearchCoinAdapter(SearchCoinActivity.this, cryptoCoins);

        ListView listView = findViewById(R.id.lv_search_coin);
        listView.setAdapter(searchCoinAdapter);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        ListView listView = findViewById(R.id.lv_search_coin);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CryptoCoin cryptoCoin = searchCoinAdapter.getCoinAt(position);
                closeActivity(cryptoCoin.getSymbol());
            }
        });
    }

    @NonNull
    private SearchView.OnQueryTextListener createSearchViewListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ListView listView = findViewById(R.id.lv_search_coin);

                SearchCoinAdapter adapter = (SearchCoinAdapter) listView.getAdapter();
                adapter.getFilter().filter(s);

                return false;
            }
        };
    }

    /**
     * Close the activity an return the specified CoinName.
     */
    public void closeActivity(String coinName){
        Intent intent = new Intent();
        intent.putExtra("CoinName", coinName);

        setResult(RESULT_OK,intent);
        finish();
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
        getMenuInflater().inflate(R.menu.search_coin, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(createSearchViewListener());

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

}
