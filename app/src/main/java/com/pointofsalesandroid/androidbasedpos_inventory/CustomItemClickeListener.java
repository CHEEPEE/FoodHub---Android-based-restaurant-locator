package com.pointofsalesandroid.androidbasedpos_inventory;

import android.view.View;

/**
 * Created by Keji's Lab on 29/11/2017.
 */

public class CustomItemClickeListener {
    public interface CustomItemClickListener {
        public void onItemClick(View v, int position);
    }
}
