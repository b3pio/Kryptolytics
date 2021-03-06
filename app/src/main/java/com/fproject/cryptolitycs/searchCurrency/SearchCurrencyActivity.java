package com.fproject.cryptolitycs.searchCurrency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.fproject.cryptolitycs.utility.ResourceHelper;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.utility.Settings;

import java.util.ArrayList;
import java.util.Collections;
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
        setTheme(Settings.getTheme(this));
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
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

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

        Collections.sort(currencyList);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String symbol = searchCurrencyAdapter.getSymbolAt(position);
                closeActivity(symbol);
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
    private void closeActivity(String currencyName){
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
        getMenuInflater().inflate(R.menu.search_currency, menu);

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
