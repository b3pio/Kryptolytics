package com.fproject.cryptolytics.converter;

import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;

/**
 * Represents an item of the of the Converter.
 */
public class ConverterItem {
    private long itemId;

    private String symbol;
    private String value;

    private CryptoCurrency cryptoCurrency;

    public ConverterItem(long itemId, String symbol, String value) {
        this.symbol = symbol;
        this.value = value;
    }


    public long getItemId(){
        return  itemId;
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


    public  void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public CryptoCurrency getCryptoCurrency(){
        return cryptoCurrency;
    }


}
