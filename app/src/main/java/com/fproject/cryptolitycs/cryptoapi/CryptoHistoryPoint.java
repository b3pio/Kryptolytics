package com.fproject.cryptolitycs.cryptoapi;

import java.util.Comparator;

/**
 * The values of a {@link CryptoCurrency} at one point in time.
 */
public class CryptoHistoryPoint {

    public float time;

    public float close;
    public float high;
    public float low;
    public float open;

    public float volumeFrom;
    public float volumeTo;

    private Integer sortOrder;

    public CryptoHistoryPoint(float time, float close, float high, float low, float open,
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

    public static Comparator<CryptoHistoryPoint> SortOrderComparator = new Comparator<CryptoHistoryPoint>() {

        public int compare(CryptoHistoryPoint cryptoHistoryPoint1, CryptoHistoryPoint cryptoHistoryPoint2) {
            Integer sortOrder1 = cryptoHistoryPoint1.getSortOrder();
            Integer sortOrder2 = cryptoHistoryPoint2.getSortOrder();

            // Ascending order
            return sortOrder1.compareTo(sortOrder2);
        }
    };

}
