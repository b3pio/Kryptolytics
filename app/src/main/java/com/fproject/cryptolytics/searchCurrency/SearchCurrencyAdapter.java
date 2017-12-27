package com.fproject.cryptolytics.searchCurrency;

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
import com.fproject.cryptolytics.searchCoin.SearchCoinAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchCurrencyAdapter extends BaseAdapter implements Filterable {

    private class ViewHolder {
        TextView tvName;
    }

    private Context context;

    private List<String>    currencies;
    private List<String>    filteredCurrencies;
    private CurrencyFilter  currencyFilter;

    public SearchCurrencyAdapter(Context context, List<String> currencies){
        this.context = context;
        this.currencies = currencies;
        this.filteredCurrencies = currencies;
    }

    @Override
    public int getCount() {
        return filteredCurrencies.size();
    }

    @Override
    public Object getItem(int position){
        return filteredCurrencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.row_search_coin, null);

            viewHolder = new ViewHolder();
            viewHolder.tvName  = (TextView) view.findViewById(R.id.fullName);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvName.setText(currencies.get(position));

        return view;
    }

    @Override
    public Filter getFilter() {
        if (currencyFilter == null) {
            currencyFilter = new CurrencyFilter();
        }

        return currencyFilter;
    }


    private class CurrencyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                String constraintStr = constraint.toString().toUpperCase();
                List filteredCurrencies = new ArrayList<String>();

                for (int index = 0; index < currencies.size(); index++) {
                    String currency = currencies.get(index);

                    if (currency.contains(constraintStr)) {
                        filteredCurrencies.add(currency);
                    }
                }

                results.count = filteredCurrencies.size();
                results.values = filteredCurrencies;

            } else {

                results.count = currencies.size();
                results.values = currencies;

            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredCurrencies = (List) results.values;
            notifyDataSetChanged();
        }

    }

 }

