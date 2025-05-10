package com.example.e_shopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    private CardView profileCard, addressCard, paymentCard, logoutCard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);
        setupClickListeners();
        view.findViewById(R.id.logoutCard).setOnClickListener(v -> logout());
        return view;
    }

    private void initializeViews(View view) {
        profileCard = view.findViewById(R.id.profileCard);
        addressCard = view.findViewById(R.id.addressCard);
        paymentCard = view.findViewById(R.id.paymentCard);
        logoutCard = view.findViewById(R.id.logoutCard);
    }

    private void setupClickListeners() {
        if (profileCard != null) {
            profileCard.setOnClickListener(v -> navigateToFragment(new ProfileFragment()));
        }

        if (addressCard != null) {
            addressCard.setOnClickListener(v -> navigateToFragment(new AddressManagementFragment()));
        }

        if (paymentCard != null) {
            paymentCard.setOnClickListener(v -> navigateToFragment(new PaymentMethodsFragment()));
        }

        if (logoutCard != null) {
            logoutCard.setOnClickListener(v -> logout());
        }
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
