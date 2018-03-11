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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnConvertItemClickListener} interface
 * to handle interaction events.
 * Use the {@link ConverterListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConverterListFragment extends Fragment {
    private final static String MODULE_TAG = "[ConverterListFragment]";

    // These provide data about the watched items.
    private CryptoClient    cryptoClient    = null;
    private DatabaseManager databaseManager = null;

    private String fromSymbol = new String();
    private List<String> toSymbols = new ArrayList<>();

    // List of converter items.
    private List<ConverterItem> converterItems  = null;
    private ConverterItem       selectedItem    = null;

    private Map<String,CryptoCoin>  cryptoCoins     = null;
    private Map<String,CryptoRate>  cryptoRates     = null;

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(" Updating...");
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
                selectedItem = converterListAdapter.getConveterItem(position);

                clearValues();
                updateValues();

                if (convertItemClickListener != null) {
                    convertItemClickListener.onConvertItemClicked(selectedItem);
                }
            }
        });
    }

    /**
     * Populate the fragment with data.
     */
    private void updateFragment(){
        if (converterItems == null) {

            converterItems = new ArrayList<>();
            converterItems.add(new ConverterItem(0, "BTC", "1.00"));
            converterItems.add(new ConverterItem(1, "ETH", " - "));
            converterItems.add(new ConverterItem(2, "XRP", " - "));
            converterItems.add(new ConverterItem(3, "ETL", " - "));
            converterItems.add(new ConverterItem(4, "BTC", " - "));

            ListView listView = getView().findViewById(R.id.lv_converter);
            converterListAdapter  = new ConverterListAdapter(getContext(), converterItems);
            listView.setAdapter(converterListAdapter);
        }

        updateFromSymbol();
        updateToSymbols();

        cryptoCoins = new HashMap<>();
        cryptoRates = new HashMap<>();

        getCryptoCoinsCallback();
        //getCryptoRatesCallback();
    }

    private void updateFromSymbol(){
        if (selectedItem != null) {
            fromSymbol = selectedItem.getSymbol();
        }
        else {
            fromSymbol = converterItems.get(0).getSymbol();
        }
    }

    private void updateToSymbols(){
        toSymbols = new ArrayList<>();

        for(ConverterItem convertItem : converterItems) {
            toSymbols.add(convertItem.getSymbol());
        }
    }

    /**
     * Obtain the {@link CryptoCoin} data.
     */
    private void getCryptoCoinsCallback(){
        cryptoClient.getCryptoCoins(new CryptoCallback() {
            @Override
            public void onSuccess(CryptoData cryptoData) {

                cryptoCoins = cryptoData.getAsCryptoCoins();
                onCryptoDataRecevied();

                Log.d(MODULE_TAG, "getCryptoCoinsCallback() - onSuccess()");
            }

            @Override
            public void onFailure(String cryptoError) {
                Log.d(MODULE_TAG, "getCryptoCoinsCallback() - onFailure()");
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

                // Earlier request
                Log.d(MODULE_TAG, "getCryptoRatesCallback() - onSuccess()" + String.valueOf(cryptoRates.size()));


                if (!cryptoRates.isEmpty()) {
                    if (cryptoRates.entrySet().iterator().next().getValue().getFromSymbol().equals(fromSymbol)) {
                        onCryptoDataRecevied();
                    }
                }


                Log.d(MODULE_TAG, "getCryptoRatesCallback() - onSuccess()");
            }

            @Override
            public void onFailure(String cryptoError) {
                Log.d(MODULE_TAG, "getCryptoRatesCallback() - onFailure()");
            }
        });
    }

    /**
     * Determines weather all the requested data has arrived and updates the ConvertList.
     */
    private void onCryptoDataRecevied(){
        if (cryptoCoins.size() == 0) return;
        if (cryptoRates.size() == 0) return;

        for (ConverterItem item:converterItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getSymbol());
            item.setCrpytoCoin(cryptoCoin);

            CryptoRate cryptoRate = cryptoRates.get(item.getSymbol());
            item.setCryptoRate(cryptoRate);

            /*
            Log.d(MODULE_TAG, "[" + item.getSymbol() + "][" +
                cryptoRate.getFromSymbol() +  " -> " +
                cryptoRate.getToSymbol()   +  " : " +
                String.valueOf(cryptoRate.getExRate()) + "]");
            */
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        if (selectedItem == null)
            selectedItem = converterItems.get(0);

        computeValues();
        converterListAdapter.notifyDataSetChanged();
    }

    public void notifySetDataChanged(){

        computeValues();
        converterListAdapter.notifyDataSetChanged();
    }


    /**
     * Update the values of the {@link ConverterItem} list.
     */
    public void computeValues() {
        if (cryptoRates.isEmpty())
            return;

        // Convert the value to universal currency.
        String valueStr  =   selectedItem.getValue();
        Double selectedValue  = Double.valueOf(valueStr);

        for (ConverterItem converterItem:converterItems) {

            Double newValue =selectedValue * converterItem.getCryptoRate().getExRate();
            newValue = Math.round(newValue * 100000000D)/100000000D;

            converterItem.setValue(String.valueOf(newValue));
        }
    }

    public void updateValues(){
        updateToSymbols();
        updateFromSymbol();

        getCryptoRatesCallback();
    }

    public void clearValues() {
        cryptoRates.clear();

        for (ConverterItem converterItem:converterItems) {
            converterItem.setValue(" - ");
            converterItem.setCryptoRate(null);
        }

        selectedItem.setValue("1.0");
        converterListAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnConvertItemClickListener {
        // TODO: Update argument type and name
        void onConvertItemClicked(ConverterItem converterItem);
    }
}
