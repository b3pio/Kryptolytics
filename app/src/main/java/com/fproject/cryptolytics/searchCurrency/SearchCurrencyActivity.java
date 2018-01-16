package com.fproject.cryptolytics.searchCurrency;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.utility.ResourceHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fproject.cryptolytics.utility.ResourceHelper.getStringMap;

public class SearchCurrencyActivity extends AppCompatActivity {

    private  SearchCurrencyAdapter searchCurrencyAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_currency);

        populateListView();
        setupListView();
        setupSearchView();
    }

    private void populateListView(){
        Map<String,String> currencyMap =  ResourceHelper.getStringMap(this, R.array.currencies);
        List<String> currencyList = new ArrayList<>();

        for (Map.Entry<String,String> currency:currencyMap.entrySet()){
            currencyList.add(currency.getKey() + " - " + currency.getValue());
        }

        ListView listView = (ListView) findViewById(R.id.lv_search_currency);
        searchCurrencyAdapter = new SearchCurrencyAdapter(this, currencyList);
        listView.setAdapter(searchCurrencyAdapter);
    }

    /**
     * Configure the ListView and hook up the event listeners.
     */
    private void setupListView(){
        ListView listView = (ListView) findViewById(R.id.lv_search_currency);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String currency = (String)listView.getItemAtPosition(position);
                String symbol   =  currency.split(" - ")[0];
                closeActivty(symbol);
            }
        });
    }

    private void setupSearchView() {
        SearchView searchView = (SearchView) findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
    }

    /**
     * Close the activity an return the specified CoinName.
     */
    public void closeActivty(String currencyName){
        Intent intent = new Intent();
        intent.putExtra("CurrencyName", currencyName);
        setResult(RESULT_OK,intent);
        finish();
    }
}
