package com.aliosman.privatesms.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aliosman.privatesms.AvatarView;
import com.aliosman.privatesms.R;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
