package com.example.phongphung.appshopping.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.interFace.ItemClickListener;


public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productNameTextView, productPriceTextView;
    public ImageView productImageView, favoriteImageView, shareImageView, quickCartImageView;

    private ItemClickListener itemClickListener;




    public ProductViewHolder(View itemView) {
        super(itemView);

        productNameTextView = itemView.findViewById(R.id.product_name_text_view);
        productImageView = itemView.findViewById(R.id.product_image_view);
        favoriteImageView = itemView.findViewById(R.id.fav_image);
        shareImageView = itemView.findViewById(R.id.share_button_image_view);
        productPriceTextView = itemView.findViewById(R.id.product_price_text_view);
        quickCartImageView = itemView.findViewById(R.id.fav_quick_cart_image_view);


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
