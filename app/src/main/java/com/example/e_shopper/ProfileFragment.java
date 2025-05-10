package com.example.e_shopper;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private TextView nameText, emailText, phoneText;
    private ImageView editNameIcon, editPhoneIcon;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        loadUserData();

        editNameIcon.setOnClickListener(v -> showEditDialog("name"));
        editPhoneIcon.setOnClickListener(v -> showEditDialog("phone"));

        return view;
    }

    private void initializeViews(View view) {
        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        phoneText = view.findViewById(R.id.phoneText);
        editNameIcon = view.findViewById(R.id.editNameIcon);
        editPhoneIcon = view.findViewById(R.id.editPhoneIcon);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        emailText.setText(currentUser.getEmail());
    }

    private void loadUserData() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            nameText.setText(documentSnapshot.getString("name"));
                            phoneText.setText(documentSnapshot.getString("phone"));
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void showEditDialog(String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        EditText editField = dialogView.findViewById(R.id.editField);
        MaterialButton submitButton = dialogView.findViewById(R.id.submitButton);

        if (field.equals("name")) {
            dialogTitle.setText("Edit Name");
            editField.setText(nameText.getText().toString());
        } else {
            dialogTitle.setText("Edit Phone");
            editField.setText(phoneText.getText().toString());
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        submitButton.setOnClickListener(v -> {
            String newValue = editField.getText().toString().trim();
            db.collection("users").document(currentUser.getUid())
                    .update(field, newValue)
                    .addOnSuccessListener(aVoid -> {
                        if (field.equals("name")) nameText.setText(newValue);
                        else phoneText.setText(newValue);
                        dialog.dismiss();
                    });
        });
    }
}



//package com.example.e_shopper;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//public class ProfileFragment extends Fragment {
//    private TextInputEditText nameInput, emailInput, phoneInput;
//    private MaterialButton saveButton;
//    private FirebaseFirestore db;
//    private FirebaseUser currentUser;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        initializeViews(view);
//        loadUserData();
//
//        saveButton.setOnClickListener(v -> saveUserData());
//
//        return view;
//    }
//
//    private void initializeViews(View view) {
//        nameInput = view.findViewById(R.id.nameInput);
//        emailInput = view.findViewById(R.id.emailInput);
//        phoneInput = view.findViewById(R.id.phoneInput);
//        saveButton = view.findViewById(R.id.saveButton);
//
//        db = FirebaseFirestore.getInstance();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        emailInput.setText(currentUser.getEmail());
//        emailInput.setEnabled(false); // Email can't be changed
//    }
//
//    private void loadUserData() {
//        if (currentUser != null) {
//            db.collection("users").document(currentUser.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            nameInput.setText(documentSnapshot.getString("name"));
//                            phoneInput.setText(documentSnapshot.getString("phone"));
//                        }
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
//        }
//    }
//
//    private void saveUserData() {
//        if (currentUser != null) {
//            String name = nameInput.getText().toString().trim();
//            String phone = phoneInput.getText().toString().trim();
//
//            db.collection("users").document(currentUser.getUid())
//                    .update(
//                            "name", name,
//                            "phone", phone
//                    )
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                        requireActivity().onBackPressed();
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
//        }
//    }
//}