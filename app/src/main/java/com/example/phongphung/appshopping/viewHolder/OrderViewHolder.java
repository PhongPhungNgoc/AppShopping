package com.example.phongphung.appshopping.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.interFace.ItemClickListener;


public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOrderID,tvOrderStatus,tvOrderPhone,tvOrderAdress;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(View itemView) {
        super(itemView);
        tvOrderID = itemView.findViewById(R.id.order_id_text_view);
        tvOrderPhone = itemView.findViewById(R.id.order_phone_text_view);
        tvOrderStatus = itemView.findViewById(R.id.order_status_text_view);
        tvOrderAdress = itemView.findViewById(R.id.order_address_text_view);
    }

}
