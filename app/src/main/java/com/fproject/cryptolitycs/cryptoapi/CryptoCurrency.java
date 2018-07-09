package com.fproject.cryptolitycs.cryptoapi;


import java.util.Comparator;

public class CryptoCurrency {

    private String fromSymbol;
    private String toSymbol;

    private String market;
    private String price;
    private String lastUpdate;

    private String open;
    private String high;
    private String low;

    private String changeValue;
    private String changePercent;

    private String supply;
    private String volume;
    private String marketCap;

    public CryptoCurrency(){

    }

    public CryptoCurrency(String fromSymbol,
                          String toSymbol,
                          String market,
                          String price,
                          String lastUpdate,
                          String open,
                          String high,
                          String low,
                          String changeValue,
                          String changePercent,
                          String supply,
                          String volume,
                          String marketCap){

        this.fromSymbol = fromSymbol;
        this.toSymbol = toSymbol;
        this.market = market;
        this.price = price;
        this.lastUpdate = lastUpdate;

        this.open = open;
        this.high = high;
        this.low = low;

        this.changeValue = changeValue;
        this.changePercent = changePercent;

        this.supply = supply;
        this.volume = volume;
        this.marketCap = marketCap;
    }

    public String getFromSymbol() {
        return fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public String getMarket() {
        return market;
    }

    public String getPrice() {
        return price;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getOpen() {
        return open;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getChangeValue() {
        if (isChangePositive()) {
            return  "+" + changePercent;
        }

        return changeValue;
    }

    public String getChangePercent() {
        if (isChangePositive()) {
            return "+" + changePercent;
        }

        return changePercent;
    }

    public boolean isChangePositive(){
        if (changePercent.charAt(0) == '-'){
            return false;
        }

        return  true;
    }

    public String getSupply(){
        return supply;
    }

    public String getVolume() {
        return volume;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public static Comparator<CryptoCurrency> PriceComparator = new Comparator<CryptoCurrency>() {

        public int compare(CryptoCurrency cryptoCurrency1, CryptoCurrency cryptoCurrency2) {
            Double price1 = Double.valueOf(cryptoCurrency1.getPrice());
            Double price2 = Double.valueOf(cryptoCurrency2.getPrice());

            //descending order
            return price2.compareTo(price1);
        }
    };

}
