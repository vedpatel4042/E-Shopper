package com.example.e_shopper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<ProductModel> cartProducts;
    private CartFragment cartFragment;
    private Map<String, Integer> productQuantities;
    private DatabaseReference cartRef;
    private String userId;

    public CartAdapter(List<ProductModel> cartProducts, CartFragment cartFragment, Map<String, Integer> productQuantities) {
        this.cartProducts = cartProducts;
        this.cartFragment = cartFragment;
        this.productQuantities = productQuantities;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("users").child("cart").child(userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = cartProducts.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("₹%.2f", product.getPrice()));
        Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.productImage);

        int quantity = productQuantities.getOrDefault(product.getId(), 1);
        holder.quantityTextView.setText(String.valueOf(quantity));

        holder.incrementButton.setOnClickListener(v -> {
            int currentQuantity = productQuantities.get(product.getId());
            int newQuantity = currentQuantity + 1;
            productQuantities.put(product.getId(), newQuantity);
            cartRef.child(product.getId()).setValue(newQuantity);
            holder.quantityTextView.setText(String.valueOf(newQuantity));
            cartFragment.updateTotalPrice();
        });

        holder.decrementButton.setOnClickListener(v -> {
            int currentQuantity = productQuantities.get(product.getId());
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                productQuantities.put(product.getId(), newQuantity);
                cartRef.child(product.getId()).setValue(newQuantity);
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                cartFragment.updateTotalPrice();
            } else {
                cartRef.child(product.getId()).removeValue();
                cartProducts.remove(position);
                notifyItemRemoved(position);
                cartFragment.updateTotalPrice();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantityTextView;
        Button incrementButton, decrementButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}

//package com.example.e_shopper;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
//
//    private List<ProductModel> cartProducts;
//    private CartFragment cartFragment;
//    private Map<String, Integer> productQuantities;
//    private DatabaseReference cartRef;
//
//    public CartAdapter(List<ProductModel> cartProducts, CartFragment cartFragment, Map<String, Integer> productQuantities) {
//        this.cartProducts = cartProducts;
//        this.cartFragment = cartFragment;
//        this.productQuantities = productQuantities;
//        this.cartRef = FirebaseDatabase.getInstance().getReference("carts").child("user_id");
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ProductModel product = cartProducts.get(position);
//
//        holder.productName.setText(product.getName());
//        holder.productPrice.setText(String.format("₹%.2f", product.getPrice()));
//        Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.productImage);
//
//        int quantity = productQuantities.getOrDefault(product.getId(), 1);
//        holder.quantityTextView.setText(String.valueOf(quantity));
//
//        holder.incrementButton.setOnClickListener(v -> {
//            int currentQuantity = productQuantities.get(product.getId());
//            productQuantities.put(product.getId(), currentQuantity + 1);
//            holder.quantityTextView.setText(String.valueOf(currentQuantity + 1));
//            cartFragment.updateTotalPrice();
//        });
//
//        holder.decrementButton.setOnClickListener(v -> {
//            int currentQuantity = productQuantities.get(product.getId());
//            if (currentQuantity > 1) {
//                productQuantities.put(product.getId(), currentQuantity - 1);
//                holder.quantityTextView.setText(String.valueOf(currentQuantity - 1));
//                cartFragment.updateTotalPrice();
//            } else {
//                cartRef.child(product.getId()).removeValue();
//                cartProducts.remove(position);
//                notifyItemRemoved(position);
//                cartFragment.updateTotalPrice();
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return cartProducts.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView productImage;
//        TextView productName, productPrice, quantityTextView;
//        Button incrementButton, decrementButton;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productImage = itemView.findViewById(R.id.productImage);
//            productName = itemView.findViewById(R.id.productName);
//            productPrice = itemView.findViewById(R.id.productPrice);
//            quantityTextView = itemView.findViewById(R.id.quantityTextView);
//            incrementButton = itemView.findViewById(R.id.incrementButton);
//            decrementButton = itemView.findViewById(R.id.decrementButton);
//        }
//    }
//}