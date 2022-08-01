@file:Suppress("unused")

package io.github.raghavsatyadev.moreapps.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

abstract class MoreAppsBaseAdapter<ViewHolder : RecyclerView.ViewHolder, Model> internal constructor(
    private val models: ArrayList<Model>,
) : Adapter<ViewHolder>() {
    var myClickListener: MyClickListener? = null
        private set

    protected abstract fun creatingViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return creatingViewHolder(parent, viewType)
    }

    protected abstract fun bindingViewHolder(holder: ViewHolder, position: Int)
    override fun getItemCount(): Int {
        return models.size
    }

    fun addAll(models: ArrayList<Model>) {
        val position = itemCount
        this.models.addAll(models)
        notifyItemRangeInserted(position, models.size)
    }

    fun addItem(model: Model, index: Int) {
        models.add(model)
        notifyItemInserted(index)
    }

    fun addItem(model: Model) {
        models.add(model)
        notifyItemInserted(itemCount - 1)
    }

    fun deleteAll() {
        val itemCount = itemCount
        models.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    open fun replaceAll(items: ArrayList<Model>?) {
        if (items != null) {
            val oldItemCount = itemCount
            val newItemCount = items.size

            models.clear()
            models.addAll(items)

            if (oldItemCount == 0) {
                notifyItemRangeInserted(oldItemCount, newItemCount)
            } else if (newItemCount < oldItemCount) {
                notifyItemRangeChanged(0, newItemCount)
                notifyItemRangeRemoved(newItemCount, oldItemCount - newItemCount)
            } else {
                notifyItemRangeChanged(0, oldItemCount)
                notifyItemRangeInserted(oldItemCount, newItemCount - oldItemCount)
            }
        } else {
            val oldItemCount = itemCount
            models.clear()
            notifyItemRangeRemoved(0, oldItemCount)
        }
    }

    fun getItem(index: Int): Model {
        return models[index]
    }

    fun deleteItem(index: Int) {
        if (index in 0 until itemCount) {
            models.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindingViewHolder(holder, position)
    }

    fun setOnItemClickListener(myClickListener: MyClickListener?) {
        this.myClickListener = myClickListener
    }

    interface MyClickListener {
        fun onItemClick(position: Int, v: View?)
    }
}