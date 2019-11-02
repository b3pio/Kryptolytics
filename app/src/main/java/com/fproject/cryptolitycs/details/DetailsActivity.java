package com.fproject.cryptolitycs.details;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolitycs.cryptoapi.CryptoClient;
import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.cryptoapi.CryptoData;
import com.fproject.cryptolitycs.utility.ImageDownloader;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.cryptoapi.CryptoCallback;
import com.fproject.cryptolitycs.cryptoapi.CryptoCurrency;
import com.fproject.cryptolitycs.utility.ResourceHelper;
import com.fproject.cryptolitycs.utility.Settings;

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
    private CryptoCoin cryptoCoin     = null;
    private CryptoCurrency  cryptoCurrency = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.getTheme(this));
        setContentView(R.layout.content_details);
        //
        //  Components
        //
        cryptoClient = new CryptoClient(this);
        //
        //  Activity
        //
        setupActivity();
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
    private void setupActivity(){
        fromSymbol = getIntent().getStringExtra("fromSymbol");
        toSymbol = getIntent().getStringExtra("toSymbol");
        //
        // ActionBar
        //
        setupActionBar();
    }


    /**
     * Configure the Action Bar.
     */
    private void setupActionBar(){
        Toolbar myToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity(){
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
                cryptoCoin = cryptoData.asCryptoCoins().get(fromSymbol);
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
                cryptoCurrency =  cryptoData.asCryptoCurrency();
                updateActivityData();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    private void updateActivityData(){
        if ((cryptoCoin == null) || (cryptoCurrency == null)) return;
        //
        //  Name
        //
        getSupportActionBar().setTitle(cryptoCoin.getCoinName());
        //
        //  Image
        //
        ImageView ivImage = findViewById(R.id.iv_image);
        new ImageDownloader(ivImage).execute(cryptoCoin.getImageUrl());
        //
        //  Name
        //
        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(cryptoCoin.getSymbol());
        //
        //  Price
        //
        TextView tvPrice = findViewById(R.id.tv_price);
        String priceStr = cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol();
        tvPrice.setText(priceStr);
        //
        //  High
        //
        TextView tvHigh = findViewById(R.id.tv_high);
        String highStr = cryptoCurrency.getHigh()+ " " + cryptoCurrency.getToSymbol();
        tvHigh.setText(highStr);
        tvHigh.setTextColor( ResourceHelper.getThemeColor(this,R.attr.colorPositiveValue));
        //
        //  Low
        //
        TextView tvLow = findViewById(R.id.tv_low);
        String lowStr = cryptoCurrency.getLow() + " " + cryptoCurrency.getToSymbol();
        tvLow.setText(lowStr);
        tvLow.setTextColor( ResourceHelper.getThemeColor(this,R.attr.colorNegativeValue));
        //
        //  Open
        //
        TextView tvOpen = findViewById(R.id.tv_open);
        String openStr = cryptoCurrency.getOpen() + " " + cryptoCurrency.getToSymbol();
        tvOpen.setText(openStr);
        //
        //  Volume
        //
        TextView tvVolume = findViewById(R.id.tv_volume);
        tvVolume.setText(cryptoCurrency.getVolume());
        //
        //  LastUpdate
        //
        TextView tvLastUpdate = findViewById(R.id.tv_lastUpdate);
        tvLastUpdate.setText(cryptoCurrency.getLastUpdate());
        //
        //  ChangeValue
        //
        TextView tvChangeValue = findViewById(R.id.tv_changeValue);
        String changeValueStr = cryptoCurrency.getChangeValue() + " " + cryptoCurrency.getToSymbol();
        tvChangeValue.setText(changeValueStr);
        //
        //  ChangePercent
        //
        TextView tvChangePercent = findViewById(R.id.tv_changePercent);
        String changePercentStr = cryptoCurrency.getChangePercent() + "%";
        tvChangePercent.setText(changePercentStr);
        //
        //  ChangeColor
        //
        int color = getChangeColor(cryptoCurrency.isChangePositive());
        tvChangePercent.setTextColor(color);
        tvChangeValue.setTextColor(color);
        //
        //  Supply
        //
        TextView tvSupply = findViewById(R.id.tv_supply);
        tvSupply.setText(cryptoCurrency.getSupply());
        //
        //  MarketCap
        //
        TextView tvMarketCap = findViewById(R.id.tv_marketCap);
        tvMarketCap.setText(cryptoCurrency.getMarketCap());
        //
        //  Algorithm
        //
        TextView tvAlgorithm = findViewById(R.id.tv_algorithm);
        tvAlgorithm.setText(cryptoCoin.getAlgorithm());
        //
        //  ProofType
        //
        TextView tvProofType = findViewById(R.id.tv_proofType);
        tvProofType.setText(cryptoCoin.getProofType());
    }

    private int getChangeColor(boolean positive) {

        if (positive) {
            return  ResourceHelper.getThemeColor(this,R.attr.colorPositiveValue);
        }
        else {
            return  ResourceHelper.getThemeColor(this,R.attr.colorNegativeValue);
        }
    }

    /**
     * Open activity with chart.
     */
    public void openChartActivity() {

        Intent intent = new Intent(getApplicationContext(), ChartActivity.class);

        intent.putExtra("fromSymbol", fromSymbol);
        intent.putExtra("toSymbol", toSymbol);

        startActivityForResult(intent, 3);
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
