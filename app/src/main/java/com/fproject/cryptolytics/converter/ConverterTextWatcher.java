package com.fproject.cryptolytics.converter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * Created by chamu on 1/16/2018.
 */

public class ConverterTextWatcher implements TextWatcher {

    private ConvertListAdapter adapter = null;

    private int itemIndex;

    public ConverterTextWatcher (ConvertListAdapter adapter, int itemIndex){
        super();
        this.adapter = adapter;
        this.itemIndex = itemIndex;
    }

    public void onTextChanged(CharSequence text, int start, int before, int count) {

    }

    public void beforeTextChanged(CharSequence text, int start, int count,int after) {

    }

    public void afterTextChanged(Editable text) {

        if (!text.equals("")) {

            adapter.getConveterItem(1).setValue("555");
            adapter.notifyDataSetChanged();

//            update(text.toString(), itemIndex);
        }

    }

    private  void update(String value,int position){

        for (int index = 0; index < adapter.getCount(); index++ ){

            if (index == position)
                continue;;


        }

        adapter.getConveterItem(1).setValue("100");
        adapter.notifyDataSetChanged();
    }
}
