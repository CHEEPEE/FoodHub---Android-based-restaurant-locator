package com.pointofsalesandroid.androidbasedpos_inventory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.RestaurantLocationMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.CategoryModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class RecycleItemProductAdapter extends RecyclerView.Adapter<RecycleItemProductAdapter.MyViewHolder> {
    ArrayList<CategoryModel> categoryItemList = new ArrayList<>();
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private ArrayList<ProductItemGridModel> categoryItem = new ArrayList<>();
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView banner,ic_delete,ic_edit;
        public TextView itemName,itemCategory,itemPrice;
        public MyViewHolder(View view){
            super(view);
            ic_delete = (ImageView) view.findViewById(R.id.ic_delete);
            ic_edit = (ImageView)view.findViewById(R.id.ic_edit);
            itemName = (TextView) view.findViewById(R.id.label_itemName);
            itemCategory = (TextView) view.findViewById(R.id.label_itemCode);
            itemPrice = (TextView) view.findViewById(R.id.label_item_price);
            banner = (ImageView) view.findViewById(R.id.imgBanner);

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
    public void onBindViewHolder(final RecycleItemProductAdapter.MyViewHolder holder, final int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            RestaurantLocationMapModel categoryMapModel=dataSnapshot1.getValue(RestaurantLocationMapModel.class);
                            try {
                                if (categoryMapModel.key.equals(categoryItem.get(position).getItemCategory())) {
                                    holder.itemCategory.setText(categoryMapModel.restauarantAddress);
                                }

                            }catch (Exception error){
                                Log.d("Error",error.toString());
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        ProductItemGridModel productItemGridModel = categoryItem.get(position);
        holder.itemName.setText(productItemGridModel.getiName());
        //holder.itemCategory.setText(productItemGridModel.getItemCategory());
        holder.itemPrice.setText(productItemGridModel.getItemPrice());

         GlideApp.with(context).load(productItemGridModel.getItemBannerUrl()).override(300,300).into(holder.banner);

            holder.ic_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView,position,"edit");

            }
        });
        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView,position,"delete");
            }
        });
    }



    public interface OnItemClickLitener {
        void onItemClick(View view, int position,String text);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemCount() {
        return categoryItem.size();
    }


}