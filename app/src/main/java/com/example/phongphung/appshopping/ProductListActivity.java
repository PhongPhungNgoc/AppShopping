package com.example.phongphung.appshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.interFace.ItemClickListener;
import com.example.phongphung.appshopping.model.Favorite;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.model.Product;
import com.example.phongphung.appshopping.viewHolder.ProductViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductListActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference productList;

    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categoryId = "";

    //Search functionality
    FirebaseRecyclerAdapter<Product, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;

    //Facebook Share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    //Create Target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create Photo from Bitmap
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/login_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_product_list);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Init firebase
        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Product");

        localDB = new Database(this);

        swipeRefreshLayout = findViewById(R.id.swipe_layout_food);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get intent here
                if(getIntent() != null) {
                    categoryId = getIntent().getStringExtra("CategoryID");
                }
                if(!categoryId.isEmpty() && categoryId != null){
                    //check internet connection and load foods list
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadListFood(categoryId);
                    }  else {
                        Toast.makeText(ProductListActivity.this, "Vui lòng kiểm tra kết nối Internet !!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get intentn here
                if(getIntent() != null){
                    categoryId = getIntent().getStringExtra("CategoryID");
                    if(!categoryId.isEmpty() && categoryId != null){
                        if(Common.isConnectedToInternet(getBaseContext())) {
                            loadListFood(categoryId);
                        }  else {
                            Toast.makeText(ProductListActivity.this, "Vui lòng kiểm tra kết nối Internet !!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    //Search
                    materialSearchBar = findViewById(R.id.search_bar);
                    materialSearchBar.setHint("Nhập vào sản phẩm");
                    materialSearchBar.setSpeechMode(false);
                    loadSuggest();
                    materialSearchBar.setCardViewElevation(10);
                    materialSearchBar.addTextChangeListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            List<String> suggest = new ArrayList<>();
                            for(String search : suggestList){
                                if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                                    suggest.add(search);
                                }
                            }
                            materialSearchBar.setLastSuggestions(suggest);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                        @Override
                        public void onSearchStateChanged(boolean enabled) {
                            //When search bar is close
                            //Restore original suggest adapter
                            if(!enabled){
                                recyclerView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onSearchConfirmed(CharSequence text) {
                            //When search finish
                            //Show result of search adapter
                            startSearch(text);
                        }

                        @Override
                        public void onButtonClicked(int buttonCode) {

                        }
                    });
                }
            }
        });

        //Load menu
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void startSearch(CharSequence text) {
        //Create query by name
        Query searchByName = productList.orderByChild("name").equalTo(text.toString());
        //Create Options with query
        FirebaseRecyclerOptions<Product> foodOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(searchByName,Product.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder viewHolder, int position, @NonNull Product model) {
                viewHolder.productNameTextView.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.productImageView);

                final Product local = model;
                viewHolder.setItemClickListener(new ItemClickListener(){
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activiyt
                        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                        intent.putExtra("foodId", searchAdapter.getRef(position).getKey()); //send foodId to new activity
                        startActivity(intent);
                    }
                });
            }

            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item,parent,false);
                return new ProductViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter); //Set adapter for Recycler View is Search result.
    }

    private void loadSuggest() {
        productList.orderByChild("idCategory").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Product item = postSnapshot.getValue(Product.class);
                            suggestList.add(item.getName()); //Add name of foods
                        }

                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        //Create query by category Id
        Query searchByCategory = productList.orderByChild("idCategory").equalTo(categoryId);
        //Create Options with query
        FirebaseRecyclerOptions<Product> foodOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(searchByCategory,Product.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder viewHolder, final int position, @NonNull final Product model) {
                viewHolder.productNameTextView.setText(model.getName());
                viewHolder.productPriceTextView.setText(String.format("$ %s", model.getPrice()));
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.productImageView);

                //Quick orders

                viewHolder.quickCartImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isFoodExists = new Database(getBaseContext()).checkIfFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());

                        if(!isFoodExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    String.valueOf(model.getPrice()),
                                    String.valueOf(model.getDiscount()),
                                    model.getImage()
                            ));

                        } else {

                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                        }
                        Toast.makeText(ProductListActivity.this, "Sản phẩm đã thêm vào giỏ hàng !!!", Toast.LENGTH_SHORT).show();
                    }
                });

                //Add favorites
                if(localDB.isFavorite(adapter.getRef(position).getKey(), Common.currentUser.getPhone())){
                    viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp); //poner corazon redondo.
                }

                //Click to Share
                viewHolder.shareImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getBaseContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //Click to change status of Favorite
                viewHolder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favorite favorite = new Favorite();
                        favorite.setProductID(adapter.getRef(position).getKey());
                        favorite.setProductName(model.getName());
                        favorite.setProductDescription(model.getDescription());
                        favorite.setProductDiscount(String.valueOf(model.getDiscount()));
                        favorite.setProductImage(model.getImage());
                        favorite.setProductCategoryID(String.valueOf(model.getIdCategory()));
                        favorite.setUserPhone(Common.currentUser.getPhone());
                        favorite.setProductPrice(String.valueOf(model.getPrice()));

                        if(!localDB.isFavorite(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                            localDB.addToFavorites(favorite);
                            viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductListActivity.this, model.getName()+" thêm vào danh sách ưu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ProductListActivity.this, model.getName()+" xóa khỏi danh sách ưu thích", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener(){
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activiyt
                        Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                        intent.putExtra("FoodID", adapter.getRef(position).getKey()); //send foodId to new activity
                        startActivity(intent);
                    }
                });
            }


            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item,parent,false);
                return new ProductViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();
        }
    }
}

