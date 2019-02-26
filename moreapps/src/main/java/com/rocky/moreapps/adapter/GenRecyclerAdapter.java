package com.rocky.moreapps.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class GenRecyclerAdapter
        <ViewHolder extends RecyclerView.ViewHolder, Model>
        extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Model> models;
    private GenRecyclerAdapter.MyClickListener myClickListener;

    GenRecyclerAdapter(ArrayList<Model> models) {
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
        getModels().clear();
        notifyDataSetChanged();
    }

    public void replaceAll(ArrayList<Model> models) {
        getModels().clear();
        getModels().addAll(models);
        notifyDataSetChanged();
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

    public void setOnItemClickListener(GenRecyclerAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
