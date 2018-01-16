package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.fproject.cryptolytics.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamu on 1/1/2018.
 */

public class ConvertListAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView tvName;
        EditText etValue;
    }


    private List<ConverterItem> converterItems = new ArrayList<>();

    // Context of current state of the application.
    private Context context = null;


    public ConvertListAdapter(Context context, List<ConverterItem> converterItems){
        this.context = context;
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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = createView(view);

        ConverterItem item  = (ConverterItem) getItem(position);
        ViewHolder viewHolder  = (ViewHolder) view.getTag();

        viewHolder.tvName.setText(item.getSymbol());
      //  viewHolder.etValue.setText(item.getValue(), TextView.BufferType.NORMAL);

        return view;
    }

    /**
     * Returns the ViewHolder of the current view.
     */
    private View createView(View view){
        if (view != null) return  view;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.row_converter_list, null);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvName =  (TextView) view.findViewById(R.id.tv_name);
    //    viewHolder.etValue = (EditText) view.findViewById(R.id.value);

        view.setTag(viewHolder);

        return  view;
    }
}
