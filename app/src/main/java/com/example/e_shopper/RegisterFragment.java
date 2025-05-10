package com.example.e_shopper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText nameInput, emailInput, passwordInput;
    private MaterialButton registerButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initializeFirebase();
        initializeViews(view);
        setupAnimations(view);
        setupListeners(view);
        return view;
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initializeViews(View view) {
        nameInput = view.findViewById(R.id.nameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        registerButton = view.findViewById(R.id.registerButton);
    }

    private void setupAnimations(View view) {
        int[] animationIds = {
                R.id.registerTitle, R.id.nameLayout,
                R.id.emailLayout, R.id.passwordLayout,
                R.id.registerButton
        };
        int animationType = R.anim.fade_slide_up;
        for (int viewId : animationIds) {
            view.findViewById(viewId).startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), animationType)
            );
        }
    }

    private void setupListeners(View view) {
        registerButton.setOnClickListener(v -> attemptRegistration());
        view.findViewById(R.id.loginLink).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private boolean validateInput(String name, String email, String password) {
        boolean isValid = true;

        if (name.isEmpty() || name.length() < 2) {
            nameInput.setError("Name must be at least 2 characters");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            isValid = false;
        } else {
            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters");
                isValid = false;
            }
            if (!password.matches(".*[A-Z].*")) {
                passwordInput.setError("Password must contain at least one uppercase letter");
                isValid = false;
            }
            if (!password.matches(".*[a-z].*")) {
                passwordInput.setError("Password must contain at least one lowercase letter");
                isValid = false;
            }
            if (!password.matches(".*\\d.*")) {
                passwordInput.setError("Password must contain at least one number");
                isValid = false;
            }
        }
        return isValid;
    }

    private void attemptRegistration() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInput(name, email, password)) {
            return;
        }

        registerButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            createUserDocument(user.getUid(), name, email);
                        } else {
                            showToast("Authentication failed");
                            registerButton.setEnabled(true);
                        }
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void createUserDocument(String userId, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("userId", userId);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Registration successful");
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to save user data: " + e.getMessage());
                    registerButton.setEnabled(true);
                });
    }

    private void handleRegistrationError(Exception e) {
        String errorMessage = "Registration failed";

        if (e instanceof FirebaseAuthUserCollisionException) {
            errorMessage = "An account with this email already exists";
            emailInput.setError(errorMessage);
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid email or password";
            emailInput.setError(errorMessage);
        } else {
            errorMessage = "Registration failed: " + e.getMessage();
        }

        showToast(errorMessage);
        registerButton.setEnabled(true);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
