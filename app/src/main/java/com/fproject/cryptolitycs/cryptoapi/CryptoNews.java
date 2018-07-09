package com.fproject.cryptolitycs.cryptoapi;

import java.util.Comparator;

/**
 * Created by chamu on 4/15/2018.
 */

public class CryptoNews {

    private String title;
    private String body;
    private String url;
    private String imageUrl;
    private String  source;



    private Long    date;
    private Integer sortOrder;

    public CryptoNews( String title, String body,  String url, String imageUrl, String source, Long date, Integer sortOrder) {

        this.title = title;
        this.body = body;
        this.url = url;
        this.imageUrl = imageUrl;

        this.source = source;
        this.date   = date;
        this.sortOrder = sortOrder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSource() {
        return source;
    }

    public Long getDate() {
        return date;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public static Comparator<CryptoNews> SortOrderComparator = new Comparator<CryptoNews>() {

        public int compare(CryptoNews cryptoCoin1, CryptoNews cryptoCoin2) {
            Integer sortOrder1 = cryptoCoin1.getSortOrder();
            Integer sortOrder2 = cryptoCoin2.getSortOrder();

            // Ascending order
            return sortOrder1.compareTo(sortOrder2);
        }
    };
}
