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

import java.util.ArrayList;
import java.util.List;


public class SearchCurrencyAdapter extends BaseAdapter implements Filterable {

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    private class ViewHolder {
        TextView tvName;
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    private CurrencyFilter  currencyFilter;

    private List<String>    currencies;
    private List<String>    filteredCurrencies;

    // Context for accessing the application assets and resources.
    private Context context = null;


    public SearchCurrencyAdapter(Context context, List<String> currencies) {
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

    public String getSymbolAt(int position){
        String currency = filteredCurrencies.get(position);
        String symbol   =  currency.split(" - ")[0];

        return  symbol;
    }

    @Override
    public Filter getFilter() {
        if (currencyFilter == null) {
            currencyFilter = new CurrencyFilter();
        }

        return currencyFilter;
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
        viewHolder.tvName.setText((String) getItem(position));

        return view;
    }

    private ViewHolder createViewHolder(View view){
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvName = view.findViewById(R.id.fullName);
        view.setTag(viewHolder);

        return viewHolder;
    }

    // --------------------------------------------------------------------------------------------
    //region CurrencyFilter
    // --------------------------------------------------------------------------------------------

    private class CurrencyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if ((constraint == null) || (constraint.length() == 0))  {

                results.count = currencies.size();
                results.values = currencies;
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
            List filteredCurrencies = new ArrayList<String>();

            String constraintStr = constraint.toString().toUpperCase();

            for (int index = 0; index < currencies.size(); index++) {
                String currency = currencies.get(index);

                if (currency.toUpperCase().contains(constraintStr.toUpperCase())) {
                    filteredCurrencies.add(currency);
                }
            }

            results.count = filteredCurrencies.size();
            results.values = filteredCurrencies;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredCurrencies = (List) results.values;
            notifyDataSetChanged();
        }

    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

 }

