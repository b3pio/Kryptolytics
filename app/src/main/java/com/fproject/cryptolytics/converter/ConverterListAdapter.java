package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamu on 1/1/2018.
 */

public class ConverterListAdapter extends BaseAdapter  {

    public static class ViewHolder {
        TextView tvSymbol;
        TextView tvName;
        TextView tvValue;

        ConverterTextWatcher twValue;
    }

    private List<ConverterItem> converterItems = new ArrayList<>();

    // Context of current state of the application.
    private Context  context  = null;
    private ListView listView = null;


    public ConverterListAdapter(Context context, ListView listView, List<ConverterItem> converterItems){
        this.context  = context;
        this.listView = listView;
        this.converterItems = converterItems;
    }

    @Override
    public int getCount() {
        return converterItems.size();
    }

    @Override
    public Object getItem(int position){
        return converterItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ConverterItem getConveterItem(int position) {
        return (ConverterItem) converterItems.get(position);
    }

    public List<ConverterItem> getConverterItems(){
        return converterItems;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = createView(view,position);

        ConverterItem item  = (ConverterItem) getItem(position);
        ViewHolder viewHolder  = (ViewHolder) view.getTag();

        viewHolder.tvSymbol.setText(item.getSymbol());
        viewHolder.tvValue.setText(item.getValue());
        viewHolder.twValue.setItemIndex(position);

        if (item.isLoaded()) {
            viewHolder.tvName.setText(item.getCrpytoCoin().getCoinName());
        }

        return view;
    }

    /**
     * Returns the ViewHolder of the current view.
     */
    private View createView(View view, int position){
        if (view != null) return  view;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.row_converter_list, null);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvSymbol = view.findViewById(R.id.tv_symbol);
        viewHolder.tvName   = view.findViewById(R.id.tv_name);
        viewHolder.tvValue  = view.findViewById(R.id.tv_value);

      //  viewHolder.tvValue.addTextChangedListener();
        // viewHolder.etValue  = (EditText) view.findViewById(R.id.et_value);


        viewHolder.twValue  = new ConverterTextWatcher(listView,
                viewHolder.tvValue);


       viewHolder.tvValue.addTextChangedListener(viewHolder.twValue);

        view.setTag(viewHolder);

        return  view;
    }



}
