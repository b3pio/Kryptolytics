package com.fproject.cryptolytics.converter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * TextWatcher for a {@link ConverterItem}.
 */
public class ConverterTextWatcher implements TextWatcher {

    private TextView textView = null;
    private ListView listView = null;

    private int itemIndex = -1;

    // The list of item in the list view.
    private List<ConverterItem> converterItems = null;

    public ConverterTextWatcher (ListView listView, TextView editText){
        super();

        this.listView = listView;
        this.textView = editText;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public void onTextChanged(CharSequence text, int start, int before, int count) {
        // Do nothing.
    }

    public void beforeTextChanged(CharSequence text, int start, int count,int after) {
        // Do nothing.
    }

    public void afterTextChanged(Editable text) {
        if (itemIndex == -1) return;

        converterItems = ((ConverterListAdapter) listView.getAdapter()).getConverterItems();


        //Log.d("TextWatcher", "changed man " + text);

        Log.d("TextWatcher", String.valueOf(listView.getSelectedItemPosition()) + " " + String.valueOf(itemIndex) );
        // Only update the values if this EditText has focus, because calling setText()
        // method triggers the TextWatcher and causing in infinite loop.
        if (listView.getSelectedItemPosition() == itemIndex) {
        //if (textView.hasFocus()) {
            Log.d("TextWatcher", "has focus " + text);
            String valueStr = text.toString();

            if (valueStr.equals("")) {
                clearValues();
            }
            else {
                updateValues(valueStr);
            }
        }
    }

    /**
     * Clear the values of the {@link ConverterItem} list.
     */
    private void clearValues() {

        for (int index = 0; index < converterItems.size(); index++) {

            // Don`t update this one, it was changed manually by the user.
            if (index == itemIndex)
                continue;

            updateValue(index, "");
        }
    }

    /**
     * Update the values of the {@link ConverterItem} list.
     */
    private void updateValues(String valueStr) {

        // Convert the value to universal currency.
        Double ueRateValue = valueStrToUERate(valueStr);

        for (int index = 0; index < converterItems.size(); index++) {

            // Don`t update this one, it was changed manually by the user.
            if (index == itemIndex)
                continue;

            // Convert the value to the currency of the item.
            ConverterItem converterItem = converterItems.get(index);
            String itemRateValue = ueRateToValueStr(converterItem, ueRateValue);
            updateValue(index, itemRateValue);
        }
    }

    /**
     * Updates the value of the {@link ConverterItem} located at the
     * specified index of the listview.
     */
    private void updateValue(int index, String value){
        View view = listView.getChildAt(index - listView.getFirstVisiblePosition());

        if ((view != null)  && (view.getTag() != null)) {
            ((ConverterListAdapter.ViewHolder) view.getTag()).tvValue.setText(value);
        }
    }

    /**
     * Convert the value string to universal exchange rate.
     */
    private Double valueStrToUERate(String valueStr){
        Double value = Double.valueOf(valueStr.toString());
        return valueToUERate(value);
    }

    /**
     * Convert value to universal exchange rate.
     */
    private Double valueToUERate(Double value){
        ConverterItem item = converterItems.get(itemIndex);

        if (item.isLoaded()) {

            Log.d("TAG", "Symbol: " + item.getSymbol());
            Log.d("TAG", "Rate: " + item.getToCryptoRate().getExRate().toString());

            return item.getToCryptoRate().getExRate() * value;
        }

        return 0.0D;
    }

    /**
     * Converts universal exchange rate to the rate of the item.
     */
    private Double ueRateToValue(ConverterItem item, Double value){

        if (item.isLoaded()) {

            Log.d("TAG", "Symbol: " + item.getSymbol());
            Log.d("TAG", "Rate: " + item.getFromCryptoRate().getExRate().toString());

           return item.getFromCryptoRate().getExRate() * value;
        }

        return 0.0D;
    }

    /**
     * Converts universal exchange rate to the rate of the item as string.
     */
    private String ueRateToValueStr(ConverterItem item, Double value) {

        Double toValue = ueRateToValue(item, value);


        //BigDecimal bigDecimal = new BigDecimal(String.valueOf(value)).setScale(4, BigDecimal.ROUND_FLOOR);
        toValue = (double)Math.round(toValue * 10000000d) / 10000000d;
        //return bigDecimal.toString();
        return String.valueOf(toValue);
    }
}
