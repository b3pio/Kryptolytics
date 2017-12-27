package com.fproject.cryptolytics.watchlist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;
import com.fproject.cryptolytics.cryptoapi.CryptoCurrency;
import com.fproject.cryptolytics.utility.ImageDownloader;
import java.util.List;

public class WatchListAdapter extends BaseAdapter {

    private class ViewHolder {
        ImageView ivImage;
        TextView  tvSymbol;
        TextView  tvName;
        TextView  tvPrice;
        TextView  tvChange;
    }

    private List<WatchedItem> watchedItems;
    private Context context;

    public WatchListAdapter(Context context, List<WatchedItem> watchedItems){

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
        ViewHolder  viewHolder  = null;
        WatchedItem watchedItem = (WatchedItem) getItem(position);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.row_watch_list, null);

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

        return viewHolder;
    }

    private void updateViewHolder(ViewHolder viewHolder, WatchedItem watchedItem){

        if (!watchedItem.isLoaded()) {
            clearViewHolder(viewHolder,watchedItem);
        }
        else {
            loadViewHolder(viewHolder,watchedItem);
        }
    }

    private void clearViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {

        viewHolder.ivImage.setImageDrawable(null);

        viewHolder.tvSymbol.setText(watchedItem.getFromSymbol());
        viewHolder.tvName.setText(" - ");
        viewHolder.tvPrice.setText(" - ");
        viewHolder.tvChange.setText("");

    }

    private void loadViewHolder(ViewHolder viewHolder, WatchedItem watchedItem) {
        CryptoCoin     cryptoCoin     = watchedItem.getCryptoCoin();
        CryptoCurrency cryptoCurrency = watchedItem.getCryptoCurrency();

        new ImageDownloader(viewHolder.ivImage).execute(cryptoCoin.getImageUrl());

        viewHolder.tvSymbol.setText(cryptoCoin.getSymbol());
        viewHolder.tvName.setText(cryptoCurrency.getToSymbol());
        viewHolder.tvPrice.setText(cryptoCurrency.getPrice() + " " + cryptoCurrency.getToSymbol());
        viewHolder.tvChange.setText(cryptoCurrency.getChangePercent() + "%");

        if (cryptoCurrency.isChangePositive()) {
            viewHolder.tvChange.setTextColor(Color.GREEN);
        }
        else {
            viewHolder.tvChange.setTextColor(Color.RED);
        }
    }
}
