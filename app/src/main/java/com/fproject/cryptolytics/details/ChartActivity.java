package com.fproject.cryptolytics.details;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.cryptoapi.CryptoHistory;
import com.fproject.cryptolytics.utility.DateFormatter;
import com.fproject.cryptolytics.utility.ImageDownloader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    // Components
    private CryptoClient cryptoClient   = null;

    // The coin to convert from/to
    private String fromSymbol = null;
    private String toSymbol   = null;

    // The data that will displayed by the activity.
    private List<CryptoHistory> cryptoHistories;
    private CryptoCoin          cryptoCoin;

    private TextView  tvPrice;
    private TextView  tvTime;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //
        //  Components
        //
        cryptoClient = new CryptoClient(this);
        //
        //
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
        tvPrice = findViewById(R.id.tv_price);
        tvTime  = findViewById(R.id.tv_time);
        //
        // Toolbar
        //
        //getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        //
        // Params
        //
        fromSymbol = getIntent().getStringExtra("fromSymbol");
        toSymbol = getIntent().getStringExtra("toSymbol");
        //
        // Chart
        //
        setupChart();
    }

    /**
     * Configure the Chart component.
     */
    private void setupChart() {
        lineChart = findViewById(R.id.lineChart);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraBottomOffset(10.0f);
        lineChart.setExtraRightOffset(5.0f);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(0.5f);
        lineChart.setBorderColor(ContextCompat.getColor(this, R.color.colorChartText));
        //
        // X Axis
        //
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ChartDateFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.colorText));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(false);
        //
        // Right Axis
        //
        lineChart.getAxisLeft().setEnabled(false);
        //
        // Left Axis
        //
        YAxis leftRight = lineChart.getAxisRight();
        leftRight.setTextColor(ContextCompat.getColor(this, R.color.colorText));
        leftRight.setDrawGridLines(true);
        leftRight.setDrawAxisLine(false);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                CryptoHistory cryptoHistory = (CryptoHistory) e.getData();
                selectedChanged(cryptoHistory);
            }

            @Override
            public void onNothingSelected() {
                if (!cryptoHistories.isEmpty()) {
                    selectedChanged(cryptoHistories.get(cryptoHistories.size() - 1));
                }
            }
        });
    }

    /**
     * Populate the activity with data.
     */
    private void updateActivity() {
        getCryptoHistoryCallback();
        getCryptoCoinCallback();
    }

    /**
     * Obtain the {@link CryptoHistory} data.
     */
    private void getCryptoHistoryCallback(){
       cryptoClient.getCryptoHistories(fromSymbol, toSymbol, new CryptoCallback() {
           @Override
           public void onSuccess(CryptoData cryptoData) {
                cryptoHistories = cryptoData.getAsCryptoHistories();
                updateChart();
           }

           @Override
           public void onFailure(String cryptoError) {

           }
        });
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCoin = cryptoData.getAsCryptoCoins().get(fromSymbol);
                updateHeader();
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    /**
     * Populate the activities header with data.
     */
    private void updateHeader() {
        if (cryptoCoin == null) return;

        ImageView ivImage = findViewById(R.id.iv_image);
        new ImageDownloader(ivImage).execute(cryptoCoin.getImageUrl());

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(cryptoCoin.getFullName());
    }

    /**
     * Populate the chart with data.
     */
    private void updateChart(){
        if ((cryptoHistories == null) || cryptoHistories.isEmpty()) return;

        List<Entry> entries = new ArrayList<Entry>();

        for (CryptoHistory cryptoHistory : cryptoHistories) {
            Entry entry = new Entry();

            entry.setX(cryptoHistory.getTime());
            entry.setY(cryptoHistory.getClose());
            entry.setData(cryptoHistory);

            entries.add(entry);
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorChartMain));
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.colorChartMain));
        dataSet.setFillAlpha(200);
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(ContextCompat.getColor(this, R.color.colorChartText));
        dataSet.setHighlightEnabled(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        selectedChanged(cryptoHistories.get(cryptoHistories.size() - 1));
    }

    /**
     * This method has to be called when the selected value on the chart has changed.
     */
    private void selectedChanged(CryptoHistory cryptoHistory) {
        String priceStr = cryptoHistory.getClose() + " " + toSymbol;
        tvPrice.setText(priceStr);

        String timeStr = DateFormatter.format(cryptoHistory.getTime());
        tvTime.setText(timeStr);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Override Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
