package com.fproject.cryptolitycs.converter;

import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.cryptoapi.CryptoRate;

/**
 * Represents an item of the of the Converter.
 */
public class ConverterItem {
    private long itemId;

    private String symbol;
    private String value;

    private CryptoCoin cryptoCoin;
    private CryptoRate cryptoRate;

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

    public String getValueStr(){
        if (value.equals(" - ")) {
            return value;
        }

        return  value + " " + symbol;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CryptoRate getCryptoRate() {
        return cryptoRate;
    }

    public void setCryptoRate(CryptoRate cryptoRate) {
        this.cryptoRate = cryptoRate;
    }

    public CryptoCoin getCryptoCoin() {
        return cryptoCoin;
    }

    public void setCryptoCoin(CryptoCoin cryptoCoin) {
        this.cryptoCoin = cryptoCoin;
    }

    public Double getExRate(){
        if (cryptoRate != null){
            return cryptoRate.getExRate();
        }

        return Double.NaN;
    }

    public boolean isLoaded(){
        if (cryptoRate == null || cryptoCoin == null)
            return false;

        return true;
    }

}
