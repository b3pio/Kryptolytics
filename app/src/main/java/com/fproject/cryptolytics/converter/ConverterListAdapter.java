package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.utility.ImageDownloader;

import java.util.List;


public class ConverterListAdapter extends RecyclerView.Adapter<ConverterListAdapter.ViewHolder>  {
    private List<ConverterItem> converterItems;

    // Context of current state of the application.
    private Context context;

    // Notify this when an item is selected.
    private OnClickListener ocListener;

    // The index of the currently selected item.
    private Integer selectedIndex = -1;


    public ConverterListAdapter(Context context, List<ConverterItem> converterItems) {
        this.context  = context;
        this.converterItems = converterItems;
    }

    public void setOnClickListener(OnClickListener ocListener){
        this.ocListener = ocListener;
    }

    public int getItemCount() {
        return converterItems.size();
    }

    public ConverterItem getConverterItem(int position) {
        return converterItems.get(position);
    }

    public void removeItem(int position) {
        converterItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_converter_list, parent, false);

        return new ViewHolder(view, ocListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ConverterItem item = getConverterItem(position);

        viewHolder.tvValue.setText(item.getValueStr());
        viewHolder.itemView.setSelected(selectedIndex == position);

        if (item.getCryptoCoin() != null) {

            viewHolder.tvName.setText(item.getCryptoCoin().getCoinName());
            bindImage(viewHolder, item.getCryptoCoin().getImageUrl());
        }
    }

    private void bindImage(ViewHolder viewHolder, String imageUrl){
        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.imageDownloader = new ImageDownloader(viewHolder.ivImage);
        viewHolder.imageDownloader.execute(imageUrl);
    }

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView    tvName;
        TextView    tvValue;
        ImageView   ivImage;

        ImageDownloader imageDownloader;
        OnClickListener ocListener;

        public ViewHolder(View itemView,  OnClickListener ocListener) {
            super(itemView);

            tvName  = itemView.findViewById(R.id.tv_name);
            tvValue = itemView.findViewById(R.id.tv_value);
            ivImage = itemView.findViewById(R.id.iv_image);

            this.ocListener = ocListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            toggleSelection(getAdapterPosition());

            if (ocListener != null) {
                ocListener.onClick(view, getAdapterPosition());
            }
        }

        private void toggleSelection(int position) {
            if (selectedIndex != -1) {
                notifyItemChanged(selectedIndex);
            }

            selectedIndex = position;
            notifyItemChanged(selectedIndex);
        }
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    //region OnClickListener
    // --------------------------------------------------------------------------------------------

    public interface OnClickListener {
        void onClick(View view, int position);
    }

    // --------------------------------------------------------------------------------------------
    //endregion
    // --------------------------------------------------------------------------------------------

}
