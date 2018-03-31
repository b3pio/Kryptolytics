package com.fproject.cryptolytics.searchCurrency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;


import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.utility.ResourceHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Activity for searching between the list of traditional currencies (like Euro, Dollar...)
 */
public class SearchCurrencyActivity extends AppCompatActivity {

    private  SearchCurrencyAdapter searchCurrencyAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_currency);
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
        Map<String,String> currencyMap = ResourceHelper.getStringMap(this, R.array.currencies);
        List<String> currencyList = new ArrayList<>();

        for (Map.Entry<String,String> currency:currencyMap.entrySet()){
            currencyList.add(currency.getKey() + " - " + currency.getValue());
        }

        ListView listView = findViewById(R.id.lv_search_currency);
        searchCurrencyAdapter = new SearchCurrencyAdapter(this, currencyList);
        listView.setAdapter(searchCurrencyAdapter);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        ListView listView = findViewById(R.id.lv_search_currency);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String symbol = searchCurrencyAdapter.getSymbolAt(position);
                closeActivty(symbol);
            }
        });
    }

    @NonNull
    private SearchView.OnQueryTextListener createOnQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ListView listView = findViewById(R.id.lv_search_currency);

                SearchCurrencyAdapter adapter =  (SearchCurrencyAdapter) listView.getAdapter();
                adapter.getFilter().filter(s);

                return false;
            }
        };
    }

    /**
     * Close the activity an return the specified CoinName.
     */
    private void closeActivty(String currencyName){
        Intent intent = new Intent();
        intent.putExtra("CurrencyName", currencyName);

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
        getMenuInflater().inflate(R.menu.menu_search_currency, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(createOnQueryTextListener());

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
