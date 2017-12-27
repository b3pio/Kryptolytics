package com.fproject.cryptolytics.cryptoapi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class CryptoData {
    private static final String TAG = "CryptoData";
    private JSONObject cryptoData;

    public CryptoData(JSONObject cryptoData){
        this.cryptoData = cryptoData;
    }

    public Map<String,CryptoCoin> getAsCryptoCoins() {
        Map<String,CryptoCoin> cryptoCoinList = new HashMap<String,CryptoCoin>();

        try {

            JSONObject jsonCoins  = cryptoData.getJSONObject("Data");
            Iterator<String> keys = jsonCoins.keys();

            while (keys.hasNext()) {
                JSONObject jsonCoin = jsonCoins.getJSONObject(keys.next());

                String name      = jsonCoin.getString("Name");
                String symbol    = jsonCoin.getString("Symbol");
                String coinName  = jsonCoin.getString("CoinName");
                String fullName  = jsonCoin.getString("FullName");
                String algorithm = jsonCoin.getString("Algorithm");
                String proofType = jsonCoin.getString("ProofType");
                String totalCoinSupply = jsonCoin.getString("TotalCoinSupply");
                String imageUrl  = null;

                if (jsonCoin.has("ImageUrl")) {
                    imageUrl = "https://www.cryptocompare.com" + jsonCoin.getString("ImageUrl");
                }

                CryptoCoin cryptoCoin = new CryptoCoin(name,imageUrl,symbol,coinName,fullName,algorithm,proofType,totalCoinSupply);
                cryptoCoinList.put(symbol,cryptoCoin);
            }
        }
        catch (JSONException exception) {
            Log.d(TAG, "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoinList;
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
            Log.d(TAG, "getCryptoCurrencyFromJson: " +  exception.toString());
        }

        return cryptoCurrency;
    }
}
