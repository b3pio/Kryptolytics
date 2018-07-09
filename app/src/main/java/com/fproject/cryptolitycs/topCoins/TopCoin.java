package com.fproject.cryptolitycs.topCoins;

import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.cryptoapi.CryptoCurrency;

/**
 * Represents an item of the of the TopCoins
 */
public class TopCoin {
    private long sortOrder;

    // The CryptCoin Symbol ex (ETH, BTC)
    private String fromSymbol;

    // The currency to convert to (EUR, USD)
    private String toSymbol;

    private CryptoCoin cryptoCoin;
    private CryptoCurrency cryptoCurrency;

    public TopCoin() {
        // do nothing
    }

    public TopCoin(long itemId, String fromSymbol, String toSymbol) {
        this.sortOrder = itemId;
        this.fromSymbol = fromSymbol;
        this.toSymbol = toSymbol;
    }

    public long getSortOrder() {
        return sortOrder;
    }

    public void setFromSymbol(String fromSymbol) {
        this.fromSymbol = fromSymbol;
    }

    public String getFromSymbol() {
        return fromSymbol;
    }

    public void setToSymbol(String toSymbol) {
        this.toSymbol = toSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public void setCryptoCoin(CryptoCoin cryptoCoin) {
        this.cryptoCoin = cryptoCoin;
    }

    public CryptoCoin getCryptoCoin() {
        return cryptoCoin;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public  String getPriceStr() {
        if ((cryptoCurrency == null) || (cryptoCoin == null))
                return  null;

        return cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol();
    }

    public  String getChangePercentStr(){
        if ((cryptoCurrency == null) || (cryptoCoin == null))
            return  null;

        return cryptoCurrency.getChangePercent() + "%";
    }

    public String toString() {
        return String.valueOf(sortOrder) + " - " + fromSymbol + " - " + toSymbol;
    }

}
