package com.fproject.cryptolitycs.cryptoapi;

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
    private JSONObject cryptoObject;
    private JSONArray  cryptoArray;

    // --------------------------------------------------------------------------------------------
    //region  Constructor
    // --------------------------------------------------------------------------------------------
    public CryptoData(JSONObject cryptoData) {
        this.cryptoObject = cryptoData;
    }

    public CryptoData(JSONArray cryptoArray) {
        this.cryptoArray = cryptoArray;
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
    public List<CryptoCoin> asTopCoins(){
        List<CryptoCoin> topCoins = new ArrayList<>();

        try {
            JSONArray jsonData = cryptoObject.getJSONArray("Data");

            for (Integer index = 0; index < jsonData.length(); index ++)   {

                JSONObject jsonIndex  = jsonData.getJSONObject(index);
                CryptoCoin cryptoCoin = asTopCryptoCoin(jsonIndex, index);

                topCoins.add(cryptoCoin);
            }
        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "asTopCoins(): " +  ex.toString());
        }

        Collections.sort(topCoins,CryptoCoin.SortOrderComparator);

        return topCoins;
    }

    /**
     * Parses the {@link CryptoData} as a {@link CryptoCoin} collection.
     */
    public Map<String,CryptoCoin> asCryptoCoins() {
        Map<String,CryptoCoin> cryptoCoinList = new HashMap<String,CryptoCoin>();

        try {
            JSONObject jsonCoins  = cryptoObject.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            // Parse each coin.
            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                CryptoCoin cryptoCoin = asCryptoCoin(jsonCoin);
                cryptoCoinList.put(cryptoCoin.getSymbol(), cryptoCoin);
            }
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "asCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoinList;
    }

    /**
     * Parses the {@link CryptoData} as a {@link CryptoRate} collection.
     */
    public Map<String,CryptoRate> asCryptoRates(){
        Map<String,CryptoRate> cryptoRates = new HashMap();

        try {
            String fromSymbol = cryptoObject.keys().next();
            JSONObject jsonFromSymbol = cryptoObject.getJSONObject(fromSymbol);
            Iterator<String> keys = jsonFromSymbol.keys();

            while (keys.hasNext()) {

                String toSymbol = keys.next();
                String rateStr = jsonFromSymbol.getString(toSymbol);
                Double exRate = Double.valueOf(rateStr);

                cryptoRates.put(toSymbol, new CryptoRate(fromSymbol, toSymbol, exRate));
            }
        }
        catch (JSONException exception) {
                Log.d(MODULE_TAG, "asCryptoRates(): " + exception.toString());
        }

        return cryptoRates;
    }

    /**
     * Parses the {@link CryptoData} as a {@link CryptoCurrency}.
     */
    public CryptoCurrency asCryptoCurrency() {
        CryptoCurrency cryptoCurrency = null;

        try {
            JSONObject jsonRaw = cryptoObject.getJSONObject("RAW");

            String fromSymbol = jsonRaw.keys().next();
            JSONObject jsonFromSymbol = jsonRaw.getJSONObject(fromSymbol);

            String toSymbol = jsonFromSymbol.keys().next();
            JSONObject jsonToSymbol = jsonFromSymbol.getJSONObject(toSymbol);

            JSONObject jsonDisplay = cryptoObject.getJSONObject("DISPLAY").getJSONObject(fromSymbol).getJSONObject(toSymbol);

            String price = jsonDisplay.getString("PRICE");
            price = cleanString(price);

            String supply = jsonDisplay.getString("SUPPLY");
            supply = cleanString(supply);

            String marketCap = jsonDisplay.getString("MKTCAP");
            marketCap = cleanString(marketCap);

            String change= jsonDisplay.getString("CHANGEDAY");
            change = cleanString(change);

            String changePct    = jsonDisplay.getString("CHANGEPCTDAY");
            String market       = jsonDisplay.getString("MARKET");
            String lastUpdate   = jsonDisplay.getString("LASTUPDATE");

            // This values could be missing
            String open     = getValueFromJSONObject("OPENDAY",jsonDisplay);
            String high     = getValueFromJSONObject("HIGHDAY",jsonDisplay);
            String low      = getValueFromJSONObject("LOWDAY",jsonDisplay);
            String volume   = getValueFromJSONObject("VOLUMDAY",jsonDisplay);

            cryptoCurrency = new CryptoCurrency(fromSymbol, toSymbol, market, price,
                    lastUpdate, open, high, low, change, changePct,supply, volume, marketCap);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "asCryptoCurrency(): " +  exception.toString());
        }

        return cryptoCurrency;
    }

    /**
     * Parses the {@link CryptoData} as top {@link CryptoCoin} collection. (Top 10 List)
     */
    public List<CryptoHistoryPoint> asCryptoHistoryPoints(){
        List<CryptoHistoryPoint> cryptoHistoryPoints = new ArrayList<>();

        try {
            JSONArray jsonData = cryptoObject.getJSONArray("Data");

            for (Integer index = 0; index < jsonData.length(); index ++)   {

                JSONObject jsonIndex  = jsonData.getJSONObject(index);
                CryptoHistoryPoint cryptoHistoryPoint = asCryptoHistoryPoint(jsonIndex,index);

                cryptoHistoryPoints.add(cryptoHistoryPoint);
            }
        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "asCryptoHistoryPoints(): " +  ex.toString());
        }

        Collections.sort(cryptoHistoryPoints, CryptoHistoryPoint.SortOrderComparator);

        return cryptoHistoryPoints;
    }

    /**
     * Parses the {@link CryptoData} as a {@link CryptoNewsArticle} collection.
     */
    public List<CryptoNewsArticle> asCryptoNewsArticles(){
        List<CryptoNewsArticle> cryptoNewsArticles = new ArrayList<>();

        try {

            for (Integer index = 0; index < cryptoArray.length(); index ++)   {

                JSONObject jsonIndex  = cryptoArray.getJSONObject(index);

                String title     = jsonIndex.getString("title");
                String body      = jsonIndex.getString("body");
                String url       = jsonIndex.getString("url");
                String imageUrl  = jsonIndex.getString("imageurl");
                String source    = jsonIndex.getString("source");
                Long   date      = jsonIndex.getLong("published_on");

                CryptoNewsArticle cryptoNewsArticle = new CryptoNewsArticle(title, body, url, imageUrl, source, date, index);
                cryptoNewsArticles.add(cryptoNewsArticle);
            }

        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "asCryptoNewsArticles(): " +  ex.toString());
        }

        Collections.sort(cryptoNewsArticles, CryptoNewsArticle.SortOrderComparator);

        return cryptoNewsArticles;
    }


    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region Error Parsing Methods
    // --------------------------------------------------------------------------------------------

    /**
     * Parses the {@link CryptoData} as a {@link String} message.
     */
    public String asCryptoMessage(){
        String message = null;

        try {

            message = cryptoObject.getString("Message");

        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "asCryptoMessage(): " +  exception.toString());
        }

        return message;
    }

    /**
     * Determine whether the {@link CryptoData} is an error message
     */
    public boolean isErrorMessage(){
        String response = new String();

        try {

            if (cryptoObject.has("Response")) {
                response = cryptoObject.getString("Response");
            }

        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "asCryptoMessage(): " +  exception.toString());
        }

        return response.contains("Error");
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
    private CryptoCoin asCryptoCoin(JSONObject jsonCoin) {
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
            Log.d(MODULE_TAG, "asCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoin;
    }

    /**
     * Parses a {@link CryptoCoin} from the specified {@link JSONObject}.
     */
    private CryptoCoin asTopCryptoCoin(JSONObject jsonObject, Integer sortOrder) {
        CryptoCoin cryptoCoin = null;

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
            Log.d(MODULE_TAG, "asTopCryptoCoin(): " +  exception.toString());
        }

        return cryptoCoin;
    }

    /**
     * Parses a {@link CryptoHistoryPoint} from the specified {@link JSONObject}.
     */
    private CryptoHistoryPoint asCryptoHistoryPoint(JSONObject jsonObject, Integer sortOrder) {
        CryptoHistoryPoint cryptoHistoryPoint = null;

        try {

            String timeStr = jsonObject.getString("time");
            float time = Float.valueOf(timeStr);

            String closeStr = jsonObject.getString("close");
            float close = Float.valueOf(closeStr);

            String highStr = jsonObject.getString("high");
            float high = Float.valueOf(highStr);

            String lowStr = jsonObject.getString("low");
            float low = Float.valueOf(lowStr);

            String openStr = jsonObject.getString("open");
            float open = Float.valueOf(openStr);

            String volumeFromStr = jsonObject.getString("volumefrom");
            float volumeFrom = Float.valueOf(volumeFromStr);

            String volumeToStr = jsonObject.getString("volumeto");
            float volumeTo = Float.valueOf(volumeToStr);

            cryptoHistoryPoint = new CryptoHistoryPoint(time, close, high, low, open,
                    volumeFrom, volumeTo, sortOrder);

        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "asCryptoHistoryPoint(): " +  exception.toString());
        }

        return cryptoHistoryPoint;
    }

    /**
     * Tries to extract the specified value from the string if it exists,
     * otherwise returns zero;
     */
    private String getValueFromJSONObject(String key, JSONObject jsonObject) throws JSONException {
        String value = new String("0");

        if (jsonObject.has(key)) {
            value = jsonObject.getString(key);
            value = cleanString(value);
        }

        return value;
    }

    /**
     * Clean unnecessary symbols from the string.
     */
    private String cleanString(String string) {

        if ((string !=null) && (!string.isEmpty())) {
            string = string.substring(string.indexOf(" ")).trim();
        }

        return string;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}
