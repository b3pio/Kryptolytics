package com.fproject.cryptolytics.cryptoapi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chamu on 3/11/2018.
 */

public class TopCoinsParser {

    public static CryptoCoin getCryptoCoin(JSONObject jsonCoin) {
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
                imageUrl = "https://www.cryptocompare.com" + jsonCoin.getString("ImageUrl");
            }
            cryptoCoin = new CryptoCoin(name, imageUrl, symbol, coinName, fullName, algorithm, proofType, coinSupply, sortOrder);
        }
        catch (JSONException exception) {
                Log.d("TEST", "getAsCryptoCoins(): " +  exception.toString());
        }

        return cryptoCoin;
    }
}
