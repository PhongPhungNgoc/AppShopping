package com.example.phongphung.appshopping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.common.Config;
import com.example.phongphung.appshopping.database.Database;
import com.example.phongphung.appshopping.helper.RecyclerItemTouchHelper;
import com.example.phongphung.appshopping.interFace.RecyclerItemTouchHelperListener;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.model.OrderRequest;
import com.example.phongphung.appshopping.remote.IGoogleService;
import com.example.phongphung.appshopping.adapter.CartAdapter;
import com.example.phongphung.appshopping.viewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity implements
        RecyclerItemTouchHelperListener {
    private static final String TAG = "CartActivity";
    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView totalPriceTextView;
    Button placeOrderButton;

    List<Order> orders = new ArrayList<>();
    CartAdapter adapter;

    Place shippingAddress;

    //Paypal payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address, comment;

    RelativeLayout rootLayout;

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

        setContentView(R.layout.activity_cart);

        rootLayout = findViewById(R.id.rootLayout);
        rootLayout.setBackgroundResource(R.drawable.background);


        //Init firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Order");

        //Initi Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        //Init
        recyclerView = findViewById(R.id.list_cart_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        totalPriceTextView = findViewById(R.id.totalPrice_text_view);
        placeOrderButton = findViewById(R.id.place_order_button);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders.size() > 0) {
                    showAlertDialog();
                } else {
                    Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Common.isConnectedToInternet(this)) {
            loadFoodList();
        } else {
            Toast.makeText(this, "Kiểm tra kết nối Internet!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Hoàn tất thanh toán !");
        alertDialog.setMessage("Nhập vào địa chỉ : ");

        final LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final PlaceAutocompleteFragment addressEdit = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        addressEdit.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText) addressEdit.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your address");

        ((EditText) addressEdit.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        addressEdit.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
                Log.d(TAG, "onPlaceSelected: " + place.toString());
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "onError: " + status.getStatusMessage());
            }
        });


        final MaterialEditText commentEditText = order_address_comment.findViewById(R.id.order_comment_edit_text);

        //Radio
        final RadioButton codRadioButton = order_address_comment.findViewById(R.id.cod_radio_button);
        final RadioButton paypalRadioButton = order_address_comment.findViewById(R.id.paypal_radio_button);


        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    if (shippingAddress != null) {
                        address = shippingAddress.getAddress().toString();
                    } else {
                        Toast.makeText(CartActivity.this, "Vui lòng nhập vào địa chỉ nhận hàng !!!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(CartActivity.this, "Vui lòng nhập vào địa chỉ nhận hàng !!!", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }

                comment = commentEditText.getText().toString();

                //check payment
                if (!codRadioButton.isChecked() && !paypalRadioButton.isChecked()) {

                    Toast.makeText(CartActivity.this, "Vui lòng chọn phương thức thanh toán !!!", Toast.LENGTH_SHORT).show();

                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                } else if (paypalRadioButton.isChecked()) {

                    String formatAmount = totalPriceTextView.getText().toString()
                            .replace("$", "")
                            .replace(",", "");

                    //Show Paypal to payment
                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                            "USD",
                            "Order App",
                            PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                } else if (codRadioButton.isChecked()) {
                    OrderRequest orderRequest = new OrderRequest(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            totalPriceTextView.getText().toString(),
                            comment,
                            "COD",
                            "Unpaid",
//                            String.format("%s %s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                            String.format("%s %s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                            orders
                    );

                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(orderRequest);
                    //Delete Cart
                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    Toast.makeText(CartActivity.this, "Cảm ơn , Đơn hàng đang được xử lý !!!", Toast.LENGTH_LONG).show();

                    finish();

                }
                //Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);
                        String paymentState = jsonObject.getJSONObject("response").getString("state");

                        OrderRequest orderRequest = new OrderRequest(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                totalPriceTextView.getText().toString(),
                                comment,
                                "Paypal",
                                paymentState,
                                String.format("%s %s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                                orders
                        );

                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(orderRequest);

                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                        Toast.makeText(CartActivity.this, "Cảm ơn , Đơn hàng đang được xử lý !!!", Toast.LENGTH_LONG).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment canceled.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid payment.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadFoodList() {
        orders = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(orders, this);
        //adapter.notifyDataSetChanged(); //no needed because user not add items while he see his orders.
        recyclerView.setAdapter(adapter);

        float total = 0;
        for (Order order : orders) {
            total += (Float.parseFloat(order.getPrice())) * (Float.parseFloat(order.getQuantity()));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalPriceTextView.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        orders.remove(position);
        new Database(this).cleanCart(Common.currentUser.getPhone());
        for (Order item : orders) {
            new Database(this).addToCart(item);
            loadFoodList();
        }
        Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {

            String name = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            float total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item : orders) {
                total += (Float.parseFloat(item.getPrice())) * (Float.parseFloat(item.getQuantity()));
            }
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            totalPriceTextView.setText(fmt.format(total));

            Snackbar snackbar = Snackbar.make(rootLayout, name + " xóa khỏi giỏ hàng!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hủy", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    float total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item : orders) {
                        total += (Float.parseFloat(item.getPrice())) * (Float.parseFloat(item.getQuantity()));
                    }
                    Locale locale = new Locale("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    totalPriceTextView.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

