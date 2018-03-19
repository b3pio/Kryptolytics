package com.fproject.cryptolytics.topCoins;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import com.fproject.cryptolytics.watchlist.WatchedItem;

import java.util.List;

/**
 * Created by chamu on 3/11/2018.
 */
public class TopCoinsAdapter extends RecyclerView.Adapter<TopCoinsAdapter.ViewHolder>{
    private List<WatchedItem> watchedItems;
    private Context context = null;
    public TopCoinsAdapter(Context context,List<WatchedItem> watchedItems){
        this.watchedItems = watchedItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_top_coins, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        WatchedItem watchedItem = watchedItems.get(position);

        CryptoCoin cryptoCoin     = watchedItem.getCryptoCoin();
        CryptoCurrency cryptoCurrency = watchedItem.getCryptoCurrency();

        viewHolder.tvSymbol.setText(cryptoCoin.getSymbol());
        viewHolder.tvName.setText(cryptoCoin.getCoinName());
        viewHolder.tvPrice.setText(cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol());
        viewHolder.tvChange.setText(cryptoCurrency.getChangePercent() + "%");
        viewHolder.tvVolume.setText(cryptoCurrency.getVolume());

        if (cryptoCurrency.isChangePositive()) {


            viewHolder.tvChange.setTextColor(ContextCompat.getColor(context,R.color.colorAscending));
        }
        else {
            viewHolder.tvChange.setTextColor(ContextCompat.getColor(context,R.color.colorDescending));
        }

        new ImageDownloader(viewHolder.ivImage).execute(cryptoCoin.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return watchedItems.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView   ivImage;
        TextView    tvSymbol;
        TextView    tvName;
        TextView    tvPrice;
        TextView    tvChange;
        TextView    tvVolume;

        public ViewHolder(View view) {
            super(view);

            ivImage   = view.findViewById(R.id.image);
            tvSymbol  = view.findViewById(R.id.symbol);
            tvName    = view.findViewById(R.id.name);
            tvPrice   = view.findViewById(R.id.price);
            tvChange  = view.findViewById(R.id.changeValue);
            tvVolume  = view.findViewById(R.id.volume);
        }
    }
}
/*
public class TopCoinsAdapter  extends BaseAdapter  {

        private static class ViewHolder {
            ImageView ivImage;
            TextView tvSymbol;
            TextView  tvName;
            TextView  tvPrice;
            TextView  tvChange;
            TextView  tvMarketCap;
            TextView tvCoinSupply;

            ImageDownloader imageDownloader;
        }

        private List<WatchedItem> watchedItems;
        private Context context;

    public TopCoinsAdapter(Context context, List<WatchedItem> watchedItems){

        this.context = context;
        this.watchedItems = watchedItems;
    }

        @Override
        public int getCount() {
        return watchedItems.size();
    }

        @Override
        public Object getItem(int position){
        return watchedItems.get(position);
    }

        @Override
        public long getItemId(int position) {
        return position;
    }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder  = null;
        WatchedItem watchedItem = (WatchedItem) getItem(position);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.row_top_coins, null);

            viewHolder = createViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        updateViewHolder(viewHolder,watchedItem);

        return view;
    }

    public WatchedItem getWatchedItem(int position){
        return watchedItems.get(position);
    }

    private ViewHolder createViewHolder(View view){
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.ivImage   = (ImageView) view.findViewById(R.id.image);
        viewHolder.tvSymbol  = (TextView)  view.findViewById(R.id.symbol);
        viewHolder.tvName    = (TextView)  view.findViewById(R.id.name);
        viewHolder.tvPrice   = (TextView)  view.findViewById(R.id.price);
        viewHolder.tvChange  = (TextView)  view.findViewById(R.id.changeValue);
        viewHolder.tvMarketCap = (TextView) view.findViewById(R.id.marketCap);
        viewHolder.tvCoinSupply = (TextView) view.findViewById(R.id.coinSupply);

        return viewHolder;
    }

    private void updateViewHolder(ViewHolder viewHolder, WatchedItem watchedItem){
        clearViewHolder(viewHolder,watchedItem);

        if (watchedItem.isLoaded()) {
            loadViewHolder(viewHolder,watchedItem);
        }
    }

    private void clearViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {

        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.ivImage.setImageDrawable(null);
        viewHolder.tvSymbol.setText(watchedItem.getFromSymbol());
        viewHolder.tvName.setText(" - ");
        viewHolder.tvPrice.setText(" - ");
        viewHolder.tvChange.setText("");
        viewHolder.tvMarketCap.setText(" - ");
        viewHolder.tvCoinSupply.setText(" - ");
    }

    private void loadViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {
        CryptoCoin cryptoCoin     = watchedItem.getCryptoCoin();
        CryptoCurrency cryptoCurrency = watchedItem.getCryptoCurrency();

        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.imageDownloader = new ImageDownloader(viewHolder.ivImage);
        viewHolder.imageDownloader.execute(cryptoCoin.getImageUrl());

        viewHolder.tvSymbol.setText(cryptoCoin.getSymbol());
        viewHolder.tvName.setText(cryptoCoin.getCoinName());
        viewHolder.tvPrice.setText(cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol());
        viewHolder.tvChange.setText(cryptoCurrency.getChangePercent() + "%");
        viewHolder.tvMarketCap.setText(cryptoCurrency.getMarketCap());
        viewHolder.tvCoinSupply.setText(cryptoCurrency.getSupply());

        if (cryptoCurrency.isChangePositive()) {
            viewHolder.tvChange.setTextColor(Color.GREEN);
        }
        else {
            viewHolder.tvChange.setTextColor(Color.RED);
        }

    }
}
*/
