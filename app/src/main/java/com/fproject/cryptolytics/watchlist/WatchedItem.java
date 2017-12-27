package com.fproject.cryptolytics.watchlist;

import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;

/**
 * Represents an item of the of the WatchList.
 */
public class WatchedItem {
    private long itemId;

    // The CryptCoin Symbol ex (ETH, BTC)
    private String  fromSymbol;

    // The currency to convert to (EUR, USD)
    private String  toSymbol;

    private CryptoCoin      cryptoCoin;
    private CryptoCurrency  cryptoCurrency;

    public WatchedItem(){
        // do nothing
    }

    public WatchedItem(long itemId, String fromSymbol, String toSymbol){
        this.itemId = itemId;
        this.fromSymbol = fromSymbol;
        this.toSymbol = toSymbol;
    }

    public  long getItemId(){
        return itemId;
    }


    public void setFromSymbol(String fromSymbol){
        this.fromSymbol = fromSymbol;
    }

    public String getFromSymbol(){
        return fromSymbol;
    }


    public void setToSymbol(String toSymbol){
        this.toSymbol = toSymbol;
    }

    public String getToSymbol(){
        return toSymbol;
    }


    public void  setCryptoCoin(CryptoCoin cryptoCoin){
        this.cryptoCoin = cryptoCoin;
    }

    public CryptoCoin getCryptoCoin(){
        return  cryptoCoin;
    }


    public  void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public CryptoCurrency getCryptoCurrency(){
        return cryptoCurrency;
    }


    public boolean isLoaded(){
        if ((cryptoCoin == null) || (cryptoCurrency == null))
            return  false;

        return true;
    }
}
