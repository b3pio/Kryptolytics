package com.fproject.cryptolytics.cryptoapi;

/**
 *
 */
public interface CryptoCallback {
    void onSuccess(CryptoData cryptoData);
    void onFailure(String cryptoError);
}
