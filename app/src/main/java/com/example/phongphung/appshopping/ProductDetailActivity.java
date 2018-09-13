package com.example.phongphung.appshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.model.Product;
import com.example.phongphung.appshopping.model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailActivity extends AppCompatActivity implements RatingDialogListener {

    @BindView(R.id.product_image_view)
    ImageView foodImageView;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.cart_button_fab)
    CounterFab cartButtonFab;
    @BindView(R.id.product_name_text_view)
    TextView foodNameTextView;
    @BindView(R.id.product_price_text_view)
    TextView foodPriceTextView;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.food_desc_text_view)
    TextView foodDescTextView;

    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    Product currentProduct;
    @BindView(R.id.rating_button_fab)
    FloatingActionButton ratingButtonFab;
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.show_comments_button)
    Button showCommentsButton;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/fontnew.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);


        //firebase
        database = FirebaseDatabase.getInstance();

        Log.d("abc", "onCreate: "+"abc");
        foods = database.getReference("Product");
        ratingTbl = database.getReference("Rating");

        //Initview
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

//        count item
        cartButtonFab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        //get food id from intent
        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodID");
            if (!foodId.isEmpty()) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    getDetailFood(foodId);
                    getRatingFood(foodId);
                } else {
                    Toast.makeText(this, "Vui lòng kiểm tra kết nối Internet !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0 ;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count ++;
                }
                if (count != 0){
                    float average = sum /count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(Product.class);

                Picasso.with(getBaseContext()).load(currentProduct.getImage()).into(foodImageView);
                collapsingToolbar.setTitle(currentProduct.getName());
                foodPriceTextView.setText(String.valueOf(currentProduct.getPrice()));
                foodNameTextView.setText(currentProduct.getName());
                foodDescTextView.setText(currentProduct.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.cart_button_fab, R.id.rating_button_fab,R.id.show_comments_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cart_button_fab:

                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        foodId,
                        currentProduct.getName(),
                        numberButton.getNumber(),
                        String.valueOf(currentProduct.getPrice()),
                        String.valueOf(currentProduct.getDiscount()),
                        currentProduct.getImage()
                ));

                Toast.makeText(this, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                cartButtonFab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
                break;

            case R.id.rating_button_fab:
                showRatingDialog();
                break;

            case R.id.show_comments_button:
                Intent intent = new Intent(this,ShowCommentActivity.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
                break;
        }
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Xác nhận")
                .setNegativeButtonText("Hủy")
                .setNoteDescriptions(Arrays.asList("Tồi tệ","Không tốt lắm", "Khá ổn", "Rất tốt", "Tuyệt vời"))
                .setDefaultRating(1)
                .setTitle("Đánh giá đồ ăn")
                .setDescription("Vui lòng đánh giá sản phẩm ")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Nhập vào đánh giá của bạn về sản phẩm ...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ProductDetailActivity.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //get rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);
        //Fix use can rate multiple times
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProductDetailActivity.this, "Cảm ơn bạn đã đánh giá !!!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
