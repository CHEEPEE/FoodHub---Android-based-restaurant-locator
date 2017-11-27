package com.pointofsalesandroid.androidbasedpos_inventory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class RecycleItemProductAdapter extends RecyclerView.Adapter<RecycleItemProductAdapter.MyViewHolder> {

    private ArrayList<ProductItemGridModel> categoryItem = new ArrayList<>();
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgBanner;
        public TextView itemName,itemCategory,itemPrice;
        public MyViewHolder(View view){
            super(view);

            itemName = (TextView) view.findViewById(R.id.label_itemName);
            itemCategory = (TextView) view.findViewById(R.id.label_itemCode);
            itemPrice = (TextView) view.findViewById(R.id.label_item_price);
            imgBanner = (ImageView) view.findViewById(R.id.imgBanner);

        }
    }

    public RecycleItemProductAdapter(Context c, ArrayList<ProductItemGridModel> categoryItem){
        this.categoryItem = categoryItem;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.resturant_item_grid,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecycleItemProductAdapter.MyViewHolder holder, final int position) {
        ProductItemGridModel productItemGridModel = categoryItem.get(position);
        holder.itemName.setText(productItemGridModel.getiName());
        holder.itemCategory.setText(productItemGridModel.getItemCategory());
        holder.itemPrice.setText(productItemGridModel.getItemPrice());
        Glide.with(context).load(Utils.testImageUrl).into(holder.imgBanner);

    }

    @Override
    public int getItemCount() {
        return categoryItem.size();
    }
}