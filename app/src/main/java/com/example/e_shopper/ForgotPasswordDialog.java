package com.example.e_shopper;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_forgot_password, null);
        TextInputEditText emailInput = view.findViewById(R.id.emailInput);

        builder.setView(view)
                .setTitle("Reset Password")
                .setPositiveButton("Send Reset Link", (dialog, id) -> {
                    String email = emailInput.getText().toString().trim();
                    if (!email.isEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(requireContext(),
                                                "Password reset email sent", Toast.LENGTH_LONG).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(),
                                                "Failed to send reset email: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show());
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }
}