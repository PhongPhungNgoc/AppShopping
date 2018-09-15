package com.example.phongphung.appshopping.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.interFace.ItemClickListener;
import com.example.phongphung.appshopping.interFace.PositionClickListener;
import com.example.phongphung.appshopping.model.OrderRequest;


public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView tvOrderAmount;
    public TextView tvOrderPhone;
    public TextView tvOrderStatus;
    public TextView tvOrderAdress;

    public OrderViewHolder(View itemView) {
        super(itemView);
        tvOrderPhone = itemView.findViewById(R.id.order_phone_text_view);
        tvOrderAmount = itemView.findViewById(R.id.order_amount_text_view);
        tvOrderStatus = itemView.findViewById(R.id.order_status_text_view);
        tvOrderAdress = itemView.findViewById(R.id.order_address_text_view);
    }

    public void bindData(OrderRequest orderRequest){
        tvOrderPhone.setText(orderRequest.getPhone());
        tvOrderAmount.setText(String.valueOf(orderRequest.getTotal()));
        tvOrderStatus.setText(orderRequest.getStatus());
        tvOrderAdress.setText(orderRequest.getAdress());
    }
}
