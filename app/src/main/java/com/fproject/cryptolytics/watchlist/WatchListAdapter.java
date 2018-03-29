package com.fproject.cryptolytics.watchlist;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.utility.ImageDownloader;

import java.util.List;

/**
 * Created by chamu on 3/26/2018.
 */

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {
    private List<WatchedItem> watchedItems;

    private OnClickListener ocListener = null;

    // Context for accessing the application assets and resources.
    private Context context  = null;


    public WatchListAdapter(Context context, List<WatchedItem> watchedItems) {
        this.watchedItems = watchedItems;
        this.context = context;
    }

    public void setOnClickListener(OnClickListener ocListener){
        this.ocListener = ocListener;
    }

    @Override
    public int getItemCount() {
        return watchedItems.size();
    }

    public WatchedItem getWatchedItem(int position){
        return watchedItems.get(position);
    }

    public void remove(int position){
        watchedItems.remove(position);
        this.notifyItemRemoved(position);
    }

    public void remove(WatchedItem watchedItem){
        int position = watchedItems.indexOf(watchedItem);
        watchedItems.remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_watch_list, parent, false);

        return new ViewHolder(view, ocListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        WatchedItem watchedItem = getWatchedItem(position);
        viewHolder.tvSymbol.setText(watchedItem.getFromSymbol());

        if (watchedItem.isLoaded()) {
            CryptoCoin cryptoCoin = watchedItem.getCryptoCoin();
            CryptoCurrency cryptoCurrency = watchedItem.getCryptoCurrency();

            viewHolder.tvName.setText(cryptoCoin.getCoinName());

            String priceStr = cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol();
            viewHolder.tvPrice.setText(priceStr);

            String changePercentStr = cryptoCurrency.getChangePercent() + "%";
            viewHolder.tvChange.setText(changePercentStr);

            Integer changeColor = getChangeColor(cryptoCurrency.isChangePositive());
            viewHolder.tvChange.setTextColor(changeColor);

            bindImage(viewHolder, cryptoCoin.getImageUrl());
        }
    }

    private void bindImage(ViewHolder viewHolder, String imageUrl){
        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.imageDownloader = new ImageDownloader(viewHolder.ivImage);
        viewHolder.imageDownloader.execute(imageUrl);
    }

    private int getChangeColor(boolean positive) {
        if (positive) {
            return ContextCompat.getColor(context, R.color.colorPositive);
        } else {
            return ContextCompat.getColor(context, R.color.colorNegative);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImage;
        TextView  tvSymbol;
        TextView  tvName;
        TextView  tvPrice;
        TextView  tvChange;

        ImageDownloader imageDownloader;
        OnClickListener ocListener;

        public ViewHolder(View view, OnClickListener ocListener) {
            super(view);

            ivImage   = view.findViewById(R.id.image);
            tvSymbol  = view.findViewById(R.id.symbol);
            tvName    = view.findViewById(R.id.name);
            tvPrice   = view.findViewById(R.id.price);
            tvChange  = view.findViewById(R.id.changeValue);

            this.ocListener = ocListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (ocListener != null) {
                ocListener.onClick(view, getAdapterPosition());
            }
        }
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }

}

