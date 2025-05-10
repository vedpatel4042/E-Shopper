package com.example.e_shopper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DatabaseReference databaseRef;
    private List<ProductModel> productList = new ArrayList<>();
    private String categoryId;

    public ProductsFragment() {
        // Default constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = view.findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // Grid layout with 2 columns
        productAdapter = new ProductAdapter(productList, product -> {
            // Handle product click (e.g., navigate to product details)
        });
        recyclerView.setAdapter(productAdapter);

        // Retrieve categoryId from arguments
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            Log.d("ProductsFragment", "Received categoryId: " + categoryId);
        }

        if (categoryId == null) {
            Log.e("ProductsFragment", "Category ID is null");
            return view;
        }

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        // Load products based on the categoryId
        loadProducts();

        return view;
    }

    private void loadProducts() {
        if (categoryId == null) {
            Log.e("ProductsFragment", "Category ID is null");
            return;
        }

        databaseRef.orderByChild("category").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProductModel product = dataSnapshot.getValue(ProductModel.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.updateData(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductsFragment", "Database error: " + error.getMessage());
            }
        });
    }
}
