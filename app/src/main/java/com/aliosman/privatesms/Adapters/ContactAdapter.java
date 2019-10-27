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

import com.aliosman.privatesms.AvatarView;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Model.Contact;
import com.aliosman.privatesms.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> items;
    private RecyclerViewListener<Contact> listener;

    public ContactAdapter(List<Contact> items, RecyclerViewListener<Contact> listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Contact item = items.get(i);
        viewHolder.avatarView.SetUser(item);
        viewHolder.name.setText(item.getNameText());
        viewHolder.number.setText(item.getNumberText());
        if (listener != null)
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.Onclick(item);
                }
            });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Contact> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AvatarView avatarView;
        private TextView name, number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.contact_item_avatar);
            number = itemView.findViewById(R.id.contact_item_number);
            name = itemView.findViewById(R.id.contact_item_name);
        }
    }
}
