package com.example.phongphung.appshopping.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.interFace.PositionClickListener;
import com.example.phongphung.appshopping.model.OrderRequest;
import com.example.phongphung.appshopping.viewHolder.OrderViewHolder;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private LayoutInflater inflater;
    private List<OrderRequest> orderList;

    private PositionClickListener positionClickListener;

    public OrderAdapter(List<OrderRequest> orderList, Context context) {
        this.orderList = orderList;
        inflater = LayoutInflater.from(context);
    }

    public void setPositionClickListener(PositionClickListener positionClickListener) {
        this.positionClickListener = positionClickListener;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.order_item_layout, parent, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, final int position) {
        OrderRequest orderRequest = orderList.get(position);
        holder.bindData(orderRequest);
        if (positionClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    positionClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
