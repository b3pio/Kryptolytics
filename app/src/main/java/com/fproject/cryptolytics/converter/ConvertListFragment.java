package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * {@link ConvertListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConvertListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertListFragment extends Fragment {


    // The universal exchange to use for converting/to different currencies.
    private final static String UEX_RATE = "EUR";

    // These provide data about the watched items.
    private CryptoClient cryptoClient       = null;
    private DatabaseManager databaseManager = null;

    // List of converter items.
    private List<ConverterItem> converterItems = null;

    private ConverterItem       newConverterItem    = null;
    private ConverterListAdapter converterListAdapter = null;

    private Map<String,CryptoCoin> cryptoCoins     = null;
    private Map<Long, CryptoRate>   fromCryptoRates = null;
    private Map<Long, CryptoRate>   toCryptoRates   = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConvertListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConvertListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvertListFragment newInstance(String param1, String param2) {
        ConvertListFragment fragment = new ConvertListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_convert_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        //
        //
        updateActivity();
    }

    private void updateActivity(){
        if (converterItems == null) {

            converterItems = new ArrayList<>();
            converterItems.add(new ConverterItem(0, "BTC", "0"));
            converterItems.add(new ConverterItem(1, "ETH", "0"));
            converterItems.add(new ConverterItem(2, "XRP", "0"));
            converterItems.add(new ConverterItem(3, "ETL", "0"));


            ListView listView = getView().findViewById(R.id.lv_converter);
            converterListAdapter = new ConverterListAdapter(getContext(), listView, converterItems);
            listView.setAdapter(converterListAdapter);
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
        }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
