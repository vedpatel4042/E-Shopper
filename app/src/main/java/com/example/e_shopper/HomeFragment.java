package com.example.e_shopper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView productsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private ProductAdapter productAdapter;
    private HomeCategoryAdapter categoryAdapter; // Updated adapter
    private DatabaseReference databaseRef;
    private List<ProductModel> originalProductList = new ArrayList<>();
    private List<CategoryModel> categoryList = new ArrayList<>();
    private ProgressBar progressBar;
    private BottomNavigationView bottomNav;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(rootView);
        setupRecyclerViews();
        setupScrollBehavior();
        initializeFirebase();
        setupSwipeToRefresh();

        return rootView;
    }

    private void initializeViews(View rootView) {
        productsRecyclerView = rootView.findViewById(R.id.productsRecyclerView);
        categoryRecyclerView = rootView.findViewById(R.id.categoryRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);
        bottomNav = requireActivity().findViewById(R.id.bottomNavigation);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCategories();
            loadProducts();
        });
    }

    private void setupRecyclerViews() {
        // Setup Products RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        productsRecyclerView.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        productsRecyclerView.setAdapter(productAdapter);

        // Setup Categories RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter = new HomeCategoryAdapter(categoryList); // Use HomeCategoryAdapter
        categoryAdapter.setOnCategoryClickListener(category -> {
            // Filter products when category is clicked
            if (category != null && category.getId() != null) {
                filterProductsByCategory(category.getId());
            } else {
                // If no category is selected or category is null, show all products
                productAdapter.updateData(originalProductList);
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupScrollBehavior() {
        productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && bottomNav.isShown()) {
                    bottomNav.animate().translationY(bottomNav.getHeight()).setDuration(300);
                } else if (dy < 0) {
                    bottomNav.animate().translationY(0).setDuration(300);
                }
            }
        });
    }

    private void initializeFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        databaseRef.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                // Add an "All" category at the beginning
                CategoryModel allCategory = new CategoryModel();
                allCategory.setId("all");
                allCategory.setName("All");
                allCategory.setIconUrl("your_default_icon_url"); // Set a default icon URL for "All" category
                categoryList.add(allCategory);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel category = dataSnapshot.getValue(CategoryModel.class);
                    if (category != null) {
                        category.setId(dataSnapshot.getKey());
                        categoryList.add(category);
                    }
                }
                categoryAdapter.updateData(categoryList); // Update the adapter with the new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadCategories: onCancelled: " + error.getMessage());
                showError("Failed to load categories");
            }
        });
    }

    private void loadProducts() {
        showLoading();
        databaseRef.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(false);
                List<ProductModel> productList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProductModel product = dataSnapshot.getValue(ProductModel.class);
                    if (product != null) {
                        product.setId(dataSnapshot.getKey());
                        productList.add(product);
                    }
                }
                originalProductList = new ArrayList<>(productList);
                productAdapter.updateData(productList);
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "loadProducts: onCancelled: " + error.getMessage());
                hideLoading();
            }
        });
    }

    private void filterProductsByCategory(String categoryId) {
        if (categoryId.equals("all")) {
            productAdapter.updateData(originalProductList);
            return;
        }

        List<ProductModel> filteredList = originalProductList.stream()
                .filter(product -> product.getCategory() != null &&
                        product.getCategory().equals(categoryId))
                .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            showError("No products found in this category");
        }

        productAdapter.updateData(filteredList);
    }

    @Override
    public void onProductClick(ProductModel product) {
        Bundle args = new Bundle();
        args.putString("product_id", product.getId());
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(args);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out,
                R.anim.slide_in,
                R.anim.slide_out
        );

        transaction.replace(R.id.fragment_container, productDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setOriginalProductList(List<ProductModel> products) {
        this.originalProductList = products;
        if (productAdapter != null) {
            productAdapter.updateData(products);
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void setFilteredProductList(List<ProductModel> filteredList) {
        if (productAdapter != null) {
            productAdapter.updateData(filteredList);
        }
    }
}
