package com.fproject.cryptolytics.cryptoapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fproject.cryptolytics.utility.GsonUtility;

import org.json.JSONObject;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Responsable for fethcing information about the crypto currencies from: www.cryptocompare.com
 *
 * @author lszathmary
 */
public class CryptoClient {
    private final static String TAG         = "[CryptoClient]";
    private final static String DATA_SERVER = "https://min-api.cryptocompare.com/data";
    private final static String IMG_SERVER  = "https://www.cryptocompare.com";

    private RequestQueue requestQueue;

    // Context for accessing the application assets and resources.
    private Context context;

    /**
     * Initializes a new instance of the {@link CryptoClient} class.
     */
    public CryptoClient(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Requests available {@link CryptoCoin} list.
     */
    public void getCryptoCoins(CryptoCallback callback) {
        String url  = DATA_SERVER + "/all/coinlist";
        String file = Uri.encode(url);

        // The list does not change very often so we can afford to cache it.

        if (isFileCached(file)) {
            Log.d(TAG, "Loading Coins from Cache.");
            getCryptoCoinsFromCache(file, url, callback);
        }
        else {
            Log.d(TAG, "Loading Coins from Server.");
            getCryptoCoinsFromServer(file, url, callback);
        }

    }

    /**
     * Obtains {@link CryptoCoin} list from the cache.
     */
    private void getCryptoCoinsFromCache(String file, String url, CryptoCallback callback) {
        JSONObject response = GsonUtility.fromFile(context, file);

        if (response != null) {

            callback.onSuccess(new CryptoData(response));

        } else {

            callback.onFailure("Could not load from disk!");
        }
    }

    /**
     * Obtains {@link CryptoCoin} list from the server.
     */
    private void getCryptoCoinsFromServer(String file, String url, CryptoCallback callback){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {

                    GsonUtility.toFile(context, response, file);
                    callback.onSuccess(new CryptoData(response));

                },
                (VolleyError error)   -> {
                    callback.onFailure(error.getMessage());
                }
        );

        requestQueue.add(request);
    }

    /**
     *
     */
    public void getCrytpoCurrency(String fromSymbol, String toSymbol, CryptoCallback callback) {
        String url = DATA_SERVER + "/pricemultifull?fsyms="+ fromSymbol + "&tsyms=" + toSymbol;

        Log.d(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> callback.onSuccess(new CryptoData(response)),
                (VolleyError error)   -> callback.onFailure(error.getMessage())
        );

        requestQueue.add(request);

    }

    /**
     * Returns true if the file exists and it was created today, otherwise returns false.
     */
    private boolean isFileCached(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        Log.d("FILENAME", file.getAbsolutePath());

        if (file.exists()) {
            Date today     = new Date();
            Date fileDate  = new Date(file.lastModified());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);

            int todaysDays = calendar.get(Calendar.DAY_OF_MONTH);
            int todaysMonth = calendar.get(Calendar.MONTH);
            int todaysYear  = calendar.get(Calendar.YEAR);

            calendar.setTime(fileDate);
            int filesDay = calendar.get(Calendar.DAY_OF_MONTH);
            int filesMonth = calendar.get(Calendar.MONTH);
            int filesYear  = calendar.get(Calendar.YEAR);

            if ((todaysDays == filesDay) && (todaysMonth == filesMonth) && (todaysYear == filesYear)) {
                return  true;
            }
        }

        return false;
    }


    /*
    public Map<String,CryptoCoin> getCryptoCoins(){
        JSONObject response = null;
        String url = DATA_SERVER + "/all/coinlist";
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectRequest request = new JsonObjectRequest(url,null,requestFuture,requestFuture);
        requestQueue.add(request);


        try {
            response = requestFuture.get(5, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
        } catch (InterruptedException e) {
            Log.d(TAG,"interrupted");
        } catch (ExecutionException e) {
            Log.d(TAG,"execution");
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d(TAG,response.toString());

        return new CryptoData(response).getAsCryptoCoins();
    }
     */

}
