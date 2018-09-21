package com.example.phongphung.appshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.phongphung.appshopping.ProductDetailActivity;
import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.interFace.ItemClickListener;
import com.example.phongphung.appshopping.model.Favorite;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.viewHolder.FavoriteViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private Context context;
    private List<Favorite> favoriteList;

    public FavouriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.favorite_item,parent,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder viewHolder, final int position) {
        viewHolder.favoriteNameTextView.setText(favoriteList.get(position).getProductName());
        viewHolder.favoritePriceTextView.setText(String.format("$ %s", favoriteList.get(position).getProductPrice()));
        Picasso.with(context)
                .load(favoriteList.get(position).getProductImage())
                .into(viewHolder.productImageView);

        viewHolder.quickCartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFoodExists = new Database(context).checkIfFoodExists(favoriteList.get(position).getProductID(), Common.currentUser.getPhone());

                if(!isFoodExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoriteList.get(position).getProductID(),
                            favoriteList.get(position).getProductName(),
                            "1",
                            favoriteList.get(position).getProductPrice(),
                            favoriteList.get(position).getProductDiscount(),
                            favoriteList.get(position).getProductImage()
                    ));

                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(), favoriteList.get(position).getProductID());
                }
                Toast.makeText(context, "Sản phẩm đã thêm vào giỏ hàng !!!", Toast.LENGTH_SHORT).show();
            }
        });


        final Favorite favorite = favoriteList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener(){
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("FoodID", favoriteList.get(position).getProductID()); //send foodId to new activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public void removeItem(int position){
        favoriteList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorite item, int position){
        favoriteList.add(position, item);
        notifyItemInserted(position);
    }

    public Favorite getItem(int position){
        return favoriteList.get(position);
    }
}
