package com.fproject.cryptolytics.topCoins;


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
 * Created by chamu on 3/11/2018.
 */
public class TopCoinsAdapter extends RecyclerView.Adapter<TopCoinsAdapter.ViewHolder>{

    private List<TopCoin> topCoins;
    private Context context = null;

    public TopCoinsAdapter(Context context, List<TopCoin> topCoins){
        this.topCoins = topCoins;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return topCoins.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_top_coins, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TopCoin topCoin = topCoins.get(position);

        CryptoCoin cryptoCoin  = topCoin.getCryptoCoin();
        CryptoCurrency cryptoCurrency = topCoin.getCryptoCurrency();

        viewHolder.tvSymbol.setText(cryptoCoin.getSymbol());
        viewHolder.tvName.setText(cryptoCoin.getCoinName());
        viewHolder.tvPrice.setText(topCoin.getPriceStr());
        viewHolder.tvChange.setText(topCoin.getChangePercentStr());

        viewHolder.tvVolume.setText(cryptoCurrency.getVolume());
        viewHolder.tvChange.setTextColor(getChangeColor(cryptoCurrency.isChangePositive()));

        new ImageDownloader(viewHolder.ivImage).execute(cryptoCoin.getImageUrl());
    }

    private int getChangeColor(boolean positive){
        if (positive) {
            return ContextCompat.getColor(context,R.color.colorPositive);
        }
        else {
            return ContextCompat.getColor(context,R.color.colorNegative);
        }
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
