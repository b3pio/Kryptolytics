package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.cryptoapi.CryptoRate;
import com.fproject.cryptolytics.database.DatabaseManager;
import com.fproject.cryptolytics.searchCoin.SearchCoinActivity;
import com.fproject.cryptolytics.utility.DecimalFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for converting items.
 *
 * @note
 * Activities that contain this fragment must implement the
 * {@link OnConvertItemClickListener} interface.
 */
public class ConverterListFragment extends Fragment {
    private final static int MAX_ITEM_COUNT         = 10;
    private final static int SEARCH_COIN_REQUEST    = 1;

    // These provide data about the watched items.
    private CryptoClient    cryptoClient;
    private DatabaseManager databaseManager;

    private String fromSymbol       = new String();
    private List<String> toSymbols  = new ArrayList<>();

    // List of converter items.
    private List<ConverterItem> converterItems;
    private ConverterItem       selectedItem;

    private Map<String,CryptoCoin>  cryptoCoins;
    private Map<String,CryptoRate>  cryptoRates;

    private ConverterListAdapter        converterListAdapter;
    private OnConvertItemClickListener  convertItemClickListener;

    // --------------------------------------------------------------------------------------------
    //region Constructor/Create Methods
    // --------------------------------------------------------------------------------------------

    public ConverterListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this.getContext());
        databaseManager = new DatabaseManager(this.getContext());
        //
        // Activity
        //
        setupFragment();
        //
        // Listeners
        //
        setupListeners();
        //
        // Data
        //
        updateFragment();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Configure the fragment.
     */
    private void setupFragment(){
        converterItems = new ArrayList<>();
        //
        // recyclerView
        //
        RecyclerView recyclerView = getView().findViewById(R.id.rv_converter);
        recyclerView.setHasFixedSize(true);
        //
        // LayoutManager
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        //
        // ItemDecoration
        //
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        //
        // Adapter
        //
        converterListAdapter = new ConverterListAdapter(this.getContext(), converterItems);
        recyclerView.setAdapter(converterListAdapter);
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners() {
        //
        // SwipeRefreshLayout
        //
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFragment();
            }
        });
        //
        //  OnClickListener
        //
       converterListAdapter.setOnClickListener(new ConverterListAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                convertItemClicked(position);
            }
        });
        //
        //  ItemTouchHelper
        //
        RecyclerView recyclerView = getView().findViewById(R.id.rv_converter);
        ItemTouchHelper helper = createItemTouchHelper();
        helper.attachToRecyclerView(recyclerView);
    }

    /**
     * Create the {@link ItemTouchHelper} that will be attached to the {@link RecyclerView}.
     */
    private ItemTouchHelper createItemTouchHelper(){

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // We need this because Android is crap.
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeConvertItem(viewHolder.getAdapterPosition());
            }
        };

        return new ItemTouchHelper(callback);
    }

    /**
     * This method is called when an item in the ConverterList is clicked.
     */
    private void convertItemClicked(int position) {
        selectedItem = converterListAdapter.getConverterItem(position);

        clearValues();
        updateValues();

        if (convertItemClickListener != null) {
            convertItemClickListener.onConvertItemClicked(selectedItem);
        }
    }

    /**
     * Populate the fragment with data.
     */
    private void updateFragment(){
        converterItems.clear();
        converterItems.addAll(databaseManager.getConverterTable().getItems());
        converterListAdapter.notifyDataSetChanged();

        updateFromSymbol();
        updateToSymbols();

        cryptoCoins = new HashMap<>();
        cryptoRates = new HashMap<>();

        getCryptoCoinsCallback();
        getCryptoRatesCallback();

    }

    private void removeConvertItem(int position) {
        ConverterItem converterItem = converterListAdapter.getConverterItem(position);

        converterListAdapter.removeItem(position);
        databaseManager.getConverterTable().remove(converterItem.getItemId());
    }

    private void addConvertItem(String symbol) {

        databaseManager.getConverterTable().add(symbol);
        updateFragment();
    }

    /**
     * Stop the refresh animation.
     */
    private void hideSwipeRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 1000);
    }

    /**
     * Display a message using a snack bar.
     */
    private void displayMessage(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Override Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_convert_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnConvertItemClickListener) {
            convertItemClickListener = (OnConvertItemClickListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnConvertItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        convertItemClickListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSearchCoinsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == SEARCH_COIN_REQUEST) && resultCode == (RESULT_OK)) {
            String coinName = data.getStringExtra("CoinName");

            if (coinName != null) {
                addConvertItem(coinName);
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Crypto Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinsCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoCoins = cryptoData.getAsCryptoCoins();
                onCryptoDataReceived();
            }

            @Override
            public void onFailure(String cryptoError) {
                hideSwipeRefresh();
            }
        });
    }

    /**
     * Obtain the {@link CryptoRate} data.
     */
    private void getCryptoRatesCallback(){
        if (fromSymbol.isEmpty() || toSymbols.isEmpty())
            return;

        cryptoClient.getCryptoRates(fromSymbol, toSymbols, new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoRates = cryptoData.getAsCryptoRates();
                onCryptoDataReceived();
            }

            @Override
            public void onFailure(String cryptoError) {
                hideSwipeRefresh();
            }
        });
    }

    /**
     * Determines weather all the requested data has arrived and updates the ConvertList.
     */
    private void onCryptoDataReceived(){
        if (cryptoCoins.size() == 0)  return;
        if (cryptoRates.size() == 0)  return;
        if (!areCryptoRatesCorrect()) return;

        for (ConverterItem item:converterItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getSymbol());
            item.setCryptoCoin(cryptoCoin);

            CryptoRate cryptoRate = cryptoRates.get(item.getSymbol());
            item.setCryptoRate(cryptoRate);
        }

        if (selectedItem == null) {
            selectedItem = converterItems.get(0);

            if (converterItems.get(0).getValue().equals(" - ")){
                converterItems.get(0).setValue("1");
            }
        }

        computeValues();
        converterListAdapter.notifyDataSetChanged();
        hideSwipeRefresh();
    }

    /**
     * Determines whether is crypto rates are for current request.
     */
    private boolean areCryptoRatesCorrect() {

        // Maybe an earlier request.
        if (!cryptoRates.get(fromSymbol).getFromSymbol().equals(fromSymbol)) {
            cryptoRates.clear();
            return false;
        }

        return true;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Symbol Methods
    // --------------------------------------------------------------------------------------------

    private void updateFromSymbol(){
        if (selectedItem != null) {
            fromSymbol = selectedItem.getSymbol();
        }
        else {
            if (!converterItems.isEmpty()) {
                fromSymbol = converterItems.get(0).getSymbol();
            }
            else {
                fromSymbol = new String();
            }
        }
    }

    private void updateToSymbols(){
        toSymbols = new ArrayList<>();

        for(ConverterItem convertItem : converterItems) {
            toSymbols.add(convertItem.getSymbol());
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Value Manipulation Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Update the values of the {@link ConverterItem} collection.
     */
    private void updateValues(){
        updateToSymbols();
        updateFromSymbol();

        getCryptoRatesCallback();
    }

    /**
     * Calculate the values of the {@link ConverterItem} collection.
     */
    private void computeValues() {
        if (cryptoRates.isEmpty())
            return;

        // Convert from the value of the selected item to the other ones.
        String valueStr = selectedItem.getValue();
        Double value = Double.valueOf(selectedItem.getValue());

        // Format the selected value properly
        String formattedStr = DecimalFormatter.formatString(valueStr);
        selectedItem.setValue(formattedStr);

        for (ConverterItem converterItem : converterItems) {
            if (converterItem == selectedItem)
                continue;

            Double newValue = value * converterItem.getExRate();
            String formattedValue = DecimalFormatter.formatDouble(newValue);

            converterItem.setValue(formattedValue);
        }
    }

    /**
     * Clear the values of the {@link ConverterItem} collection.
     */
    private void clearValues() {
        cryptoRates.clear();

        for (ConverterItem converterItem:converterItems) {
            converterItem.setValue(" - ");
            converterItem.setCryptoRate(null);
        }

        selectedItem.setValue("1");
        converterListAdapter.notifyDataSetChanged();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Open Activities
    // --------------------------------------------------------------------------------------------

    /**
     * Open Activity where the user can select the FromSymbol.
     */
    public void openSearchCoinsActivity() {
        if (converterItems.size() < MAX_ITEM_COUNT) {

            Intent intent = new Intent(getContext(), SearchCoinActivity.class);
            intent.putExtra("showBackButton", true);
            startActivityForResult(intent, SEARCH_COIN_REQUEST);

        }
        else {
            displayMessage("Maximum item count reached.");
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Public Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Call this method when the value of the selected item changes, and
     * the other values need to be recalculated.
     */
    public void valueChanged(){

        computeValues();
        converterListAdapter.notifyDataSetChanged();
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnConvertItemClickListener {

        // This method is called when an item in the ConverterList is clicked.
        void onConvertItemClicked(ConverterItem converterItem);
    }
}
