package com.fproject.cryptolitycs.utility;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Used for checking the internet connection and network connection.
 */
@SuppressWarnings("unused")
public class InternetChecker {
    private Context context = null;

    public InternetChecker(Context context) {
        this.context = context;
    }

    /**
     * Determines whether the device is connected to the network.
     */
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected());
    }

    /**
     * Determines whether the connection is actually working.
     */
    public boolean isInternetAvailable() {
        try {

            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");

        }
        catch (UnknownHostException ex) {

        }

        return false;
    }

    /**
     * Determines whether the connection and it is working.
     */
    public boolean isConnected() {
        return (isNetworkAvailable() && isInternetAvailable());
    }
}
