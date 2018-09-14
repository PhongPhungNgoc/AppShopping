package com.example.phongphung.appshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.phongphung.appshopping.adapter.OrderDetailAdapter;
import com.example.phongphung.appshopping.model.Order;
import com.example.phongphung.appshopping.model.OrderRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActitity extends AppCompatActivity {

    @BindView(R.id.recycle_foodOrder)
    RecyclerView recycleProductOrder;
    @BindView(R.id.tv_PhoneDetail)
    TextView tvPhoneDetail;
    @BindView(R.id.tv_AmountDetail)
    TextView tvAmountDetail;
    @BindView(R.id.tv_StatusDetail)
    TextView tvStatusDetail;
    @BindView(R.id.tv_StateDetail)
    TextView tvStateDetail;
    @BindView(R.id.tv_CommentDetail)
    TextView tvCommentDetail;
    @BindView(R.id.tv_AddressDetail)
    TextView tvAddressDetail;

    private ArrayList<Order> productList = new ArrayList<>();
    private OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_actitity);
        ButterKnife.bind(this);

        Intent intentDetail = getIntent();
        OrderRequest orderRequest = (OrderRequest) intentDetail.getSerializableExtra(ListOrderActivity.DETAIL_PRODUCT);

        tvPhoneDetail.setText(orderRequest.getPhone());
        tvAmountDetail.setText(String.valueOf(orderRequest.getTotal()));
        tvStateDetail.setText(orderRequest.getPaymentState());
        tvStatusDetail.setText(orderRequest.getStatus());
        tvCommentDetail.setText(orderRequest.getComment());
        productList = (ArrayList<Order>) orderRequest.getFoods();

        recycleProductOrder.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleProductOrder.setLayoutManager(manager);

        adapter = new OrderDetailAdapter(productList, getApplicationContext());
        recycleProductOrder.setAdapter(adapter);
    }
}
