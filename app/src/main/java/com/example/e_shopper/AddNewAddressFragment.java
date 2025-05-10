package com.example.e_shopper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewAddressFragment extends Fragment {
    private static final String TAG = "AddNewAddress";
    private TextInputEditText addressLine1Input, addressLine2Input, cityInput, stateInput, countryInput, postcodeInput;
    private MaterialButton submitButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_address, container, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        initializeInputFields(rootView);
        setupSubmitButton();
        return rootView;
    }

    private void initializeInputFields(View rootView) {
        addressLine1Input = rootView.findViewById(R.id.addressLine1Input);
        addressLine2Input = rootView.findViewById(R.id.addressLine2Input);
        cityInput = rootView.findViewById(R.id.cityInput);
        stateInput = rootView.findViewById(R.id.stateInput);
        countryInput = rootView.findViewById(R.id.countryInput);
        postcodeInput = rootView.findViewById(R.id.postcodeInput);
        submitButton = rootView.findViewById(R.id.submitButton);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveAddressToFirestore();
            }
        });
    }

    private boolean validateInputs() {
        if (addressLine1Input.getText() == null || addressLine1Input.getText().toString().trim().isEmpty()) {
            addressLine1Input.setError("Address Line 1 is required");
            return false;
        }
        if (cityInput.getText() == null || cityInput.getText().toString().trim().isEmpty()) {
            cityInput.setError("City is required");
            return false;
        }
        return true;
    }

    private void saveAddressToFirestore() {
        String userId = getCurrentUserId();
        if (userId.equals("unknown_user")) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressId = userId + "_" + System.currentTimeMillis();

        // Creating a map for Firestore
        Map<String, Object> userAddress = new HashMap<>();
        userAddress.put("userId", userId);
        userAddress.put("name", getCurrentUserName());
        userAddress.put("addressLine1", addressLine1Input.getText().toString().trim());
        userAddress.put("addressLine2", addressLine2Input.getText().toString().trim());
        userAddress.put("city", cityInput.getText().toString().trim());
        userAddress.put("state", stateInput.getText().toString().trim());
        userAddress.put("country", countryInput.getText().toString().trim());
        userAddress.put("postcode", postcodeInput.getText().toString().trim());

        firestore.collection("users").document(userId)
                .collection("addresses").document(addressId)
                .set(userAddress)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Address added successfully");
                    Toast.makeText(getContext(), "Address Added Successfully", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding address", e);
                    Toast.makeText(getContext(), "Failed to Add Address: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown_user";
    }

    private String getCurrentUserName() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getDisplayName() : "Unknown User";
    }

    private void clearInputFields() {
        addressLine1Input.setText("");
        addressLine2Input.setText("");
        cityInput.setText("");
        stateInput.setText("");
        countryInput.setText("");
        postcodeInput.setText("");
    }
}
