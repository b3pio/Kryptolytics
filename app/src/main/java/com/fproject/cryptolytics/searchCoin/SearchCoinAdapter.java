package com.fproject.cryptolytics.searchCoin;

import android.content.Context;
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

/**
 *
 */
public class SearchCoinAdapter extends BaseAdapter implements Filterable {

    private class ViewHolder {
        TextView  tvName;
    }

    private List<CryptoCoin> cryptoCoins;
    private List<CryptoCoin> filteredCoins;
    private Context context;
    private CoinFilter coinFilter;

    public SearchCoinAdapter(Context context, List<CryptoCoin> cryptoCoins){
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SearchCoinAdapter.ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.row_search_coin, null);

            viewHolder = new SearchCoinAdapter.ViewHolder();
            viewHolder.tvName  = (TextView) view.findViewById(R.id.fullName);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (SearchCoinAdapter.ViewHolder) view.getTag();
        }

        CryptoCoin cryptoCoin = (CryptoCoin) getItem(position);
        viewHolder.tvName.setText(cryptoCoin.getSymbol() + " - " + cryptoCoin.getCoinName());

        return view;
    }

    @Override
    public Filter getFilter() {
        if (coinFilter == null) {
            coinFilter = new CoinFilter();
        }
        return coinFilter;
    }


    private class CoinFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                List filteredCoins = new ArrayList<CryptoCoin>();

                for (int index = 0; index < cryptoCoins.size(); index++) {
                    CryptoCoin cryptoCoin = cryptoCoins.get(index);

                    if (cryptoCoin.getFullName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filteredCoins.add(cryptoCoin);
                    }
                }

                results.count = filteredCoins.size();
                results.values = filteredCoins;

            } else {

                results.count = cryptoCoins.size();
                results.values = cryptoCoins;

            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredCoins = (List) results.values;
            notifyDataSetChanged();
        }

    }

}
