package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Use the {@link ConvertListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertListFragment extends Fragment {
    private final static String MODULE_TAG = "[ConvertListFragment]";

    // The universal exchange to use for converting/to different currencies.
    private final static String UEX_RATE = "EUR";

    // These provide data about the watched items.
    private CryptoClient    cryptoClient       = null;
    private DatabaseManager databaseManager     = null;

    // List of converter items.
    private List<ConverterItem> converterItems = null;

    private ConverterItem   newConverterItem     = null;
    private ConverterItem   selectedItem = null;
    private ConverterListAdapter converterListAdapter = null;

    private Map<String,CryptoCoin>  cryptoCoins     = null;
    private Map<Long, CryptoRate>   fromCryptoRates = null;
    private Map<Long, CryptoRate>   toCryptoRates   = null;

    // Listener that need to be notified
    private OnConvertItemClickListener convertItemClickListener;

    private ConverterValueUpdater converterValueUpdater = null;

    public ConvertListFragment() {
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
                if (convertItemClickListener != null) {

                    selectedItem = converterListAdapter.getConveterItem(position);
                    selectedItem.setValue("1.0");
                    converterValueUpdater.setSelectedItem(selectedItem);

                    notifySetDataChanged();


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

            converterItems.add(new ConverterItem(0, "BTC", "0"));
            //converterItems.add(new ConverterItem(1, "ETH", "1"));
            //converterItems.add(new ConverterItem(2, "XRP", "2"));
            //converterItems.add(new ConverterItem(3, "ETL", "3"));
            converterItems.add(new ConverterItem(4, "BTC", "0"));

            ListView listView = getView().findViewById(R.id.lv_converter);
            converterListAdapter = new ConverterListAdapter(getContext(), converterItems);
            listView.setAdapter(converterListAdapter);

            converterValueUpdater = new ConverterValueUpdater(converterItems);
        }

        cryptoCoins     = new HashMap<String,CryptoCoin>();
        fromCryptoRates = new HashMap<Long, CryptoRate>();
        toCryptoRates   = new HashMap<Long, CryptoRate>();

        getCryptoCoinsCallback();
        getCryptoRatesCallback();
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
            }

            @Override
            public void onFailure(String cryptoError) {

            }
        });
    }

    /**
     * Obtain the {@link CryptoRate} data.
     */
    private void getCryptoRatesCallback(){
        for(ConverterItem convertItem : converterItems) {
            //
            // toCryptoRates
            //
            cryptoClient.getCrytpoRate(convertItem.getSymbol(), UEX_RATE, new CryptoCallback() {
                @Override
                public void onSuccess(CryptoData cryptoData) {

                    toCryptoRates.put(convertItem.getItemId(), cryptoData.getAsCryptoRate());
                    onCryptoDataRecevied();
                }

                @Override
                public void onFailure(String cryptoError) {

                }
            });
            //
            // fromCryptoRates
            //
            cryptoClient.getCrytpoRate(UEX_RATE, convertItem.getSymbol(), new CryptoCallback() {
                @Override
                public void onSuccess(CryptoData cryptoData) {

                    fromCryptoRates.put(convertItem.getItemId(), cryptoData.getAsCryptoRate());
                    onCryptoDataRecevied();
                }

                @Override
                public void onFailure(String cryptoError) {

                }
            });
        }
    }

    /**
     * Determines weather all the requested data has arrived and updates the ConvertList.
     */
    private void onCryptoDataRecevied(){
        if (converterItems.size() != toCryptoRates.size())    return;
        if (converterItems.size() != fromCryptoRates.size())  return;
        if (cryptoCoins.size() == 0) return;

        for (ConverterItem item:converterItems) {

            CryptoCoin cryptoCoin = cryptoCoins.get(item.getSymbol());
            item.setCrpytoCoin(cryptoCoin);

            CryptoRate toCryptoRate = toCryptoRates.get(item.getItemId());
            item.setToCryptoRate(toCryptoRate);

            CryptoRate fromCryptoRate = fromCryptoRates.get(item.getItemId());
            item.setFromCryptoRate(fromCryptoRate);

            Log.d(MODULE_TAG, "[" + item.getSymbol() + "]" +
                " [FROM: " +

                fromCryptoRate.getFromSymbol() +  " -> " +
                fromCryptoRate.getToSymbol()   +  " - " +
                String.valueOf(fromCryptoRate.getExRate()) + "]" +

                " [TO: " +
                toCryptoRate.getFromSymbol() + " -> " +
                toCryptoRate.getToSymbol() + " - "  +
                 String.valueOf(toCryptoRate.getExRate()) + "]");
        }

        converterListAdapter.notifyDataSetChanged();
    }

    public void notifySetDataChanged(){

        converterValueUpdater.updateValues();
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
