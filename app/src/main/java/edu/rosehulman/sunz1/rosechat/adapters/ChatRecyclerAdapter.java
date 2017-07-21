package edu.rosehulman.sunz1.rosechat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sun on 7/21/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ChatRecyclerAdapter(View itemView) {
        super(itemView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder{

        public MyChatViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class OthersChatViewHolder extends RecyclerView.ViewHolder{

        public OthersChatViewHolder(View itemView) {
            super(itemView);
        }
    }
}
