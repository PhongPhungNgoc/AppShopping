package com.example.phongphung.appshopping.viewHolder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phongphung.appshopping.R;
import com.example.phongphung.appshopping.common.Common;
import com.example.phongphung.appshopping.model.OrderRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListOrderActivity extends AppCompatActivity {


    private static final String TAG = "order";

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<OrderRequest, OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference request;
    @BindView(R.id.list_orders_recycler)
    RecyclerView listOrdersRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);
        ButterKnife.bind(this);
        //firebase
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Order");

        listOrdersRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOrdersRecycler.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {

        FirebaseRecyclerOptions<OrderRequest> options = new FirebaseRecyclerOptions.Builder<OrderRequest>()
                .setQuery(request, OrderRequest.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<OrderRequest, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final OrderRequest model) {
                if (model.getPhone() == Common.currentUser.getPhone()) {
                    viewHolder.tvOrderID.setText(adapter.getRef(position).getKey());
                    viewHolder.tvOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                    viewHolder.tvOrderPhone.setText(model.getPhone());
                    viewHolder.tvOrderAdress.setText(model.getAdress());
                }

            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        listOrdersRecycler.setAdapter(adapter);

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
