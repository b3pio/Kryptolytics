package com.fproject.cryptolytics.converter;

import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by chamu on 3/10/2018.
 */

public class ConverterValueUpdater {

    // List of converter items.
    private List<ConverterItem> converterItems = null;


    private ConverterItem selectedItem = null;

    public  ConverterValueUpdater( List<ConverterItem>  converterItems) {
        this.converterItems = converterItems;
    }

    public void setSelectedItem(ConverterItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Clear the values of the {@link ConverterItem} list.
     */
    public void clearValues() {

        for (ConverterItem item:converterItems) {

            // Don`t update this one, it was changed manually by the user.
            if (item.equals(selectedItem))
                continue;

            item.setValue("0");
        }
    }

    /**
     * Update the values of the {@link ConverterItem} list.
     */
    public void updateValues() {

        // Convert the value to universal currency.
        String valueStr     = selectedItem.getValue();
        Double ueRateValue  = valueStrToUERate(valueStr);

        for (ConverterItem converterItem:converterItems) {

            // Don`t update this one, it was changed manually by the user.
            if (converterItem.equals(selectedItem))
                continue;

            String itemRateValue = ueRateToValueStr(converterItem, ueRateValue);
            converterItem.setValue(itemRateValue);
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
        ConverterItem item = selectedItem;

        if (item.isLoaded()) {

            Double exRate = item.getToCryptoRate().getExRate();
            Double ueRate = exRate * value;

            Log.d("TAG", item.getSymbol() +
                         "[Rate: "      + String.valueOf(exRate)    + " * " +
                         "Value: "      + String.valueOf(value)     + " = " +
                         "UE Rate: "    + String.valueOf(ueRate)    + "]");

            return ueRate;
        }

        return 0.0D;
    }

    /**
     * Converts universal exchange rate to the rate of the item.
     */
    private Double ueRateToValue(ConverterItem item, Double ueRate){

        if (item.isLoaded()) {

            Double value = item.getFromCryptoRate().getExRate() * ueRate;

            Log.d("TAG",  item.getSymbol());
            Log.d("TAG", "Rate: "   + item.getFromCryptoRate().getExRate().toString());

            return item.getFromCryptoRate().getExRate() * ueRate;
        }

        return 0.0D;
    }

    /**
     * Converts universal exchange rate to the rate of the item as string.
     */
    private String ueRateToValueStr(ConverterItem item, Double value) {

        Double toValue = ueRateToValue(item, value);
        //toValue = (double) Math.round(toValue * 10000000d) / 10000000d;

        return String.valueOf(toValue);
    }

}
