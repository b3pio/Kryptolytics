package com.fproject.cryptolitycs.cryptoapi;

import java.util.Comparator;

/**
 * The values of a {@link CryptoCurrency} at one point in time.
 */
public class CryptoHistory {

    public float time;

    public float close;
    public float high;
    public float low;
    public float open;

    public float volumeFrom;
    public float volumeTo;

    private Integer sortOrder;

    public CryptoHistory(float time, float close, float high, float low, float open,
                         float volumeFrom, float volumeTo, Integer sortOrder) {

        this.time = time;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;

        this.volumeFrom = volumeFrom;
        this.volumeTo = volumeTo;

        this.sortOrder = sortOrder;
    }

    public float getTime() {
        return time;
    }


    public float getClose() {
        return close;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getOpen() {
        return open;
    }


    public float getVolumeFrom() {
        return volumeFrom;
    }

    public float getVolumeTo() {
        return volumeTo;
    }

    public int getSortOrder(){
        return sortOrder;
    }

    public static Comparator<CryptoHistory> SortOrderComparator = new Comparator<CryptoHistory>() {

        public int compare(CryptoHistory cryptoHistory1, CryptoHistory cryptoHistory2) {
            Integer sortOrder1 = cryptoHistory1.getSortOrder();
            Integer sortOrder2 = cryptoHistory2.getSortOrder();

            // Ascending order
            return sortOrder1.compareTo(sortOrder2);
        }
    };

}
