package com.fproject.cryptolytics.converter;

import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.cryptoapi.CryptoRate;

/**
 * Represents an item of the of the Converter.
 */
public class ConverterItem {
    private long itemId;

    private String symbol;
    private String value;

    private CryptoCoin crpytoCoin;

    private CryptoRate fromCryptoRate;
    private CryptoRate toCryptoRate;


    public ConverterItem(long itemId, String symbol, String value) {
        this.itemId = itemId;
        this.symbol = symbol;
        this.value = value;
    }

    public long getItemId(){
        return this.itemId;
    }

    public String getSymbol() {
        return symbol;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public CryptoRate getFromCryptoRate() {
        return fromCryptoRate;
    }

    public void setFromCryptoRate(CryptoRate fromCryptoRate) {
        this.fromCryptoRate = fromCryptoRate;
    }

    public CryptoRate getToCryptoRate() {
        return toCryptoRate;
    }

    public void setToCryptoRate(CryptoRate toCryptoRate) {
        this.toCryptoRate = toCryptoRate;
    }

    public CryptoCoin getCrpytoCoin() {
        return crpytoCoin;
    }

    public void setCrpytoCoin(CryptoCoin crpytoCoin) {
        this.crpytoCoin = crpytoCoin;
    }

    public boolean isLoaded(){
        if (toCryptoRate == null || fromCryptoRate == null || crpytoCoin == null)
            return false;

        return true;
    }


}
