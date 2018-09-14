package com.example.phongphung.appshopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.interFace.PositionClickListener;
import com.example.phongphung.appshopping.model.OrderRequest;
import com.example.phongphung.appshopping.adapter.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListOrderActivity extends AppCompatActivity implements PositionClickListener{


    private static final String TAG = "order";
    public static final String DETAIL_PRODUCT = "Detail Product";


    FirebaseDatabase database;
    DatabaseReference request;

    @BindView(R.id.list_orders_recycler)
    RecyclerView listOrdersRecycler;

    ArrayList<OrderRequest> listAllOrder = new ArrayList<>();
    ArrayList<OrderRequest> listOrder = new ArrayList<>();

    OrderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Order");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listAllOrder.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listOrder.clear();
                    listAllOrder.add(ds.getValue(OrderRequest.class));
                    for (int i = 0; i < listAllOrder.size(); i++) {
                        if (listAllOrder.get(i).getPhone().equals(Common.currentUser.getPhone())) {
                            listOrder.add(listAllOrder.get(i));
                        }
                    }
                }
                Collections.reverse(listOrder);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        request.addValueEventListener(eventListener);

        listOrdersRecycler.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listOrdersRecycler.setLayoutManager(manager);

        adapter = new OrderAdapter(listOrder, getApplicationContext());
        listOrdersRecycler.setAdapter(adapter);
     
        adapter.notifyDataSetChanged();

        adapter.setPositionClickListener(this);
    }

    @Override
    public void onClick(int position) {
        Intent intentDetail = new Intent(getApplicationContext(), OrderDetailActitity.class);
        intentDetail.putExtra(DETAIL_PRODUCT, listOrder.get(position));
        startActivity(intentDetail);
    }
}
