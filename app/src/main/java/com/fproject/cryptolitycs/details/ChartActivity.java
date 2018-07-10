package com.fproject.cryptolitycs.details;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolitycs.cryptoapi.CryptoClient;
import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.cryptoapi.CryptoData;
import com.fproject.cryptolitycs.cryptoapi.CryptoHistoryPoint;
import com.fproject.cryptolitycs.utility.DecimalFormatter;
import com.fproject.cryptolitycs.utility.ImageDownloader;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.cryptoapi.CryptoCallback;
import com.fproject.cryptolitycs.utility.DateFormatter;
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
    private List<CryptoHistoryPoint> cryptoHistoryPoints;
    private CryptoCoin cryptoCoin;

    private TextView  tvPrice;
    private TextView  tvHigh;
    private TextView  tvLow;
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
        //  Activity
        //
        setupActivity();
        //
        //  Listeners
        //
        setupListeners();
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
    private void setupActivity() {
        //
        // Params
        //
        fromSymbol = getIntent().getStringExtra("fromSymbol");
        toSymbol = getIntent().getStringExtra("toSymbol");
        //
        //
        //
        tvPrice = findViewById(R.id.tv_price);
        tvHigh  = findViewById(R.id.tv_high);
        tvHigh.setTextColor(ContextCompat.getColor(this, R.color.colorPositive));
        tvLow   = findViewById(R.id.tv_low);
        tvLow.setTextColor(ContextCompat.getColor(this, R.color.colorNegative));
        tvTime  = findViewById(R.id.tv_time);
        //
        // ActionBar
        //
        setupActionBar();
        //
        // Chart
        //
        setupChart();
    }

    /**
     * Configure the Action Bar.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    /**
     * Configure the chart.
     */
    private void setupChart() {
        lineChart = findViewById(R.id.lineChart);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraBottomOffset(10.0f);
        lineChart.setExtraRightOffset(5.0f);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(0.5f);
        lineChart.setBorderColor(ContextCompat.getColor(this, R.color.colorChartBorder));
        //
        // X Axis
        //
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ChartDateFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
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
        leftRight.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
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

                CryptoHistoryPoint cryptoHistoryPoint = (CryptoHistoryPoint) e.getData();
                selectedPointChanged(cryptoHistoryPoint);
            }

            @Override
            public void onNothingSelected() {
                selectLastPoint();
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
     * Obtain the {@link CryptoHistoryPoint} data.
     */
    private void getCryptoHistoryCallback(){
       cryptoClient.getCryptoHistoryPoints(fromSymbol, toSymbol, new CryptoCallback() {
           @Override
           public void onSuccess(CryptoData cryptoData) {
                cryptoHistoryPoints = cryptoData.asCryptoHistoryPoints();
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
                cryptoCoin = cryptoData.asCryptoCoins().get(fromSymbol);
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

        getSupportActionBar().setTitle(cryptoCoin.getCoinName());

        ImageView ivImage = findViewById(R.id.iv_image);
        new ImageDownloader(ivImage).execute(cryptoCoin.getImageUrl());

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(cryptoCoin.getSymbol());
    }

    /**
     * Populate the chart with data.
     */
    private void updateChart(){
        if ((cryptoHistoryPoints == null) || cryptoHistoryPoints.isEmpty()) return;

        List<Entry> entries = new ArrayList<Entry>();

        for (CryptoHistoryPoint cryptoHistoryPoint : cryptoHistoryPoints) {
            Entry entry = new Entry();

            entry.setX(cryptoHistoryPoint.getTime());
            entry.setY(cryptoHistoryPoint.getClose());
            entry.setData(cryptoHistoryPoint);

            entries.add(entry);
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorChartMain));
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.colorChartMain));
        dataSet.setFillAlpha(200);
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(ContextCompat.getColor(this, R.color.colorChartBorder));
        dataSet.setHighlightEnabled(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.setScaleEnabled(false);

        selectLastPoint();
    }

    /**
     * This method has to be called when the selected value on the chart has changed.
     */
    private void selectedPointChanged(CryptoHistoryPoint cryptoHistoryPoint) {
        String timeStr = DateFormatter.format(cryptoHistoryPoint.getTime());
        tvTime.setText(timeStr);

        String highStr = DecimalFormatter.formatFloat(cryptoHistoryPoint.getHigh());
        tvHigh.setText(highStr);

        String lowStr = DecimalFormatter.formatFloat(cryptoHistoryPoint.getLow());
        tvLow.setText(lowStr);

        String priceStr = DecimalFormatter.formatFloat(cryptoHistoryPoint.getClose()) + " " + toSymbol;
        tvPrice.setText(priceStr);
    }

    /**
     * Select the last point on the chart.
     */
    private void selectLastPoint(){
        if ((cryptoHistoryPoints != null) && (!cryptoHistoryPoints.isEmpty()) ) {
            selectedPointChanged(cryptoHistoryPoints.get(cryptoHistoryPoints.size() - 1));
        }
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
