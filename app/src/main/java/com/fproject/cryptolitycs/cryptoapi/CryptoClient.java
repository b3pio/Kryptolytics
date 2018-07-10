package com.fproject.cryptolitycs.cryptoapi;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fproject.cryptolitycs.utility.FileUtility;
import com.fproject.cryptolitycs.utility.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Responsable for fethcing information about the crypto currencies from: www.cryptocompare.com
 *
 * @author lszathmary
 */
public class CryptoClient {
    private final static String EXTRA_PARAM     = "Cryptolitycs";
    private final static String MODULE_TAG      = "[CryptoClient]";
    public  final static String DATA_SERVER     = "https://min-api.cryptocompare.com/data";
    public  final static String IMAGE_SERVER    = "https://www.cryptocompare.com";

    private RequestQueue requestQueue;

    // Context for accessing the application assets and resources.
    private Context context;

    // --------------------------------------------------------------------------------------------
    //region  Constructor
    // --------------------------------------------------------------------------------------------

    /**
     * Initializes a new instance of the {@link CryptoClient} class.
     */
    public CryptoClient(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }
    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Public Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Requests the available {@link CryptoCoin} list.
     */
    public void getCryptoCoins(CryptoCallback callback) {
        String url  = DATA_SERVER + "/all/coinlist";

        getObject(url, FileUtility.CURRENT_DAY, callback);
    }

    /**
     * Requests the top {@link CryptoCoin} list.
     */
    public void getTopCryptoCoins(Integer count, String toSymbol, CryptoCallback callback) {
        String url = DATA_SERVER + "/top/totalvol?limit=" + count.toString()
                + "&tsym=" + toSymbol
                + "&extraParams=" + EXTRA_PARAM;

        getObject(url, FileUtility.CURRENT_DAY, callback);
    }

    /**
     * Requests the {@link CryptoCurrency}.
     */
    public void getCryptoCurrency(String fromSymbol, String toSymbol, CryptoCallback callback){
        String url = DATA_SERVER + "/pricemultifull?fsyms="+ fromSymbol
                + "&tsyms=" + toSymbol
                + "&extraParams=" + EXTRA_PARAM;

        getObject(url, FileUtility.CURRENT_TIME, callback);
    }

    /**
     * Requests the {@link CryptoRate} list.
     */
    public void getCryptoRates(String fromSymbol, List<String> toSymbols, CryptoCallback callback) {
        String url = DATA_SERVER + "/pricemulti?fsyms="
                + fromSymbol + "&tsyms="
                + fromSymbol + "," + TextUtils.join("," , toSymbols)
                + "&extraParams=" + EXTRA_PARAM;

        getObject(url, FileUtility.CURRENT_MINUTE, callback);
    }


    /**
     * Requests the {@link CryptoHistoryPoint} list.
     */
    public void getCryptoHistoryPoints(String fromSymbol, String toSymbol, CryptoCallback callback) {
        String url = DATA_SERVER + "/histoday?fsym="+ fromSymbol
                + "&tsym=" + toSymbol
                + "&limit=30"
                + "&extraParams=" + EXTRA_PARAM;

        getObject(url, FileUtility.CURRENT_HOUR, callback);
    }

    /**
     * Requests the {@link CryptoNewsArticle} list.
     */
    public void getCryptoNewsArticles(CryptoCallback callback) {
        String url = DATA_SERVER + "/news/?lang=EN" +  "&extraParams=" + EXTRA_PARAM;

        getArray(url, FileUtility.CURRENT_MINUTE, callback);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    private void getObject(String url, int period, CryptoCallback callback) {
        String file = Uri.encode(url);

        if (FileUtility.isFileCached(context, file, period)) {
            Log.d(MODULE_TAG, "getObjectFromCache() " + url);
            getObjectFromCache(file, callback);
        }
        else {
            Log.d(MODULE_TAG, "getObjectFromServer()" + url);
            getObjectFromServer(file, url, callback);
        }
    }

    private void getArray(String url, int period, CryptoCallback callback) {
        String file = Uri.encode(url);

        if (FileUtility.isFileCached(context, file, period)) {
            Log.d(MODULE_TAG, "getArrayFromCache() " + url);
            getArrayFromCache(file, callback);
        }
        else {
            Log.d(MODULE_TAG, "getArrayFromServer()" + url);
            getArrayFromServer(file, url, callback);
        }
    }

    public void getObjectFromCache(String file, CryptoCallback callback) {
        JSONObject response = JsonUtils.objectFromFile(context, file);

        if (response != null) {
            callback.onSuccess(new CryptoData(response));

        }
        else {
            callback.onFailure("Could not load from disk!");
        }
    }
    public void getArrayFromCache(String file, CryptoCallback callback) {
        JSONArray response = JsonUtils.arrayFromFile(context, file);

        if (response != null) {
            callback.onSuccess(new CryptoData(response));

        }
        else {
            callback.onFailure("Could not load from disk!");
        }
    }

    private void getObjectFromServer(String file, String url, CryptoCallback callback) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {

                    JsonUtils.objectToFile(context, response, file);
                    callback.onSuccess(new CryptoData(response));

                },
                (VolleyError error)  -> {

                    callback.onFailure(error.getMessage());
                }
        );

        requestQueue.add(request);
    }


    private void getArrayFromServer(String file, String url, CryptoCallback callback) {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                (JSONArray response) -> {

                    JsonUtils.arrayToFile(context, response, file);
                    callback.onSuccess(new CryptoData(response));

                },
                (VolleyError error)  -> {

                    callback.onFailure(error.getMessage());

                }
        );

        requestQueue.add(request);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
