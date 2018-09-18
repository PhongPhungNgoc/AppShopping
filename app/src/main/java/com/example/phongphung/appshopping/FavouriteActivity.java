package com.example.phongphung.appshopping;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.helper.RecyclerItemTouchHelper;
import com.example.phongphung.appshopping.interFace.RecyclerItemTouchHelperListener;
import com.example.phongphung.appshopping.model.Favorite;
import com.example.phongphung.appshopping.viewHolder.FavoriteViewHolder;
import com.example.phongphung.appshopping.adapter.FavouriteAdapter;


public class FavouriteActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavouriteAdapter adapter;
    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


        rootLayout = findViewById(R.id.root_layout);
        //Load menu
        recyclerView = findViewById(R.id.rcvFavorite);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        loadFavorites();

    }

    private void loadFavorites() {
        adapter = new FavouriteAdapter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoriteViewHolder){
            String name = ((FavouriteAdapter)recyclerView.getAdapter()).getItem(position).getProductName();

            final Favorite deleteItem = (((FavouriteAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()));
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFromFavorites(deleteItem.getProductID(), Common.currentUser.getPhone());

            Snackbar snackbar = Snackbar.make(rootLayout, name + " xóa khỏi ưa thích", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hủy", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavorites(deleteItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
