package com.fproject.cryptolitycs.cryptoapi;

/**
 *
 */
public interface CryptoCallback {

    void onSuccess(CryptoData cryptoData);
    void onFailure(String cryptoError);
}
