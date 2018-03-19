package com.pointofsalesandroid.androidbasedpos_inventory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.RestaurantMenuRatingMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ProductItemGridModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ReportRestaurantMenusAdapter extends RecyclerView.Adapter<ReportRestaurantMenusAdapter.MyViewHolder> {

    private ArrayList<ProductItemGridModel> productItemGridModels;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        RatingBar menusRatingbar;
        CircleImageView itemIcon;

        TextView menuName;
        public MyViewHolder(View view){
            super(view);
            itemIcon = (CircleImageView) view.findViewById(R.id.ic_menu);
            menuName = (TextView) view.findViewById(R.id.menu_name);
            menusRatingbar = (RatingBar) view.findViewById(R.id.rating_bar_item);

        }
    }

    public ReportRestaurantMenusAdapter(Context c, ArrayList<ProductItemGridModel> menuItems){
        this.productItemGridModels = menuItems;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_report,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReportRestaurantMenusAdapter.MyViewHolder holder, final int position) {
        ProductItemGridModel productItemGridModel = productItemGridModels.get(position);
        holder.menuName.setText(productItemGridModel.getiName());


        final DatabaseReference getMenusRatings = FirebaseDatabase.getInstance().getReference().child(Utils.restaurantMenusUserRating).child(FirebaseAuth.getInstance().getUid()).child(productItemGridModel.getItemKey());
        getMenusRatings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int ratingTotal = 0;
                ArrayList<Float> ratingArray  = new ArrayList<>();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    RestaurantMenuRatingMapModel restaurantMenuRatingMapModel = dataSnapshot1.getValue(RestaurantMenuRatingMapModel.class);
                    ratingArray.add(restaurantMenuRatingMapModel.userRating);

                }
                for (int i = 0;i<ratingArray.size();i++){
                    ratingTotal += ratingArray.get(i);

                }

                Float aveRating = Float.parseFloat(ratingTotal+"")/ratingArray.size();
                System.out.println(aveRating);
                holder.menusRatingbar.setRating(aveRating);
                if (aveRating.toString().equals("NaN")){
//                    holder.lblReviewNumber.setText("0");
                }else {
//                    holder.lblReviewNumber.setText(aveRating.toString().substring(0,1));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        GlideApp.with(context).load(productItemGridModel.getItemBannerUrl()).into(holder.itemIcon);
//        holder.displayname.setText(chatUserListModelArrayList.get(position).getDisplayName());
//        holder.displayname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnItemClickLitener.onItemClick(v,position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return productItemGridModels.size();
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
