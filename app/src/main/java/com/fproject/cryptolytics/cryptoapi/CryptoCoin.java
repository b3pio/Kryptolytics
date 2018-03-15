package com.fproject.cryptolytics.cryptoapi;


import java.util.Comparator;

public class CryptoCoin {
    private String name;
    private String imageUrl;
    private String symbol;
    private String coinName;
    private String fullName;
    private String algorithm;
    private String proofType;
    private String totalCoinSupply;
    private Integer sortOrder;

    public CryptoCoin(String name,
                      String imageUrl,
                      String symbol,
                      String coinName,
                      String fullName,
                      String algorithm,
                      String proofType,
                      String totalCoinSupply,
                        Integer sortOrder) {

        this.name       = name;
        this.imageUrl   = imageUrl;
        this.symbol     = symbol;
        this.coinName   = coinName;
        this.fullName   = fullName;
        this.algorithm  = algorithm;
        this.proofType  = proofType;
        this.totalCoinSupply = totalCoinSupply;
        this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCoinName() {
        return coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAlgorithm(){
        return  algorithm;
    }

    public String getProofType() {
        return proofType;
    }

    public String getTotalCoinSupply() {
        return totalCoinSupply;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public static Comparator<CryptoCoin> SortOrderComparator = new Comparator<CryptoCoin>() {

        public int compare(CryptoCoin cryptoCoin1, CryptoCoin cryptoCoin2) {
            Integer sortOrder1 = cryptoCoin1.getSortOrder();
            Integer sortOrder2 = cryptoCoin2.getSortOrder();

            //ascending order
            return sortOrder1.compareTo(sortOrder2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

}
