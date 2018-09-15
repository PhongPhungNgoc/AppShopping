package com.example.phongphung.appshopping.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.phongphung.appshopping.CartActivity;
import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.viewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private CartActivity cartActivity;

    public CartAdapter(List<Order> listData, CartActivity cartActivity) {
        this.listData = listData;
        this.cartActivity = cartActivity;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cartActivity);
        View itemView = inflater.inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {
        //load image in cart item.
        Picasso.with(cartActivity.getBaseContext())
                .load(listData.get(position).getImage())
                .into(holder.cartImageView);

        holder.quantityCartButton.setNumber(listData.get(position).getQuantity()); //setear quantity elegido en el button - +
        holder.quantityCartButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cartActivity).updateCart(order);

                //Update extTotal
                //Calculate total price
                float total = 0;
                List<Order> orders = new Database(cartActivity).getCarts(Common.currentUser.getPhone());
                for(Order item: orders){
                    total += (Float.parseFloat(item.getPrice()))*(Float.parseFloat(item.getQuantity()));
                }
                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                cartActivity.totalPriceTextView.setText(fmt.format(total));
            }
        });

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        float price = (Float.parseFloat(listData.get(position).getPrice()))*(Float.parseFloat(listData.get(position).getQuantity()));
        holder.cartPriceTextView.setText(fmt.format(price));
        holder.cartNameTextView.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){
        return listData.get(position);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position){
        listData.add(position, item);
        notifyItemInserted(position);
    }
}
