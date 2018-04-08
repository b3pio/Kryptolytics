package com.fproject.cryptolytics.details;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.utility.ImageDownloader;

/**
 * Displays information about an item in the WatchList.
 */
public class DetailsActivity extends AppCompatActivity {

    // Components
    private CryptoClient cryptoClient   = null;

    // The coin to convert from/to
    private String fromSymbol = null;
    private String toSymbol   = null;

    // The activity display about this currency
    private CryptoCoin      cryptoCoin     = null;
    private CryptoCurrency  cryptoCurrency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_details);
        //
        // Action Bar
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        //
        //  Components
        //
        cryptoClient = new CryptoClient(this);
        //
        //
        //
        updateActivity();
    }

    private void updateActivity(){
        fromSymbol = getIntent().getStringExtra("fromSymbol");
        toSymbol = getIntent().getStringExtra("toSymbol");

        getCryptoCoinCallback();
        getCryptoCurrencyCallback();
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCoin = cryptoData.getAsCryptoCoins().get(fromSymbol);
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
    private void getCryptoCurrencyCallback(){
        cryptoClient.getCryptoCurrency(fromSymbol, toSymbol, new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCurrency =  cryptoData.getAsCryptoCurrency();
                updateActivityData();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    private void updateActivityData(){
        if ((cryptoCoin == null) || (cryptoCurrency == null)) return;

        ImageView ivImage = findViewById(R.id.image);
        new ImageDownloader(ivImage).execute(cryptoCoin.getImageUrl());

        //getSupportActionBar().setTitle(cryptoCoin.getCoinName());

        TextView tvName = findViewById(R.id.name);
        tvName.setText(cryptoCoin.getFullName());

        TextView tvPrice = findViewById(R.id.price);
        String priceStr = cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol();
        tvPrice.setText(priceStr);

        TextView tvHigh = findViewById(R.id.high);
        String highStr = cryptoCurrency.getHigh()+ " " + cryptoCurrency.getToSymbol();
        tvHigh.setText(highStr);

        TextView tvLow = findViewById(R.id.low);
        String lowStr = cryptoCurrency.getLow()+ " " + cryptoCurrency.getToSymbol();
        tvLow.setText(lowStr);

        TextView tvOpen = findViewById(R.id.open);
        String openStr = cryptoCurrency.getOpen() + " " + cryptoCurrency.getToSymbol();
        tvOpen.setText(openStr);

        TextView tvVolume = findViewById(R.id.volume);
        tvVolume.setText(cryptoCurrency.getVolume());

        TextView tvLastUpdate = findViewById(R.id.lastUpdate);
        tvLastUpdate.setText(cryptoCurrency.getLastUpdate());

        TextView tvChangeValue = findViewById(R.id.changeValue);
        String changeValueStr = cryptoCurrency.getChangeValue() + " " + cryptoCurrency.getToSymbol();
        tvChangeValue.setText(changeValueStr);

        TextView tvChangePercent = findViewById(R.id.changePercent);
        String changePercentStr = cryptoCurrency.getChangePercent() + "%";
        tvChangePercent.setText(changePercentStr);

        int color = getChangeColor(cryptoCurrency.isChangePositive());
        tvChangePercent.setTextColor(color);
        tvChangeValue.setTextColor(color);

        TextView tvSupply = findViewById(R.id.supply);
        tvSupply.setText(cryptoCurrency.getSupply());

        /*
        TextView tvMarket = findViewById(R.id.market);
        tvMarket.setText(cryptoCurrency.getMarket());
        */


        TextView tvMarketCap = findViewById(R.id.marketCap);
        tvMarketCap.setText(cryptoCurrency.getMarketCap());
    }



    private int getChangeColor(boolean positive) {

        if (positive) {
            return ContextCompat.getColor(this, R.color.colorPositive);
        }
        else {
            return ContextCompat.getColor(this, R.color.colorNegative);
        }
    }

    /**
     * Open activity with item details.
     */
    public void openChartActivity() {

        Intent intent = new Intent(getApplicationContext(), ChartActivity.class);

        intent.putExtra("fromSymbol", fromSymbol);
        intent.putExtra("toSymbol", toSymbol);

        startActivityForResult(intent, 3);
    }

    // --------------------------------------------------------------------------------------------
    //region Override Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_chart:
                openChartActivity();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
