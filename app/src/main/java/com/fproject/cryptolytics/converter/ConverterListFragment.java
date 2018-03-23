package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCallback;
import com.fproject.cryptolytics.cryptoapi.CryptoClient;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoData;
import com.fproject.cryptolytics.cryptoapi.CryptoRate;
import com.fproject.cryptolytics.database.DatabaseManager;
import com.fproject.cryptolytics.utility.DecimalFormater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for converting items.
 *
 * @note
 * Activities that contain this fragment must implement the
 * {@link OnConvertItemClickListener} interface.
 */
public class ConverterListFragment extends Fragment {
    private final static String MODULE_TAG = "[ConverterListFragment]";

    // These provide data about the watched items.
    private CryptoClient    cryptoClient    = null;
    private DatabaseManager databaseManager = null;

    private String fromSymbol       = new String();
    private List<String> toSymbols  = new ArrayList<>();

    // List of converter items.
    private List<ConverterItem> converterItems  = null;
    private ConverterItem       selectedItem    = null;

    private Map<String,CryptoCoin>  cryptoCoins = null;
    private Map<String,CryptoRate>  cryptoRates  = null;

    private ConverterListAdapter  converterListAdapter  = null;

    // Listener that need to be notified
    private OnConvertItemClickListener convertItemClickListener;


    public ConverterListFragment() {
        // Required empty public constructor
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
        } else {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
        // Components
        //
        cryptoClient = new CryptoClient(this.getContext());
        databaseManager = new DatabaseManager(this.getContext());
        //
        // Listeners
        //
        setupListeners();
        //
        // Update
        //
        updateFragment();
    }

    private void setUpdating(boolean value) {
        if (getActivity() == null)
            return;

        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setSubtitle(value ? getResources().getString(R.string.subtitle_update) : "");
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(){
        //
        // ListView
        //
        ListView listView = getView().findViewById(R.id.lv_converter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                convertItemClicked(position);
            }
        });
    }

    /**
     * This method is called when an item in the ConverterList is clicked.
     */
    private void convertItemClicked(int position) {
        selectedItem = converterListAdapter.getConveterItem(position);

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
        setUpdating(true);
        if (converterItems == null) {

            converterItems = new ArrayList<>();
            converterItems.add(new ConverterItem(0, "BTC", "1.00"));
            converterItems.add(new ConverterItem(1, "LTC", " - "));
            converterItems.add(new ConverterItem(2, "XRP", " - "));
            converterItems.add(new ConverterItem(3, "XMR", " - "));


            ListView listView = getView().findViewById(R.id.lv_converter);
            converterListAdapter  = new ConverterListAdapter(getContext(), converterItems);
            listView.setAdapter(converterListAdapter);
        }

        updateFromSymbol();
        updateToSymbols();

        cryptoCoins = new HashMap<>();
        cryptoRates = new HashMap<>();

        getCryptoCoinsCallback();
        getCryptoRatesCallback();
    }

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
                onCryptoDataRecevied();
            }

            @Override
            public void onFailure(String cryptoError) {
                setUpdating(false);
            }
        });
    }

    /**
     * Obtain the {@link CryptoRate} data.
     */
    private void getCryptoRatesCallback(){
        if (fromSymbol.isEmpty() || toSymbols.isEmpty())
            return;

        cryptoClient.getCrytpoRates(fromSymbol, toSymbols, new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {
                cryptoRates = cryptoData.getAsCryptoRates();
                onCryptoDataRecevied();
            }

            @Override
            public void onFailure(String cryptoError) {
                setUpdating(false);
            }
        });
    }

    /**
     * Determines weather all the requested data has arrived and updates the ConvertList.
     */
    private void onCryptoDataRecevied(){
        if (cryptoCoins.size() == 0) return;
        if (cryptoRates.size() == 0) return;
        if (!areCryptoRatesCorrect()) return;

        for (ConverterItem item:converterItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getSymbol());
            item.setCrpytoCoin(cryptoCoin);

            CryptoRate cryptoRate = cryptoRates.get(item.getSymbol());
            item.setCryptoRate(cryptoRate);
        }

        if (selectedItem == null) {
            selectedItem = converterItems.get(0);
        }

        computeValues();
        converterListAdapter.notifyDataSetChanged();
        setUpdating(false);
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
        String formatedStr = DecimalFormater.formatString(valueStr);
        selectedItem.setValue(formatedStr);

        for (ConverterItem converterItem : converterItems) {
            if (converterItem == selectedItem)
                continue;

            Double newValue = value * converterItem.getExRate();
            String formatedValue = DecimalFormater.formatDouble(newValue);

            converterItem.setValue(formatedValue);
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

        selectedItem.setValue("1.00");
        converterListAdapter.notifyDataSetChanged();
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
