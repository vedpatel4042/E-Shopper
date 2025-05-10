package com.example.e_shopper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView, checkoutButton;
    private DatabaseReference cartRef, productRef;
    private List<ProductModel> cartProducts;
    private Map<String, Integer> productQuantities = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = rootView.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = rootView.findViewById(R.id.totalPriceTextView);
        checkoutButton = rootView.findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        cartProducts = new ArrayList<>();
        productQuantities = new HashMap<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("users").child("cart").child(userId);
        productRef = FirebaseDatabase.getInstance().getReference("products");

        cartAdapter = new CartAdapter(cartProducts, this, productQuantities);
        cartRecyclerView.setAdapter(cartAdapter);

        loadCartItems();

        checkoutButton.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Proceeding to Checkout", Toast.LENGTH_SHORT).show());

        return rootView;
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartProducts.clear();
                productQuantities.clear();

                for (DataSnapshot cartItem : snapshot.getChildren()) {
                    String productId = cartItem.getKey();
                    Integer quantity = cartItem.getValue(Integer.class);

                    if (quantity != null) {
                        productQuantities.put(productId, quantity);

                        productRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                ProductModel product = productSnapshot.getValue(ProductModel.class);
                                if (product != null) {
                                    cartProducts.add(product);
                                    cartAdapter.notifyDataSetChanged();
                                    calculateTotalPrice();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(requireContext(),
                                        "Failed to load product details",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                if (!snapshot.exists()) {
                    calculateTotalPrice();
                    cartAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Failed to load cart items",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalPrice() {
        double totalPrice = 0.0;
        int totalItems = 0;
        for (ProductModel product : cartProducts) {
            int quantity = productQuantities.getOrDefault(product.getId(), 1);
            totalPrice += product.getPrice() * quantity;
            totalItems += quantity;
        }
        totalPriceTextView.setText(String.format("Total: ₹%.2f (%d items)", totalPrice, totalItems));
    }

    public void updateTotalPrice() {
        calculateTotalPrice();
    }
}
//package com.example.e_shopper;

//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

//public class CartFragment extends Fragment {
//
//    private RecyclerView cartRecyclerView;
//    private CartAdapter cartAdapter;
//    private TextView totalPriceTextView, checkoutButton;
//    private DatabaseReference cartRef, productRef;
//    private List<ProductModel> cartProducts;
//    private Map<String, Integer> productQuantities = new HashMap<>();
//    private String userId = "user_id"; // Replace with actual user ID logic
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
//
//        cartRecyclerView = rootView.findViewById(R.id.cartRecyclerView);
//        totalPriceTextView = rootView.findViewById(R.id.totalPriceTextView);
//        checkoutButton = rootView.findViewById(R.id.checkoutButton);
//
//        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        cartProducts = new ArrayList<>();
//        cartAdapter = new CartAdapter(cartProducts, this, productQuantities);
//        cartRecyclerView.setAdapter(cartAdapter);
//
//        cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
//        productRef = FirebaseDatabase.getInstance().getReference("products");
//
//        loadCartItems();
//
//        checkoutButton.setOnClickListener(v -> Toast.makeText(requireContext(), "Proceeding to Checkout", Toast.LENGTH_SHORT).show());
//
//        return rootView;
//    }
//
//    private void loadCartItems() {
//        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                cartProducts.clear();
//                productQuantities.clear();
//
//                for (DataSnapshot cartItem : snapshot.getChildren()) {
//                    String productId = cartItem.getKey();
//                    int quantity = cartItem.child("quantity").getValue(Integer.class) != null ? cartItem.child("quantity").getValue(Integer.class) : 1;
//                    productQuantities.put(productId, quantity);
//
//                    productRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot productSnapshot) {
//                            ProductModel product = productSnapshot.getValue(ProductModel.class);
//                            if (product != null) {
//                                cartProducts.add(product);
//                                cartAdapter.notifyDataSetChanged();
//                                calculateTotalPrice();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(requireContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                if (!snapshot.exists()) {
//                    calculateTotalPrice();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void calculateTotalPrice() {
//        double totalPrice = 0.0;
//        int totalItems = 0;
//        for (ProductModel product : cartProducts) {
//            int quantity = productQuantities.getOrDefault(product.getId(), 1);
//            totalPrice += product.getPrice() * quantity;
//            totalItems += quantity;
//        }
//        totalPriceTextView.setText(String.format("Total: ₹%.2f (%d items)", totalPrice, totalItems));
//    }
//
//    public void updateTotalPrice() {
//        calculateTotalPrice();
//    }
//}