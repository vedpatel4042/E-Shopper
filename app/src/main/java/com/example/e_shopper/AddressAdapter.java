package com.example.e_shopper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<AddressManagementFragment.Address> addressList;
    private int defaultAddressPosition = -1;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onDeleteAddress(AddressManagementFragment.Address address);
        void onSetDefaultAddress(AddressManagementFragment.Address address);
    }

    public AddressAdapter(List<AddressManagementFragment.Address> addressList, OnAddressActionListener listener) {
        this.addressList = addressList;
        this.listener = listener;
        Log.d("AddressAdapter", "Initialized with " + this.addressList.size() + " addresses");
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressManagementFragment.Address address = addressList.get(position);
        holder.nameTextView.setText(address.getName());

        String fullAddress = String.format("%s, %s, %s, %s, %s",
                address.getAddressLine1(), address.getCity(), address.getState(), address.getCountry(), address.getPostcode());
        holder.addressTextView.setText(fullAddress);

        holder.defaultCheckBox.setChecked(holder.getAdapterPosition() == defaultAddressPosition);
        holder.defaultCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int previousDefault = defaultAddressPosition;
                defaultAddressPosition = holder.getAdapterPosition();
                listener.onSetDefaultAddress(address);
                notifyItemChanged(previousDefault);
                notifyItemChanged(defaultAddressPosition);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            listener.onDeleteAddress(address);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        MaterialTextView nameTextView, addressTextView, deleteButton;
        MaterialCheckBox defaultCheckBox;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            defaultCheckBox = itemView.findViewById(R.id.defaultCheckBox);
        }
    }
}
