/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.R;

import java.util.ArrayList;
import java.util.List;

/***
 * Hata Var Düzeltilmesi gerek
 * @param <T>
 * @param <VH>
 */
public abstract class BaseSelectedAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener, View.OnLongClickListener {
    private List<T> selected;
    private List<T> items;
    private boolean select = false;
    private String TAG = getClass().getName();
    private RecyclerViewListener<T> clicklistener;
    private RecylerSelectedListener selectedListener;


    public BaseSelectedAdapter(List<T> items) {
        this.items = items;
        selected = new ArrayList<>();
    }

    public void setItems(List<T> items) {
        /*this.items = items;
        RemoveSelected();*/
    }

    public void setClicklistener(RecyclerViewListener<T> clicklistener) {
        this.clicklistener = clicklistener;
    }

    public void setSelectedListener(RecylerSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        vh.itemView.setTag(R.string.tag_position, i);
        vh.itemView.setTag(R.string.tag_item, items.get(i));
    }

    @Override
    public void onClick(View v) {
        if (select) {
            onLongClick(v);
        } else {
            if (clicklistener != null)
                clicklistener.Onclick((T) v.getTag(R.string.tag_item));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (selectedListener == null)
            return false;
        T item = (T) v.getTag(R.string.tag_item);
        int position = (int) v.getTag(R.string.tag_position);
        if (!select) {
            selectedListener.SelectedStart();
            select = true;
        }
        if (isSelect(item)) {
            removeSelect(item, position);
        } else {
            setSelect(item, position);
        }
        selectedListener.Selected(selected.size(), position, selected);
        return false;
    }

    protected void setSelect(T item, int position) {
        selected.add(item);
        notifyItemChanged(position);
        Log.e(TAG, "setSelect: " + position);
    }

    protected void removeSelect(T item, int position) {
        Log.e(TAG, "removeSelect: " + position);
        selected.remove(item);
        if (selected.size() == 0)
            RemoveSelected();
        notifyItemChanged(position);
    }

    /**
     * Contains ile dene
     *
     * @param item
     * @return
     */
    protected boolean isSelect(T item) {
        return selected.contains(item);
        /*for (T temp : selected)
            if (temp.equals(item))
                return true;
        return false;*/
    }

    protected void Select(T item, int position, boolean select) {

    }

    /**
     * Güncellenecek
     * Pozisyonlara göre silme yapılacak
     */
    public void RemoveSelected() {
        Log.e(TAG, "RemoveSelected: ");
        selected.clear();
        select = false;
        notifyDataSetChanged();
        if (selectedListener != null)
            selectedListener.SelectedEnded(null);
    }

    public void EndSelect() {
        Log.e(TAG, "EndSelect: ");
        select = false;
        if (selectedListener != null)
            selectedListener.SelectedEnded(selected);
        selected = new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean isSelect() {
        return select;
    }

    public List<T> getSelected() {
        return selected;
    }
}