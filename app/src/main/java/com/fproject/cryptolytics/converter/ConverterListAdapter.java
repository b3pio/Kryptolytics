package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.utility.ExtTextView;
import com.fproject.cryptolytics.utility.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamu on 1/1/2018.
 */

public class ConverterListAdapter extends BaseAdapter  {

    public static class ViewHolder {
        TextView tvName;
        TextView tvValue;
        ImageView ivImage;

        ImageDownloader imageDownloader;
    }

    private List<ConverterItem> converterItems = new ArrayList<>();

    // Context of current state of the application.
    private Context  context  = null;


    public ConverterListAdapter(Context context, List<ConverterItem> converterItems){
        this.context  = context;
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

        viewHolder.tvValue.setText(item.getValueStr());

        if (item.getCrpytoCoin() != null) {
            viewHolder.tvName.setText(item.getCrpytoCoin().getCoinName());
            viewHolder.imageDownloader = new ImageDownloader(viewHolder.ivImage);
            viewHolder.imageDownloader.execute(item.getCrpytoCoin().getImageUrl());
        }



        //int viewHeight = (parent.getHeight() / converterItems.size()) -1 ;
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));


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


        viewHolder.tvName   = view.findViewById(R.id.tv_name);
        viewHolder.tvValue  = view.findViewById(R.id.tv_value);
        viewHolder.ivImage   = view.findViewById(R.id.iv_image);

        view.setTag(viewHolder);

        return  view;
    }

}
