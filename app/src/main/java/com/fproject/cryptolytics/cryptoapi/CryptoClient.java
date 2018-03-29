package com.fproject.cryptolytics.cryptoapi;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fproject.cryptolytics.utility.FileUtility;
import com.fproject.cryptolytics.utility.JsonUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Responsable for fethcing information about the crypto currencies from: www.cryptocompare.com
 *
 * @author lszathmary
 */
public class CryptoClient {
    private final static String MODULE_TAG = "[CryptoClient]";
    public final static String DATA_SERVER  = "https://min-api.cryptocompare.com/data";
    public final static String IMAGE_SERVER = "https://www.cryptocompare.com";

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

        getResponse(url, FileUtility.CURRENT_DAY, callback);
    }

    /**
     * Requests the top {@link CryptoCoin} list.
     */
    public void getTopCrytpoCoins(Integer count, String toSymbol, CryptoCallback callback) {
        String url = DATA_SERVER + "/top/totalvol?limit=" + count.toString() + "&tsym=" + toSymbol;

        getResponse(url, FileUtility.CURRENT_DAY, callback);
    }

    /**
     * Requests the {@link CryptoCurrency}.
     */
    public void getCrytpoCurrency(String fromSymbol, String toSymbol, CryptoCallback callback){
        String url = DATA_SERVER + "/pricemultifull?fsyms="+ fromSymbol + "&tsyms=" + toSymbol;

        getResponse(url, FileUtility.CURRENT_MINUTE, callback);
    }

    /**
     * Requests the {@link CryptoRate} list.
     */
    public void getCrytpoRates(String fromSymbol, List<String> toSymbols, CryptoCallback callback) {
        String url = DATA_SERVER + "/pricemulti?fsyms="+ fromSymbol + "&tsyms="
                            + fromSymbol + "," + TextUtils.join("," , toSymbols);

        getResponse(url, FileUtility.CURRENT_MINUTE, callback);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Private Methods
    // --------------------------------------------------------------------------------------------

    private  void getResponse(String url, int period, CryptoCallback callback) {
        String file = Uri.encode(url);

        if (FileUtility.isFileCached(context, file, period)) {
            Log.d(MODULE_TAG, "getResponseFromCache() " + url);
            getResponseFromCache(file, callback);
        }
        else {
            Log.d(MODULE_TAG, "getResponseFromServer()" + url);
            getResponseFromServer(file, url, callback);
        }
    }

    public void getResponseFromCache(String file, CryptoCallback callback) {
        JSONObject response = JsonUtils.fromFile(context, file);

        if (response != null) {
            callback.onSuccess(new CryptoData(response));
        }
        else {
            callback.onFailure("Could not load from disk!");
        }
    }

    private void getResponseFromServer(String file, String url, CryptoCallback callback) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {

                    JsonUtils.toFile(context, response, file);
                    callback.onSuccess(new CryptoData(response));

                },
                (VolleyError error)  -> callback.onFailure(error.getMessage())
        );

        requestQueue.add(request);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
