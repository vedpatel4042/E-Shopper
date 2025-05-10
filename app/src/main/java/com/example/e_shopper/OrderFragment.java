package com.example.e_shopper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private RecyclerView recentOrdersRecyclerView;
    private OrderAdapter orderAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference orderRef;
    private List<OrderModel> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        recentOrdersRecyclerView = view.findViewById(R.id.recent_orders_recycler_view);
        orderList = new ArrayList<>();

        orderAdapter = new OrderAdapter(requireContext(), orderList, this); // Pass listener
        recentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentOrdersRecyclerView.setAdapter(orderAdapter);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("orders");

        loadOrders();
        return view;
    }

    private void loadOrders() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderModel order = orderSnapshot.getValue(OrderModel.class);
                    if (order != null) {
                        order.setOrderId(orderSnapshot.getKey());
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(String orderId) {
        // Open OrderConfirmationFragment and pass orderId
        OrderConfirmationFragment orderConfirmationFragment = new OrderConfirmationFragment();
        Bundle args = new Bundle();
        args.putString("orderId", orderId);
        orderConfirmationFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, orderConfirmationFragment)
                .addToBackStack(null)
                .commit();
    }
}
