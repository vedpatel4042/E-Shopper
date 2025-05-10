package com.example.e_shopper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private ViewPager2 imageCarousel;
    private TextView productName, productPrice, productRating, productDescription;
    private MaterialCardView expandableContainer;
    private LinearLayout detailedContent;
    private Button addToCartButton, buyNowButton;
    private RecyclerView relatedProductsRecyclerView;

    private DatabaseReference databaseRef;
    private String productId;
    private ProductModel currentProduct;
    private ProductImageAdapter imageAdapter;
    private ProductAdapter relatedProductsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);

        if (getArguments() != null) {
            productId = getArguments().getString("product_id");
        }

        initializeViews(rootView);
        setupFirebase();
        setupClickListeners();
        if (productId != null) {
            loadProductDetails();
        }

        return rootView;
    }

    private void initializeViews(View view) {
        imageCarousel = view.findViewById(R.id.imageCarousel);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productRating = view.findViewById(R.id.productRating);
        productDescription = view.findViewById(R.id.productDescription);
        expandableContainer = view.findViewById(R.id.expandableContainer);
        detailedContent = view.findViewById(R.id.detailedContent);
        addToCartButton = view.findViewById(R.id.addToCartButton);
        buyNowButton = view.findViewById(R.id.buyNowButton);
        relatedProductsRecyclerView = view.findViewById(R.id.relatedProductsRecyclerView);

        imageAdapter = new ProductImageAdapter(new ArrayList<>());
        imageCarousel.setAdapter(imageAdapter);

        relatedProductsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        relatedProductsAdapter = new ProductAdapter(new ArrayList<>(), this);
        relatedProductsRecyclerView.setAdapter(relatedProductsAdapter);
    }

    private void setupFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setupClickListeners() {
        addToCartButton.setOnClickListener(v -> addToCart());
        buyNowButton.setOnClickListener(v -> proceedToBuy());
    }

    private void loadProductDetails() {
        databaseRef.child("products").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProduct = snapshot.getValue(ProductModel.class);
                if (currentProduct != null) {
                    updateUI();
                    loadRelatedProducts(currentProduct.getCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentProduct == null || !isAdded()) return;

        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("₹%.2f", currentProduct.getPrice()));
        productRating.setText(String.format("%.1f ★", currentProduct.getRating()));
        productDescription.setText(currentProduct.getDescription());

        List<String> imageUrls = currentProduct.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageAdapter.updateImages(imageUrls);
        }
    }

    private void loadRelatedProducts(String category) {
        databaseRef.child("products").orderByChild("category").equalTo(category).limitToFirst(5)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ProductModel> relatedProducts = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ProductModel product = dataSnapshot.getValue(ProductModel.class);
                            if (product != null && !product.getId().equals(productId)) {
                                relatedProducts.add(product);
                            }
                        }
                        relatedProductsAdapter.updateData(relatedProducts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error loading related products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToCart() {
        if (currentProduct == null || !isAdded()) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child("cart").child(userId);

        cartRef.child(productId).setValue(1)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show());
    }

    private void proceedToBuy() {
        if (currentProduct == null || !isAdded()) return;

        OrderConfirmationFragment orderConfirmationFragment = new OrderConfirmationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("product_id", currentProduct.getId());
        bundle.putString("product_name", currentProduct.getName());
        bundle.putDouble("product_price", currentProduct.getPrice());
        bundle.putString("product_image", currentProduct.getImageUrls().get(0)); // First image
        orderConfirmationFragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, orderConfirmationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onProductClick(ProductModel product) {
        Bundle args = new Bundle();
        args.putString("product_id", product.getId());
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(args);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, productDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}



//package com.example.e_shopper;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//import com.google.android.material.card.MaterialCardView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ProductDetailsFragment extends Fragment implements ProductAdapter.OnProductClickListener {
//
//    private ViewPager2 imageCarousel;
//    private TextView productName, productPrice, productRating, productDescription;
//    private MaterialCardView expandableContainer;
//    private LinearLayout detailedContent;
//    private Button addToCartButton, buyNowButton;
//    private RecyclerView relatedProductsRecyclerView;
//
//    private DatabaseReference databaseRef;
//    private String productId;
//    private ProductModel currentProduct;
//    private ProductImageAdapter imageAdapter;
//    private ProductAdapter relatedProductsAdapter;
//
//
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
////
////        productId = getArguments() != null ? getArguments().getString("productId") : null;
////
////        productName = view.findViewById(R.id.productName);
////        productPrice = view.findViewById(R.id.productPrice);
////        productRating = view.findViewById(R.id.productRating);
////        productDescription = view.findViewById(R.id.productDescription);
////        imageCarousel = view.findViewById(R.id.imageCarousel);
////
////        databaseRef = FirebaseDatabase.getInstance().getReference();
////
////        if (productId != null) {
////            loadProductDetails();
////        }
////
////        return view;
////    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);
//
//        if (getArguments() != null) {
//            productId = getArguments().getString("product_id");
//        }
//
//        initializeViews(rootView);
//        setupFirebase();
//        setupClickListeners();
//        if (productId != null) {
//            loadProductDetails();
//        }
//
//        return rootView;
//    }
//
//    private void initializeViews(View view) {
//        imageCarousel = view.findViewById(R.id.imageCarousel);
//        productName = view.findViewById(R.id.productName);
//        productPrice = view.findViewById(R.id.productPrice);
//        productRating = view.findViewById(R.id.productRating);
//        productDescription = view.findViewById(R.id.productDescription);
//        expandableContainer = view.findViewById(R.id.expandableContainer);
//        detailedContent = view.findViewById(R.id.detailedContent);
//        addToCartButton = view.findViewById(R.id.addToCartButton);
//        buyNowButton = view.findViewById(R.id.buyNowButton);
//        relatedProductsRecyclerView = view.findViewById(R.id.relatedProductsRecyclerView);
//
//        imageAdapter = new ProductImageAdapter(new ArrayList<>());
//        imageCarousel.setAdapter(imageAdapter);
//
//        // Use GridLayoutManager for the related products
//        int numberOfColumns = 2; // You can change this number based on how many columns you want
//        relatedProductsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumns));
//
//        relatedProductsAdapter = new ProductAdapter(new ArrayList<>(), this);
//        relatedProductsRecyclerView.setAdapter(relatedProductsAdapter);
//    }
//
//    private void setupFirebase() {
//        databaseRef = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void setupClickListeners() {
//        addToCartButton.setOnClickListener(v -> addToCart());
//        buyNowButton.setOnClickListener(v -> proceedToBuy());
//
//        expandableContainer.setOnClickListener(v -> {
//            if (detailedContent != null) {
//                detailedContent.setVisibility(detailedContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//            }
//        });
//    }
////    private void loadProductDetails() {
////        databaseRef.child("products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                ProductModel product = snapshot.getValue(ProductModel.class);
////                if (product != null) {
////                    productName.setText(product.getName());
////                    productPrice.setText("₹" + product.getPrice());
////                    productDescription.setText(product.getDescription());
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Toast.makeText(requireContext(), "Error loading product", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
//    private void loadProductDetails() {
//        databaseRef.child("products").child(productId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                currentProduct = snapshot.getValue(ProductModel.class);
//                if (currentProduct != null) {
//                    updateUI();
//                    loadRelatedProducts(currentProduct.getCategory());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(requireContext(), "Error loading product details", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateUI() {
//        if (currentProduct == null || !isAdded()) return;
//
//        productName.setText(currentProduct.getName());
//        productPrice.setText(String.format("₹%.2f", currentProduct.getPrice()));
//        productRating.setText(String.format("%.1f ★", currentProduct.getRating()));
//        productDescription.setText(currentProduct.getDescription());
//
//        List<String> imageUrls = currentProduct.getImageUrls();
//        if (imageUrls != null && !imageUrls.isEmpty()) {
//            imageAdapter.updateImages(imageUrls);
//        } else {
//            imageAdapter.updateImages(List.of("android.resource://" + requireContext().getPackageName() + "/" + R.drawable.placeholder));
//        }
//
//        updateSpecifications();
//    }
//
//    private void updateSpecifications() {
//        if (detailedContent == null || currentProduct == null || !isAdded()) return;
//
//        detailedContent.removeAllViews();
//        for (String spec : currentProduct.getSpecifications()) {
//            TextView specView = new TextView(requireContext());
//            specView.setText(spec);
//            specView.setTextColor(requireContext().getColor(R.color.text_secondary));
//            specView.setTextSize(14);
//            specView.setPadding(0, 8, 0, 8);
//            detailedContent.addView(specView);
//        }
//    }
//
//    private void loadRelatedProducts(String category) {
//        databaseRef.child("products").orderByChild("category").equalTo(category).limitToFirst(5)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<ProductModel> relatedProducts = new ArrayList<>();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            ProductModel product = dataSnapshot.getValue(ProductModel.class);
//                            if (product != null && !product.getId().equals(productId)) {
//                                relatedProducts.add(product);
//                            }
//                        }
//                        relatedProductsAdapter.updateData(relatedProducts);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(requireContext(), "Error loading related products", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void addToCart() {
//        if (currentProduct == null || !isAdded()) return;
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child("cart").child(userId);
//
//        cartRef.child(productId).setValue(1)
//                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show());
//    }
//
//
////    private void addToCart() {
////        if (currentProduct == null || !isAdded()) return;
////
////        String userId = getCurrentUserId();
////        DatabaseReference cartRef = databaseRef.child("carts").child(userId);
////
////        cartRef.child(productId).setValue(1).addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show())
////                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show());
////    }
//
//    private void proceedToBuy() {
//        if (currentProduct == null || !isAdded()) return;
//
//        Toast.makeText(requireContext(), "Buy Now Clicked", Toast.LENGTH_SHORT).show();
//    }
//
//    private String getCurrentUserId() {
//        // Replace with actual user authentication logic
//        return "user_id";
//    }
//
//    @Override
//    public void onProductClick(ProductModel product) {
//        Bundle args = new Bundle();
//        args.putString("product_id", product.getId());
//
//        // Create a new instance of ProductDetailsFragment
//        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
//        productDetailsFragment.setArguments(args);
//
//        // Get the FragmentManager from the activity
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//
//        // Begin the fragment transaction
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        // Replace the current fragment with ProductDetailsFragment
//        transaction.replace(R.id.fragment_container, productDetailsFragment); // Replace with your container ID
//
//        // Optionally, add the transaction to the back stack so the user can navigate back
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();
//    }
//}
