package com.rocky.moreapps.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class MoreAppsBaseAdapter
        <ViewHolder extends RecyclerView.ViewHolder, Model>
        extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Model> models;

    private MoreAppsBaseAdapter.MyClickListener myClickListener;

    MoreAppsBaseAdapter(ArrayList<Model> models) {
        this.models = models;
    }

    protected abstract ViewHolder creatingViewHolder(ViewGroup parent, int viewType);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return creatingViewHolder(parent, viewType);
    }

    protected abstract void bindingViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void addAll(ArrayList<Model> models) {
        int position = getItemCount();
        this.getModels().addAll(models);
        notifyItemRangeInserted(position, models.size());
    }

    public void addItem(Model model, int index) {
        getModels().add(model);
        notifyItemInserted(index);
    }

    public void addItem(Model model) {
        getModels().add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    private ArrayList<Model> getModels() {
        return models;
    }

    public void deleteAll() {
        int itemCount = getItemCount();
        getModels().clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public void replaceAll(ArrayList<Model> models) {
        if (models != null) {
            int oldItemCount = getItemCount();
            int newItemCount = models.size();

            getModels().clear();
            getModels().addAll(models);

            if (oldItemCount == 0) {
                notifyItemRangeInserted(oldItemCount, newItemCount - oldItemCount);
            } else if (newItemCount < oldItemCount) {
                notifyItemRangeChanged(0, newItemCount);
                notifyItemRangeRemoved(newItemCount, oldItemCount - newItemCount);
            } else {
                notifyItemRangeChanged(0, oldItemCount);
                notifyItemRangeInserted(oldItemCount, newItemCount - oldItemCount);
            }
        } else {
            int oldItemCount = getItemCount();
            getModels().clear();
            notifyItemRangeRemoved(0, oldItemCount);
        }
    }

    public Model getItem(int index) {
        return getModels().get(index);
    }

    public void deleteItem(int index) {
        if (index >= 0 && index < getItemCount()) {
            getModels().remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindingViewHolder(holder, position);
    }

    MyClickListener getMyClickListener() {
        return myClickListener;
    }

    public void setOnItemClickListener(MoreAppsBaseAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
