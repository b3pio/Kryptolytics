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


import java.util.ArrayList;
import java.util.List;

public class SearchCurrencyActivity extends AppCompatActivity {

    //private ArrayAdapter arrayAdapter = null;
    private  SearchCurrencyAdapter searchCurrencyAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_currency);

        List<String> currencies = new ArrayList<>();
        currencies.add("EUR");
        currencies.add("USD");
        currencies.add("RON");
        currencies.add("CHF");
        currencies.add("GBP");
        currencies.add("CNY");
        currencies.add("JPY");
        currencies.add("CAD");
        currencies.add("HUF");

        ListView listView = (ListView) findViewById(R.id.lv_search_currency);
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, currencies);
        //listView.setAdapter(arrayAdapter);
        searchCurrencyAdapter = new SearchCurrencyAdapter(this,currencies);
        listView.setAdapter(searchCurrencyAdapter);

        setupListView();
        setupSearchView();
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
                closeActivty(currency);
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

                //ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
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
