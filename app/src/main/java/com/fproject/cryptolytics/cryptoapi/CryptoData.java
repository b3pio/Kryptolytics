package com.fproject.cryptolytics.cryptoapi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CryptoData {
    private static final String MODULE_TAG = "CryptoData";
    private JSONObject cryptoData;
    private String fromSymbol;
    private String toSymbol;
    private List<String> toSymbols;

    public CryptoData(JSONObject cryptoData){
        this.cryptoData = cryptoData;
    }

    public CryptoData(JSONObject cryptoData, String fromSymbol, List<String> toSymbols){
        this.cryptoData = cryptoData;
        this.fromSymbol = fromSymbol;
        this.toSymbols   = toSymbols;
    }

    public CryptoData(JSONObject cryptoData, String fromSymbol, String toSymbol){
        this.cryptoData = cryptoData;
        this.fromSymbol = fromSymbol;
        this.toSymbol   = toSymbol;
    }

    public List<CryptoCoin> getAsTopCoins(){
        List<CryptoCoin> topCoins = new ArrayList<>();

        try {

            JSONObject jsonCoins = cryptoData.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                String  name      = jsonCoin.getString("Name");
                String  symbol    = jsonCoin.getString("Symbol");
                String  coinName  = jsonCoin.getString("CoinName");
                String  fullName  = jsonCoin.getString("FullName");
                String  algorithm = jsonCoin.getString("Algorithm");
                String  proofType = jsonCoin.getString("ProofType");
                String  totalCoinSupply = jsonCoin.getString("TotalCoinSupply");
                Integer sortOrder = Integer.valueOf(jsonCoin.getString("SortOrder"));
            }

        }
        catch (JSONException ex) {
            Log.d(MODULE_TAG, "getAsCryptoCoins(): " +  ex.toString());
        }

        return topCoins;
    }

    public Map<String,CryptoCoin> getAsCryptoCoins() {
        Map<String,CryptoCoin> cryptoCoinList = new HashMap<String,CryptoCoin>();

        try {

            JSONObject jsonCoins  = cryptoData.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                String  name      = jsonCoin.getString("Name");
                String  symbol    = jsonCoin.getString("Symbol");
                String  coinName  = jsonCoin.getString("CoinName");
                String  fullName  = jsonCoin.getString("FullName");
                String  algorithm = jsonCoin.getString("Algorithm");
                String  proofType = jsonCoin.getString("ProofType");
                String  totalCoinSupply = jsonCoin.getString("TotalCoinSupply");

                Integer sortOrder = Integer.valueOf(jsonCoin.getString("SortOrder"));
                String  imageUrl  = null;

                if (jsonCoin.has("ImageUrl")) {
                    imageUrl = "https://www.cryptocompare.com" + jsonCoin.getString("ImageUrl");
                }

                CryptoCoin cryptoCoin = new CryptoCoin(name, imageUrl,symbol,coinName,fullName,algorithm,proofType,totalCoinSupply, sortOrder);
                cryptoCoinList.put(symbol,cryptoCoin);
            }
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoinList;
    }

    public CryptoRate getAsCryptoRate(){
        CryptoRate cryptoRate = null;

        if ((fromSymbol == null) || (toSymbol == null)) {
            Log.d(MODULE_TAG,"getAsCryptoRate() - null");
            return cryptoRate;
        }

        try {

            String rateStr = cryptoData.getString(toSymbol);
            Double rate = Double.valueOf(rateStr);

            Log.d(MODULE_TAG, toSymbol + " - "  + rateStr);

            cryptoRate = new CryptoRate(fromSymbol, toSymbol, rate);
        }
        catch (JSONException exception) {
            Log.d(MODULE_TAG, "getAsCryptoRate(): " +  exception.toString());
        }

        return cryptoRate;
    }

    public Map<String,CryptoRate> getAsCryptoRates(){
        Map<String,CryptoRate> cryptoRates = new HashMap();

        if ((fromSymbol == null) || (toSymbols == null)) {

            Log.d(MODULE_TAG,"getAsCryptoRates(): empty");
            return cryptoRates;
        }

        for (String toSymbol:toSymbols) {
            try {

                String rateStr = cryptoData.getString(toSymbol);
                Double rate = Double.valueOf(rateStr);

                cryptoRates.put(toSymbol, new CryptoRate(fromSymbol, toSymbol, rate));

            } catch (JSONException exception) {
                Log.d(MODULE_TAG, "getAsCryptoRate(): " + exception.toString());
            }
        }

        return cryptoRates;
    }

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
}
