package com.example.phongphung.appshopping.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.interFace.ItemClickListener;


public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView favoriteNameTextView, favoritePriceTextView;
    public ImageView productImageView, quickCartImageView;

    public RelativeLayout backgroundView;
    public LinearLayout foregroundView;

    private ItemClickListener itemClickListener;



    public FavoriteViewHolder(View itemView) {
        super(itemView);

        favoriteNameTextView = itemView.findViewById(R.id.favorite_name_text_view);
        favoritePriceTextView = itemView.findViewById(R.id.favorite_price_text_view);
        productImageView = itemView.findViewById(R.id.favorite_image_view);
        quickCartImageView = itemView.findViewById(R.id.fav_quick_cart_image_view);

        backgroundView = itemView.findViewById(R.id.background_view_relative_layout);
        foregroundView = itemView.findViewById(R.id.view_foreground);


        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
