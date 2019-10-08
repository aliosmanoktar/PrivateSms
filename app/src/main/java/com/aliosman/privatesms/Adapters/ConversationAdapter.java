/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliosman.privatesms.AvatarView;
import com.aliosman.privatesms.Listener.Interfaces.RecyclerViewListener;
import com.aliosman.privatesms.Listener.Interfaces.RecylerSelectedListener;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;

import java.util.List;

public class ConversationAdapter extends BaseSelectedAdapter<Conversation, ConversationAdapter.ViewHolder> {

    private List<Conversation> items;
    private String TAG = getClass().getName();
    private RecyclerViewListener<Conversation> listener;

    public ConversationAdapter(List<Conversation> items, RecyclerViewListener<Conversation> listener, RecylerSelectedListener selectedListener) {
        super(items);
        this.items = items;
        this.listener = listener;
        setClicklistener(listener);
        setSelectedListener(selectedListener);
    }

    public void setItems(List<Conversation> items) {
        super.setItems(items);
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_conversation_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        final Conversation item = items.get(i);
        viewHolder.message.setText(item.isSent() ? "Sen: " + item.getMessage() : item.getMessage());
        if (!item.isRead()) {
            viewHolder.message.setTypeface(null, Typeface.BOLD);
            viewHolder.count.setVisibility(View.VISIBLE);
            viewHolder.count.setText(item.getCount() + "");
        } else {
            viewHolder.count.setVisibility(View.GONE);
            viewHolder.message.setTypeface(null);
        }
        if (item.isPinned())
            viewHolder.pinned.setVisibility(View.VISIBLE);
        else viewHolder.pinned.setVisibility(View.GONE);
        viewHolder.itemView.setTag(item);
        viewHolder.name.setText(item.getContact().getNameText());
        viewHolder.avatarView.SetUser(item.getContact());
        viewHolder.date.setText(item.getTimeString());

        if (isSelect(item)) {
            viewHolder.itemView.setBackgroundColor(viewHolder.avatarView.getResources().getColor(R.color.dark));
        } else {
            viewHolder.itemView.setBackgroundColor(viewHolder.avatarView.getResources().getColor(R.color.transparent));
        }
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setOnLongClickListener(this);
        viewHolder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getContact().getLookupKey().isEmpty()) {
                    Uri uri = Uri.parse("tel: " + item.getContact().getNumber());
                    Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, uri);

                    if (intent.resolveActivity(v.getContext().getPackageManager()) == null) {
                        intent = new Intent(Intent.ACTION_INSERT)
                                .setType(ContactsContract.Contacts.CONTENT_TYPE)
                                .putExtra(ContactsContract.Intents.Insert.PHONE, item.getContact().getNumber());
                    }
                    v.getContext().startActivity(intent);
                } else {
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, item.getContact().getLookupKey());
                    ContactsContract.QuickContact.showQuickContact(v.getContext(), v, uri,
                            ContactsContract.QuickContact.MODE_MEDIUM, null);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView message;
        private AvatarView avatarView;
        private TextView date;
        private TextView count;
        private ImageView pinned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.conversation_item_name);
            message = itemView.findViewById(R.id.conversation_item_message);
            avatarView = itemView.findViewById(R.id.conversation_item_avatar);
            date = itemView.findViewById(R.id.conversation_item_date);
            count = itemView.findViewById(R.id.conversation_item_count);
            pinned = itemView.findViewById(R.id.conversation_item_pinned);
        }
    }
}
