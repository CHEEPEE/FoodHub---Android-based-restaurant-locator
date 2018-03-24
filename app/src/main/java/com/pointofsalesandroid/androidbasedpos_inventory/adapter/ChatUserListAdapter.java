package com.pointofsalesandroid.androidbasedpos_inventory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ChatUserListModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.MyViewHolder> {

    private ArrayList<ChatUserListModel> chatUserListModelArrayList;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView displayname;

        public CircleImageView usericon;
        public MyViewHolder(View view){
            super(view);
            usericon = (CircleImageView) view.findViewById(R.id.usericon);
               displayname = (TextView) view.findViewById(R.id.displayname);
        }
    }

    public ChatUserListAdapter(Context c, ArrayList<ChatUserListModel> categoryItem){
        this.chatUserListModelArrayList = categoryItem;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatUserListAdapter.MyViewHolder holder, final int position) {
        holder.displayname.setText(chatUserListModelArrayList.get(position).getDisplayName());
        holder.displayname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position);
            }
        });
        GlideApp.with(context).load(chatUserListModelArrayList.get(position).getUserPhoto()).placeholder(R.drawable.image_placeholder).centerCrop().into(holder.usericon);
    }
    @Override
    public int getItemCount() {
        return chatUserListModelArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int posistion);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private OnItemLongClickListener monItemLongClickListener;

    public void setonItemLongClickListener(OnItemLongClickListener monItemLongClickListener){
        this.monItemLongClickListener = monItemLongClickListener;
    }
}
