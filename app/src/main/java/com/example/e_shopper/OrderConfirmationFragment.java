package com.example.e_shopper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderConfirmationFragment extends Fragment {
    private TextView orderNumber, orderDate, paymentMethod, orderTotal, shippingAddress;
    private RecyclerView itemsRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference orderRef;
    private String orderId;
    private List<OrderItemModel> orderItemList;
    private OrderItemAdapter orderItemAdapter;

    public OrderConfirmationFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        // Initialize UI components
        orderNumber = view.findViewById(R.id.order_number);
        orderDate = view.findViewById(R.id.order_date);
        paymentMethod = view.findViewById(R.id.payment_method);
        orderTotal = view.findViewById(R.id.order_total);
        shippingAddress = view.findViewById(R.id.shipping_address);
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view);

        // Setup RecyclerView
        orderItemList = new ArrayList<>();
        orderItemAdapter = new OrderItemAdapter(getContext(), orderItemList);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemsRecyclerView.setAdapter(orderItemAdapter);

        // Get Order ID from Bundle
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Order ID is missing", Toast.LENGTH_SHORT).show();
            return view;
        }

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("orders").child(orderId);

        loadOrderDetails();
        return view;
    }

    private void loadOrderDetails() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderNumber.setText("Order #" + orderId);
                    orderDate.setText("Order Date: " + snapshot.child("date").getValue(String.class));
                    paymentMethod.setText("Payment: " + snapshot.child("paymentMethod").getValue(String.class));
                    orderTotal.setText("Total: $" + snapshot.child("totalAmount").getValue(String.class));
                    shippingAddress.setText("Shipping to: " + snapshot.child("shippingAddress").getValue(String.class));

                    orderItemList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.child("items").getChildren()) {
                        OrderItemModel item = itemSnapshot.getValue(OrderItemModel.class);
                        if (item != null) {
                            orderItemList.add(item);
                        }
                    }
                    orderItemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve order details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ProductDetailsFragment extends Fragment {
        private ProductModel currentProduct;

        private void proceedToBuy() {
            if (currentProduct == null || !isAdded()) return;

            String orderId = UUID.randomUUID().toString(); // Generate unique order ID

            Bundle args = new Bundle();
            args.putString("orderId", orderId);
            args.putString("product_id", currentProduct.getId());

            OrderConfirmationFragment orderFragment = new OrderConfirmationFragment();
            orderFragment.setArguments(args);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, orderFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
