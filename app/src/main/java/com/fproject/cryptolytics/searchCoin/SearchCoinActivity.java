package com.fproject.cryptolytics.searchCoin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
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
import java.util.List;

/**
 * Activity for searching between the list of available {@link CryptoCoin}s.
 */
public class SearchCoinActivity extends AppCompatActivity {

    // Provides the list of available coins
    private CryptoClient cryptoClient = null;

    /// The actual list of available coins.
    private List<CryptoCoin> cryptoCoins = new ArrayList<>();

    // The listview adapter.
    private SearchCoinAdapter searchCoinAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_coin);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this);
        //
        //
        //
        populateActivty();
    }

    /**
     * Populate activity with the necessary data.
     */
    private void populateActivty() {
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCoins = new ArrayList<CryptoCoin>(cryptoData.getAsCryptoCoins().values());

                populateListView();
                setupListView();
                setupSearchView();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    /**
     * Populate ListView with the list of available {@link CryptoCoin}s.
     */
    private void populateListView(){
        searchCoinAdapter = new SearchCoinAdapter(SearchCoinActivity.this, cryptoCoins);

        ListView listView = (ListView) findViewById(R.id.lv_search_coin);
        listView.setAdapter(searchCoinAdapter);
    }

    /**
     * Configure the SearchView and hook up the event listeners.
     */
    private void setupSearchView() {
        SearchView searchView = (SearchView) findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
    }

    /**
     * Configure the ListView and hook up the event listeners.
     */
    private void setupListView(){
        ListView listView = (ListView) findViewById(R.id.lv_search_coin);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CryptoCoin cryptoCoin = (CryptoCoin)listView.getItemAtPosition(position);
                closeActivty(cryptoCoin.getSymbol());
            }
        });
    }

    /**
     * Close the activity an return the specified CoinName.
     */
    public void closeActivty(String coinName){
        Intent intent = new Intent();
        intent.putExtra("CoinName", coinName);
        setResult(RESULT_OK,intent);
        finish();
    }

}
