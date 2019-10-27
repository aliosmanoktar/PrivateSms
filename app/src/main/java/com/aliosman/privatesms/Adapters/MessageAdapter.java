/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;

import java.util.List;

public class MessageAdapter extends BaseSelectedAdapter<Message, MessageAdapter.ViewHolder> {
    private final List<Message> items;

    public MessageAdapter(List<Message> items, RecylerSelectedListener<Message> selectedListener) {
        super(items);
        setSelectedListener(selectedListener);
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isSent() ? R.layout.item_message_out : R.layout.item_message_in;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        Message item = items.get(i);
        viewHolder.message.setText(item.getMessage());
        viewHolder.date.setText(item.getTimeString());
        if (isSelect(item)) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getResources().getColor(R.color.colorAccent));
        } else {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getResources().getColor(R.color.transparent));
        }
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setOnLongClickListener(this);
    }

    @Override
    public int GetPosition(Message item) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).getID() == item.getID())
                return i;
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView message, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.item_message_body);
            date = itemView.findViewById(R.id.item_message_date);
        }
    }
}
