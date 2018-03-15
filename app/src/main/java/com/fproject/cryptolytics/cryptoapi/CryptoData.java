package com.fproject.cryptolytics.cryptoapi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class for the JSON Object received form CryptoCompare.com, also provides
 * methods for parsing the data.
 */
public class CryptoData {
    private static final String MODULE_TAG = "CryptoData";
    private static final String IMAGE_URL  = "https://www.cryptocompare.com";

    // The JSON that contains the actual data; that will be parsed.
    private JSONObject   cryptoData;

    // Info about the data contained in the JSON
    private String       fromSymbol = null;
    private String       toSymbol   = null;
    private List<String> toSymbols  = null;

    // --------------------------------------------------------------------------------------------
    //region  Constructor
    // --------------------------------------------------------------------------------------------
    public CryptoData(JSONObject cryptoData) {
        this.cryptoData = cryptoData;
    }

    public CryptoData(JSONObject cryptoData, String fromSymbol, String toSymbol){
        this.cryptoData = cryptoData;
        this.fromSymbol = fromSymbol;
        this.toSymbol   = toSymbol;
    }

    public CryptoData(JSONObject cryptoData, String fromSymbol, List<String> toSymbols) {
        this.cryptoData  = cryptoData;
        this.fromSymbol  = fromSymbol;
        this.toSymbols   = toSymbols;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region  Parsing Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Parses the {@link CryptoData} as top {@link CryptoCoin} collection. (Top 10 List)
     */
    public List<CryptoCoin> getAsTopCoins(){
        List<CryptoCoin> topCoins = new ArrayList<>();

        try {
            JSONObject jsonCoins = cryptoData.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                CryptoCoin cryptoCoin = getCryptoCoin(jsonCoin);

                // We only need the Top 10 coins; ignore the rest of them.
                if (cryptoCoin.getSortOrder() < 20){
                    topCoins.add(cryptoCoin);
                }
           }
        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "getAsTopCoins(): " +  ex.toString());
        }

        Collections.sort(topCoins,CryptoCoin.SortOrderComparator);

        return topCoins;
    }

    /**
     * Parses the {@link CryptoData} as {@link CryptoCoin} collection.
     */
    public Map<String,CryptoCoin> getAsCryptoCoins() {
        Map<String,CryptoCoin> cryptoCoinList = new HashMap<String,CryptoCoin>();

        try {
            JSONObject jsonCoins  = cryptoData.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            // Parse each coin.
            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                CryptoCoin cryptoCoin = getCryptoCoin(jsonCoin);
                cryptoCoinList.put(cryptoCoin.getSymbol(), cryptoCoin);
            }
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoinList;
    }

    /**
     * Parses the {@link CryptoData} as a {@link CryptoRate}.
     */
    public CryptoRate getAsCryptoRate(){
        CryptoRate cryptoRate = null;

        if ((fromSymbol == null) || (toSymbol == null)) {
            return cryptoRate;
        }

        try {
            String rateStr = cryptoData.getString(toSymbol);
            Double exRate = Double.valueOf(rateStr);

            cryptoRate = new CryptoRate(fromSymbol, toSymbol, exRate);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoRate(): " +  exception.toString());
        }

        return cryptoRate;
    }

    /**
     * Parses the {@link CryptoData} as {@link CryptoRate} collection.
     */
    public Map<String,CryptoRate> getAsCryptoRates(){
        Map<String,CryptoRate> cryptoRates = new HashMap();

        if ((fromSymbol == null) || (toSymbols == null)) {
            return cryptoRates;
        }

        for (String toSymbol:toSymbols) {
            try {
                String rateStr = cryptoData.getString(toSymbol);
                Double exRate = Double.valueOf(rateStr);

                cryptoRates.put(toSymbol, new CryptoRate(fromSymbol, toSymbol, exRate));

            } catch (JSONException exception) {
                Log.d(MODULE_TAG, "getAsCryptoRates(): " + exception.toString());
            }
        }

        return cryptoRates;
    }

    /**
     * Parses the {@link CryptoData} as a  {@link CryptoCurrency}.
     */
    public CryptoCurrency getAsCryptoCurrency() {
        CryptoCurrency cryptoCurrency = null;

        try {
            JSONObject jsonRaw = cryptoData.getJSONObject("RAW");

            String fromSymbol = jsonRaw.keys().next();
            JSONObject jsonFromSymbol = jsonRaw.getJSONObject(fromSymbol);

            String toSymbol = jsonFromSymbol.keys().next();
            JSONObject jsonToSymbol = jsonFromSymbol.getJSONObject(toSymbol);

            JSONObject jsonDisplay = cryptoData.getJSONObject("DISPLAY").getJSONObject(fromSymbol).getJSONObject(toSymbol);

            String market     = jsonDisplay.getString("MARKET");
            String lastUpdate = jsonDisplay.getString("LASTUPDATE");

            String price = jsonDisplay.getString("PRICE");
            price  = price.substring(price.indexOf(" ")).trim();

            String open = jsonDisplay.getString("OPENDAY");
            open = open.substring(open.indexOf(" ")).trim();

            String high = jsonDisplay.getString("HIGHDAY");
            high  = high.substring(high.indexOf(" ")).trim();

            String low  = jsonDisplay.getString("LOWDAY");
            low  = low.substring(low.indexOf(" ")).trim();

            String supply = jsonDisplay.getString("SUPPLY");
            supply  = supply.substring(supply.indexOf(" ")).trim();

            String volume  = jsonDisplay.getString("VOLUMEDAY");
            volume  = volume.substring(volume.indexOf(" ")).trim();

            String marketCap = jsonDisplay.getString("MKTCAP");
            marketCap = marketCap.substring(marketCap.indexOf(" ")).trim();

            String changePct = jsonDisplay.getString("CHANGEPCTDAY");

            String change     = jsonDisplay.getString("CHANGEDAY");
            change  = change.substring(change.indexOf(" ")).trim();

            cryptoCurrency = new CryptoCurrency(fromSymbol, toSymbol, market, price,
                    lastUpdate, open, high, low, change, changePct,supply, volume, marketCap);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoCurrency(): " +  exception.toString());
        }

        return cryptoCurrency;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region  Private Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Parses a {@link CryptoCoin} from the specified {@link JSONObject}.
     */
    private CryptoCoin getCryptoCoin(JSONObject jsonCoin) {
        CryptoCoin cryptoCoin = null;

        try {

            String name         = jsonCoin.getString("Name");
            String symbol       = jsonCoin.getString("Symbol");
            String coinName     = jsonCoin.getString("CoinName");
            String fullName     = jsonCoin.getString("FullName");
            String algorithm    = jsonCoin.getString("Algorithm");
            String proofType    = jsonCoin.getString("ProofType");
            String coinSupply   = jsonCoin.getString("TotalCoinSupply");
            Integer sortOrder   = Integer.valueOf(jsonCoin.getString("SortOrder"));
            String imageUrl     = null;

            if (jsonCoin.has("ImageUrl")) {
                imageUrl = IMAGE_URL + jsonCoin.getString("ImageUrl");
            }

            cryptoCoin = new CryptoCoin(name, imageUrl, symbol, coinName, fullName,
                    algorithm, proofType, coinSupply, sortOrder);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoin;
    }
    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
