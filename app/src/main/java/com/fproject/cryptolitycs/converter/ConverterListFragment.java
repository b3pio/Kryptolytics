package com.fproject.cryptolitycs.converter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.fproject.cryptolitycs.cryptoapi.CryptoClient;
import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.cryptoapi.CryptoData;
import com.fproject.cryptolitycs.database.DatabaseManager;
import com.fproject.cryptolitycs.searchCoin.SearchCoinActivity;
import com.fproject.cryptolitycs.utility.DecimalFormatter;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.cryptoapi.CryptoCallback;
import com.fproject.cryptolitycs.cryptoapi.CryptoRate;

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
 * {@link OnConvertItemSelectionListener} interface.
 */
public class ConverterListFragment extends Fragment {
    private final static int MAX_ITEM_COUNT         = 10;
    private final static int SEARCH_COIN_REQUEST    = 1;

    // These provide data about the watched items.
    private CryptoClient cryptoClient;
    private DatabaseManager databaseManager;

    private String fromSymbol       = new String();
    private List<String> toSymbols  = new ArrayList<>();

    // List of converter items.
    private List<ConverterItem> converterItems;
    private ConverterItem       selectedItem;

    private Map<String,CryptoCoin>  cryptoCoins;
    private Map<String,CryptoRate>  cryptoRates;

    private ConverterListAdapter            converterListAdapter;
    private OnConvertItemSelectionListener  convertItemSelectionListener;

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

        notifySelectionListener();
    }

    /**
     * Populate the fragment with data.
     */
    private void updateFragment() {
        selectedItem   = null;
        converterItems = databaseManager.getConverterTable().getItems();
        converterListAdapter.setItems(converterItems);

        clearValues();

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

        Toast.makeText(getActivity().getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
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
                cryptoCoins = cryptoData.asCryptoCoins();
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
        cryptoClient.getCryptoRates(fromSymbol, toSymbols, new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoRates = cryptoData.asCryptoRates();
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
        if (!areCryptoRatesCorrect()) return;
        if ((cryptoRates.size() == 0) && (!converterItems.isEmpty())) return;

        for (ConverterItem item:converterItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getSymbol());
            item.setCryptoCoin(cryptoCoin);

            CryptoRate cryptoRate = cryptoRates.get(item.getSymbol());
            item.setCryptoRate(cryptoRate);
        }

        computeValues();
        converterListAdapter.notifyDataSetChanged();
        hideSwipeRefresh();
    }

    /**
     * Determines whether is crypto rates are for current request.
     */
    private boolean areCryptoRatesCorrect() {
        if (cryptoRates.isEmpty())
            return true;

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

        cryptoRates.clear();
        getCryptoRatesCallback();
    }

    /**
     * Calculate the values of the {@link ConverterItem} collection.
     *
     * @note - Call this only directly after the selected value has changed.
     */
    private void computeValues() {
        if (selectedItem == null)  return;
        if (cryptoRates.isEmpty()) return;
        if (converterItems.isEmpty()) return;

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
        if (converterItems.isEmpty()) return;

        for (ConverterItem converterItem:converterItems) {
            converterItem.setValue(" - ");
        }

        if (selectedItem == null) {
            selectedItem = converterItems.get(0);
        }

        selectedItem.setValue("1");
        converterListAdapter.notifyDataSetChanged();
        notifySelectionListener();
    }

    private void notifySelectionListener() {

        if (convertItemSelectionListener != null) {
            convertItemSelectionListener.onConvertItemSelected(selectedItem);
        }

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
            displayMessage("Maximum item count reached!");
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

        if (context instanceof OnConvertItemSelectionListener) {
            convertItemSelectionListener = (OnConvertItemSelectionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnConvertItemSelectionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        convertItemSelectionListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnConvertItemSelectionListener {

        // This method is called when an item in the ConverterList is clicked.
        void onConvertItemSelected(ConverterItem converterItem);
    }
}
