package com.fproject.cryptolytics.searchCoin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoCoin;

import java.util.ArrayList;
import java.util.List;


public class SearchCoinAdapter extends BaseAdapter implements Filterable {

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    private class ViewHolder {
        TextView  tvName;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    private CoinFilter coinFilter;

    private List<CryptoCoin> cryptoCoins;
    private List<CryptoCoin> filteredCoins;

    // Context for accessing the application assets and resources.
    private Context context;


    public SearchCoinAdapter(Context context, List<CryptoCoin> cryptoCoins) {
        this.context = context;
        this.filteredCoins = cryptoCoins;
        this.cryptoCoins = cryptoCoins;
    }

    @Override
    public int getCount() {
        return filteredCoins.size();
    }

    @Override
    public Object getItem(int position){
        return filteredCoins.get(position);
    }

    public CryptoCoin getCoinAt(int position){
        return filteredCoins.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (coinFilter == null) {
            coinFilter = new CoinFilter();
        }
        return coinFilter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.row_search_coin, null);

            createViewHolder(view);
        }

        viewHolder = (ViewHolder) view.getTag();

        CryptoCoin cryptoCoin = (CryptoCoin) getItem(position);
        viewHolder.tvName.setText(cryptoCoin.getSymbol() + " - " + cryptoCoin.getCoinName());

        return view;
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvName =  view.findViewById(R.id.tv_name);
        view.setTag(viewHolder);

        return viewHolder;
    }

    // --------------------------------------------------------------------------------------------
    //region CoinFilter
    // --------------------------------------------------------------------------------------------

    private class CoinFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if ((constraint == null) || (constraint.length() == 0)) {

                results.count = cryptoCoins.size();
                results.values = cryptoCoins;
            }
            else {
                executeFiltering(constraint, results);
            }

            return results;

        }

        /**
         * Do the actual filtering here.
         */
        private void executeFiltering(CharSequence constraint, FilterResults results) {
            List filteredCoins = new ArrayList<CryptoCoin>();

            String constraintStr = constraint.toString().toUpperCase();

            for (int index = 0; index < cryptoCoins.size(); index++) {
                CryptoCoin cryptoCoin = cryptoCoins.get(index);

                if (cryptoCoin.getFullName().toUpperCase().contains(constraintStr)) {
                    filteredCoins.add(cryptoCoin);
                }
            }

            results.count = filteredCoins.size();
            results.values = filteredCoins;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredCoins = (List) results.values;
            notifyDataSetChanged();
        }

    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

}
