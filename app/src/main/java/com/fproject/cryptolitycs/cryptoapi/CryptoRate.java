package com.fproject.cryptolitycs.cryptoapi;

/**
 * Created by chamu on 1/21/2018.
 */

public class CryptoRate {

    private String fromSymbol;
    private String toSymbol;
    private Double rate;

    public CryptoRate(String fromSymbol, String toSymbol, Double rate)  {
        this.rate       = rate;
        this.fromSymbol = fromSymbol;
        this.toSymbol   = toSymbol;
    }

    public String getFromSymbol() {
        return fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public Double getExRate() {
        return rate;
    }

}
