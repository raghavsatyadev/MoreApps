package io.github.raghavsatyadev.moreapps.adapter

import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import io.github.raghavsatyadev.moreapps.R.id
import io.github.raghavsatyadev.moreapps.SimpleRatingBar
import io.github.raghavsatyadev.moreapps.adapter.MoreAppsListAdapter.DataObjectHolder
import io.github.raghavsatyadev.moreapps.model.MoreAppsDetails

class MoreAppsListAdapter(
    @LayoutRes
    private val rowLayout: Int,
    @ColorInt
    private val themeColor: Int,
    private val fontFace: Typeface?,
    @ColorInt
    private val rowTitleColor: Int,
    @ColorInt
    private val rowDescriptionColor: Int,
) : MoreAppsBaseAdapter<DataObjectHolder, MoreAppsDetails>(
    ArrayList()
) {
    override fun creatingViewHolder(parent: ViewGroup?, viewType: Int): DataObjectHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(rowLayout, parent, false)
        return DataObjectHolder(view)
    }

    override fun bindingViewHolder(holder: DataObjectHolder, position: Int) {
        val appModel = getItem(position)
        if (holder.imgMoreApps != null) Glide.with(holder.imgMoreApps.context)
            .load(appModel.imageLink).into(holder.imgMoreApps)
        if (holder.txtMoreAppsName != null) holder.txtMoreAppsName.text = appModel.name
        if (holder.ratingMoreApps != null) holder.ratingMoreApps.rating =
            appModel.rating.toFloat()
        if (holder.txtMoreAppsDescription != null) {
            if (!TextUtils.isEmpty(appModel.description)) {
                holder.txtMoreAppsDescription.visibility = View.VISIBLE
                holder.txtMoreAppsDescription.text = appModel.description
            } else {
                holder.txtMoreAppsDescription.visibility = View.GONE
            }
        }
    }

    inner class DataObjectHolder(view: View) : ViewHolder(view), OnClickListener {
        val imgMoreApps: ImageView?
        val txtMoreAppsName: TextView?
        val ratingMoreApps: SimpleRatingBar?
        val txtMoreAppsDescription: TextView?

        init {
            imgMoreApps = view.findViewById(id.img_more_apps)
            txtMoreAppsName = view.findViewById(id.txt_more_apps_name)
            ratingMoreApps = view.findViewById(id.rating_more_apps)
            txtMoreAppsDescription = view.findViewById(id.txt_more_apps_description)
            if (themeColor != 0) ratingMoreApps.fillColor = themeColor
            if (rowTitleColor != 0) txtMoreAppsName.setTextColor(rowTitleColor)
            if (rowDescriptionColor != 0) txtMoreAppsDescription.setTextColor(rowDescriptionColor)
            txtMoreAppsName.typeface = fontFace
            txtMoreAppsDescription.typeface = fontFace
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (myClickListener != null) myClickListener!!.onItemClick(layoutPosition, v)
        }
    }
}