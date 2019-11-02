package com.fproject.cryptolitycs.topCoins;


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

public class TopCoinsAdapter extends RecyclerView.Adapter<TopCoinsAdapter.ViewHolder> {
    private List<TopCoin> topCoins;

    private Context         context     = null;
    private OnClickListener ocListener  = null;

    public TopCoinsAdapter(Context context, List<TopCoin> topCoins) {
        this.topCoins = topCoins;
        this.context = context;
    }

    public void setOnClickListener(OnClickListener ocListener){
        this.ocListener = ocListener;
    }

    @Override
    public int getItemCount() {
        return topCoins.size();
    }

    public TopCoin getItem(int position){
        return topCoins.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_top_coins, parent, false);

        return new ViewHolder(view, ocListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TopCoin topCoin = topCoins.get(position);

        CryptoCoin cryptoCoin = topCoin.getCryptoCoin();
        CryptoCurrency cryptoCurrency = topCoin.getCryptoCurrency();

        viewHolder.tvSymbol.setText(cryptoCoin.getSymbol());
        viewHolder.tvName.setText(cryptoCoin.getCoinName());
        viewHolder.tvPrice.setText(topCoin.getPriceStr());

        viewHolder.tvChange.setText(topCoin.getChangePercentStr());
        viewHolder.tvChange.setTextColor(getChangeColor(cryptoCurrency.isChangePositive()));

        viewHolder.tvAlgorithm.setText(cryptoCoin.getAlgorithm());
        viewHolder.tvProofType.setText(cryptoCoin.getProofType());

        new ImageDownloader(getIconPlaceHolder(), viewHolder.ivImage).execute(cryptoCoin.getImageUrl());
    }


     private int getChangeColor(boolean positive) {
        if (positive) {
            return  ResourceHelper.getThemeColor(context,R.attr.colorPositiveValue);
        }
        else {
            return  ResourceHelper.getThemeColor(context,R.attr.colorNegativeValue);
        }
    }

    private Drawable getIconPlaceHolder() {
        return ContextCompat.getDrawable(context,R.drawable.ph_coin_icon);
    }

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSymbol;
        TextView tvName;
        TextView tvPrice;
        TextView tvChange;
        TextView tvAlgorithm;
        TextView tvProofType;

        ImageView       ivImage;
        OnClickListener ocListener;

        public ViewHolder(View view, OnClickListener ocListener) {
            super(view);

            ivImage  = view.findViewById(R.id.iv_image);
            tvSymbol = view.findViewById(R.id.tv_symbol);
            tvName   = view.findViewById(R.id.tv_name);
            tvPrice  = view.findViewById(R.id.tv_price);
            tvChange = view.findViewById(R.id.tv_changeValue);
            tvAlgorithm = view.findViewById(R.id.tv_algorithm);
            tvProofType = view.findViewById(R.id.tv_proofType);

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
