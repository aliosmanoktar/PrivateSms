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
import com.aliosman.privatesms.Listener.RecyclerViewListener;
import com.aliosman.privatesms.Model.Conversation;
import com.aliosman.privatesms.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> items;
    private RecyclerViewListener<Conversation> listener;

    public ConversationAdapter(List<Conversation> items, RecyclerViewListener<Conversation> listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_conversation_list,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Conversation item = items.get(i);
        viewHolder.message.setText(item.getMessage());
        viewHolder.name.setText(item.getContact().getNameText());
        viewHolder.avatarView.SetUser(item.getContact().getName());
        viewHolder.date.setText(getDateText(item.getDate()));
    }
    private String getDateText(long date){
        return new SimpleDateFormat("dd MMM").format(new Date(date));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{

        private TextView name;
        private TextView message;
        private AvatarView avatarView;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.conversation_item_name);
            message=itemView.findViewById(R.id.conversation_item_message);
            avatarView=itemView.findViewById(R.id.conversation_item_avatar);
            date=itemView.findViewById(R.id.conversation_item_date);
        }
    }
}
