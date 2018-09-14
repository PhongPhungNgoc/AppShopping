package com.example.phongphung.appshopping.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.viewHolder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder>{

    private ArrayList<Order> orderList;
    private LayoutInflater inflater;
    private Context context;

    public OrderDetailAdapter(ArrayList<Order> orderList, Context context) {
        this.orderList = orderList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_order_detail_actitity, parent, false);
        OrderDetailViewHolder orderDetailViewHolder = new OrderDetailViewHolder(view);
        return orderDetailViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        Picasso.with(context)
                .load(orderList.get(position).getImage())
                .centerCrop()
                .into(holder.imgDetail);
        holder.tvProductDetailID.setText(orderList.get(position).getProductId());
        holder.tvProductDetailName.setText(orderList.get(position).getProductName());
        holder.tvProductDetailPrice.setText(orderList.get(position).getPrice());
        holder.tvProductDetailQuantity.setText(orderList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
