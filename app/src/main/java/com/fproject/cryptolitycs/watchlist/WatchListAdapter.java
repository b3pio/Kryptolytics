package com.fproject.cryptolitycs.watchlist;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolitycs.cryptoapi.CryptoCoin;
import com.fproject.cryptolitycs.utility.ImageDownloader;
import com.fproject.cryptolitycs.R;
import com.fproject.cryptolitycs.cryptoapi.CryptoCurrency;
import com.fproject.cryptolitycs.utility.ResourceHelper;

import java.util.List;


public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {
    private OnClickListener     ocListener;
    private List<WatchedItem>   watchedItems;

    // Context for accessing the application assets and resources.
    private Context context;


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

    public void removeItem(WatchedItem watchedItem) {
        int position = watchedItems.indexOf(watchedItem);

        watchedItems.remove(position);
        notifyItemRemoved(position);
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

        if (watchedItem.isLoaded()) {
            loadViewHolder(viewHolder, watchedItem);
        }
        else {
            clearViewHolder(viewHolder, watchedItem);
        }
    }

    /**
     * Load the data into the {@link ViewHolder}.
     */
    private void loadViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {

        CryptoCoin cryptoCoin = watchedItem.getCryptoCoin();
        CryptoCurrency cryptoCurrency = watchedItem.getCryptoCurrency();

        viewHolder.tvSymbol.setText(watchedItem.getFromSymbol());
        viewHolder.tvName.setText(cryptoCoin.getCoinName());

        String priceStr = cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol();
        viewHolder.tvPrice.setText(priceStr);

        String changePercentStr = cryptoCurrency.getChangePercent() + "%";
        viewHolder.tvChange.setText(changePercentStr);

        Integer changeColor = getChangeColor(cryptoCurrency.isChangePositive());
        viewHolder.tvChange.setTextColor(changeColor);


        bindImage(viewHolder, cryptoCoin.getImageUrl());
    }

    /**
     * Clear the data into the {@link ViewHolder}.
     */
    private void clearViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {
        if (viewHolder.imageDownloader != null)  {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.tvSymbol.setText(watchedItem.getFromSymbol());
        viewHolder.ivImage.setImageDrawable(null);
        viewHolder.tvName.setText(" - ");
        viewHolder.tvPrice.setText(" - ");
        viewHolder.tvChange.setText("");
    }

    private void bindImage(ViewHolder viewHolder, String imageUrl){
        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.imageDownloader = new ImageDownloader(getIconPlaceHolder(), viewHolder.ivImage);
        viewHolder.imageDownloader.execute(imageUrl);
    }

    private Drawable getIconPlaceHolder() {
        return ContextCompat.getDrawable(context,R.drawable.ph_coin_icon);
    }

    private int getChangeColor(boolean positive) {

        if (positive) {
            return  ResourceHelper.getThemeColor(context,R.attr.colorPositiveValue);
        }
        else {
            return  ResourceHelper.getThemeColor(context,R.attr.colorNegativeValue);
        }
    }

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImage;
        TextView  tvSymbol;
        TextView  tvName;
        TextView  tvPrice;
        TextView  tvChange;

        ImageDownloader imageDownloader;
        OnClickListener ocListener;

        public ViewHolder(View view, OnClickListener ocListener) {
            super(view);

            ivImage   = view.findViewById(R.id.iv_image);
            tvSymbol  = view.findViewById(R.id.tv_symbol);
            tvName    = view.findViewById(R.id.tv_name);
            tvPrice   = view.findViewById(R.id.tv_price);
            tvChange  = view.findViewById(R.id.tv_changeValue);

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

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region OnClickListener
    // --------------------------------------------------------------------------------------------

    public interface OnClickListener {
        void onClick(View view, int position);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------
}

