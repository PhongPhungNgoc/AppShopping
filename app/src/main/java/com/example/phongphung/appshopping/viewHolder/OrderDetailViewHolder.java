package com.example.phongphung.appshopping.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phongphung.appshopping.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder{
    public ImageView imgDetail;
    public TextView tvProductDetailID;
    public TextView tvProductDetailName;
    public TextView tvProductDetailPrice;
    public TextView tvProductDetailQuantity;

    public OrderDetailViewHolder(View itemView) {
        super(itemView);
        imgDetail = itemView.findViewById(R.id.img_orderDetail);
        tvProductDetailID = itemView.findViewById(R.id.tv_detailID);
        tvProductDetailName = itemView.findViewById(R.id.tv_detailName);
        tvProductDetailPrice = itemView.findViewById(R.id.tv_detailPrice);
        tvProductDetailQuantity = itemView.findViewById(R.id.tv_detailQuantity);
    }
}
