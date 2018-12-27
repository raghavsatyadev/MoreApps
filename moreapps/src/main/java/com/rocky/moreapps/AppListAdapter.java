package com.rocky.moreapps;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

public class AppListAdapter extends GenRecyclerAdapter<AppListAdapter.DataObjectHolder, MoreAppsModel> {

    @ColorInt
    private final int rowTitleColor;
    @ColorInt
    private final int rowDescriptionColor;
    @LayoutRes
    private int rowLayout;
    @ColorInt
    private int themeColor;
    private Typeface fontFace;

    AppListAdapter(@LayoutRes int rowLayout, @ColorInt int themeColor, Typeface fontFace, @ColorInt int rowTitleColor, @ColorInt int rowDescriptionColor) {
        super(new ArrayList<>());
        this.rowLayout = rowLayout;
        this.themeColor = themeColor;
        this.fontFace = fontFace;
        this.rowTitleColor = rowTitleColor;
        this.rowDescriptionColor = rowDescriptionColor;
    }

    @Override
    protected DataObjectHolder creatingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    protected void bindingViewHolder(DataObjectHolder holder, int position) {
        MoreAppsModel appModel = getItem(position);
        if (appModel != null) {
            if (holder.imgMoreApps != null)
                Glide.with(holder.imgMoreApps.getContext()).load(appModel.image_link).into(holder.imgMoreApps);

            if (holder.txtMoreAppsName != null)
                holder.txtMoreAppsName.setText(appModel.name);

            if (holder.ratingMoreApps != null)
                holder.ratingMoreApps.setRating((float) appModel.rating);

            if (holder.txtMoreAppsDescription != null) {
                if (!TextUtils.isEmpty(appModel.description)) {
                    holder.txtMoreAppsDescription.setVisibility(View.VISIBLE);
                    holder.txtMoreAppsDescription.setText(appModel.description);
                } else {
                    holder.txtMoreAppsDescription.setVisibility(View.GONE);
                }
            }
        }
    }

    class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView imgMoreApps;
        private TextView txtMoreAppsName;
        private SimpleRatingBar ratingMoreApps;
        private TextView txtMoreAppsDescription;

        DataObjectHolder(View view) {
            super(view);
            if (view != null) {
                imgMoreApps = view.findViewById(R.id.img_more_apps);
                txtMoreAppsName = view.findViewById(R.id.txt_more_apps_name);
                ratingMoreApps = view.findViewById(R.id.rating_more_apps);
                txtMoreAppsDescription = view.findViewById(R.id.txt_more_apps_description);
                if (themeColor != 0) ratingMoreApps.setFillColor(themeColor);
                if (rowTitleColor != 0) txtMoreAppsName.setTextColor(rowTitleColor);
                if (rowDescriptionColor != 0)
                    txtMoreAppsDescription.setTextColor(rowDescriptionColor);
                if (fontFace != null) {
                    txtMoreAppsName.setTypeface(fontFace);
                    txtMoreAppsDescription.setTypeface(fontFace);
                }
                view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (getMyClickListener() != null)
                getMyClickListener().onItemClick(getLayoutPosition(), v);
        }
    }
}