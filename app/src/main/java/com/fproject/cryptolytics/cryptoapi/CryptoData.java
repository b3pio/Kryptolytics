package com.fproject.cryptolytics.cryptoapi;

import android.util.Log;

import org.json.JSONArray;
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

    // The JSON that contains the actual data; that will be parsed.
    private JSONObject   cryptoData;

    // --------------------------------------------------------------------------------------------
    //region  Constructor
    // --------------------------------------------------------------------------------------------
    public CryptoData(JSONObject cryptoData) {
        this.cryptoData = cryptoData;
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
            JSONArray jsonData = cryptoData.getJSONArray("Data");

            for (Integer index = 0; index < jsonData.length(); index ++)   {

                JSONObject jsonIndex  = jsonData.getJSONObject(index);
                CryptoCoin cryptoCoin = getTopCryptoCoin(jsonIndex, index);

                topCoins.add(cryptoCoin);
            }
        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "getAsTopCoinsByVolume(): " +  ex.toString());
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

    public Map<String,CryptoRate>  getAsCryptoRates(){
        Map<String,CryptoRate> cryptoRates = new HashMap();

        String fromSymbol = cryptoData.keys().next();
        Iterator<String> keys = cryptoData.keys();

        while (keys.hasNext()) {
            try {

                String toSymbol = keys.next();
                String rateStr  = cryptoData.getString(toSymbol);
                Double exRate   = Double.valueOf(rateStr);

                cryptoRates.put(toSymbol, new CryptoRate(fromSymbol, toSymbol, exRate));
            }
            catch (JSONException exception) {
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
                imageUrl = CryptoClient.IMAGE_SERVER + jsonCoin.getString("ImageUrl");
            }

            cryptoCoin = new CryptoCoin(name, imageUrl, symbol, coinName, fullName,
                    algorithm, proofType, coinSupply, sortOrder);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoin;
    }

    /**
     * Parses a {@link CryptoCoin} from the specified {@link JSONObject}.
     * @note
     */
    private CryptoCoin getTopCryptoCoin(JSONObject jsonObject, Integer sortOrder) {
        CryptoCoin cryptoCoin = null;

        Log.d("TESTING", jsonObject.toString());

        try {
            JSONObject jsonCoinInfo = jsonObject.getJSONObject("CoinInfo");

            String name         = jsonCoinInfo.getString("Name");
            String symbol       = jsonCoinInfo.getString("Name");
            String coinName     = jsonCoinInfo.getString("FullName");
            String algorithm    = jsonCoinInfo.getString("Algorithm");
            String proofType    = jsonCoinInfo.getString("ProofType");
            String coinSupply   = null;
            String imageUrl     = null;

            JSONObject jsonConversionInfo = jsonObject.getJSONObject("ConversionInfo");

            if (jsonConversionInfo != null) {
                coinSupply = jsonConversionInfo.getString("Supply");
            }

            if (jsonCoinInfo.has("ImageUrl")) {
                imageUrl = CryptoClient.IMAGE_SERVER + jsonCoinInfo.getString("ImageUrl");
            }

            cryptoCoin = new CryptoCoin(name, imageUrl, symbol, coinName, null,
                    algorithm, proofType, coinSupply, sortOrder);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getTopCryptoCoin(): " +  exception.toString());
        }

        return cryptoCoin;
    }
    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
