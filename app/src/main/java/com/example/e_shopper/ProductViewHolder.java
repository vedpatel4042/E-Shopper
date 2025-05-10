package com.example.e_shopper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private ImageButton productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productRating;
    private ProductAdapter adapter;  // Store the adapter reference

    public ProductViewHolder(@NonNull View itemView, ProductAdapter adapter) {
        super(itemView);
        this.adapter = adapter;  // Initialize adapter reference

        productImage = itemView.findViewById(R.id.productImage);
        productName = itemView.findViewById(R.id.productName);
        productPrice = itemView.findViewById(R.id.productPrice);
        productRating = itemView.findViewById(R.id.productRating);

        productImage.setOnClickListener(v -> navigateToDetails(getAdapterPosition()));
    }

    public void bind(ProductModel product) {
        productName.setText(product.getName());
        productPrice.setText(String.format("₹%.2f", product.getPrice()));
        productRating.setText(String.format("%.1f ★", product.getRating()));

        // Set the product image
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            String imageUrl = product.getImageUrls().get(0); // Get the first image URL
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(productImage);} else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.placeholder) // Use a placeholder
                    .into(productImage);
        }
    }

    // Navigate to product details fragment
    private void navigateToDetails(int position) {
        ProductModel product = adapter.getProducts().get(position);  // Use adapter to get the product
        Bundle args = new Bundle();
        args.putString("product_id", product.getId());

        NavController navController = Navigation.findNavController(itemView);
        navController.navigate(R.id.action_homeFragment_to_productDetailsFragment, args);  // Correctly resolve the action
    }
}