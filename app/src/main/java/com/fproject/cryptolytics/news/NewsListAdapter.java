package com.fproject.cryptolytics.news;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fproject.cryptolytics.R;
import com.fproject.cryptolytics.cryptoapi.CryptoNews;
import com.fproject.cryptolytics.utility.ImageDownloader;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private OnClickListener     ocListener;
    private List<CryptoNews>    cryptoNewsList;

    // Context for accessing the application assets and resources.
    private Context context;

    public NewsListAdapter(Context context, List<CryptoNews> newsItems) {
        this.cryptoNewsList = newsItems;
        this.context = context;
    }

    public void setOnClickListener(OnClickListener ocListener){
        this.ocListener = ocListener;
    }

    @Override
    public int getItemCount() {
        return cryptoNewsList.size();
    }

    public CryptoNews getNewsItem(int position){
        return cryptoNewsList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_news, parent, false);

        return new ViewHolder(view, ocListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CryptoNews cryptoNews = getNewsItem(position);

        viewHolder.tvTitle.setText(cryptoNews.getTitle());
        viewHolder.tvSource.setText(cryptoNews.getSource());
        viewHolder.tvDate.setText(getRelativeDateString(cryptoNews.getDate()));

        bindImage(viewHolder,cryptoNews.getImageUrl());
    }

    private void bindImage(ViewHolder viewHolder, String imageUrl){

        if (viewHolder.imageDownloader != null) {
            viewHolder.imageDownloader.cancel(true);
        }

        viewHolder.imageDownloader = new ImageDownloader(getThumbnailPlaceHolder(), viewHolder.ivThumbnail);
        viewHolder.imageDownloader.execute(imageUrl);
    }

    private Drawable getThumbnailPlaceHolder() {
        return ContextCompat.getDrawable(context, R.drawable.ph_news_thumbnail);
    }

    private String getRelativeDateString(long dateValue){
        long date = dateValue * 1000L;
        long now  = System.currentTimeMillis();
        Long difference = now - date;

        if (TimeUnit.MILLISECONDS.toMinutes(difference) <= 1) {
            return context.getString(R.string.common_just_now);
        }
        else if (TimeUnit.MILLISECONDS.toMinutes(difference) < 60) {

            return (String) DateUtils.getRelativeTimeSpanString(date, now,
                    DateUtils.MINUTE_IN_MILLIS);

        }

        return (String) DateUtils.getRelativeTimeSpanString(date, now,  DateUtils.HOUR_IN_MILLIS);

    }

    // --------------------------------------------------------------------------------------------
    //region ViewHolder
    // --------------------------------------------------------------------------------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivThumbnail;
        TextView  tvTitle;
        TextView  tvSource;
        TextView  tvDate;

        ImageDownloader imageDownloader;
        OnClickListener ocListener;

        public ViewHolder(View view, OnClickListener ocListener) {
            super(view);

            ivThumbnail = view.findViewById(R.id.iv_thumbnail);
            tvTitle  = view.findViewById(R.id.tv_title);
            tvSource = view.findViewById(R.id.tv_source);
            tvDate   = view.findViewById(R.id.tv_date);

            this.ocListener = ocListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (ocListener != null) {
                ocListener.onClick(view, getAdapterPosition());
            }
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
