package com.example.scannr.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.authentication.Validation;
import com.example.scannr.dashboard.Dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Validation validate = new Validation();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userEmail = "";

    public SettingsFragment() {
        // require a empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get preference components
        EditTextPreference editName = (EditTextPreference) findPreference("editName");
        EditTextPreference editPhone = (EditTextPreference) findPreference("editPhone");
        EditTextPreference editBirthday = (EditTextPreference) findPreference("editBirthday");
        EditTextPreference editEmail = (EditTextPreference) findPreference("editEmail");
        EditTextPreference editPassword = (EditTextPreference) findPreference("editPassword");
        Preference sessionLogout = (Preference) findPreference("sessionLogout");


        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        // add default values to preferences
                        if (editName != null) {
                            editName.setSummary(((String) documentSnapshot.get("fName") + " " +
                                    (String) documentSnapshot.get("mInitial") + " " +
                                    (String) documentSnapshot.get("lName")).trim().replaceAll(" +", " "));
                        }
                        if (editPhone != null) {
                            editPhone.setSummary((String) documentSnapshot.get("phoneNumber"));
                        }
                        if (editBirthday != null) {
                            editBirthday.setSummary((String) documentSnapshot.get("dob"));
                        }
                        if (editEmail != null) {
                            userEmail = (String) documentSnapshot.get("email");
                            editEmail.setSummary(userEmail);
                        }
                        editPassword.setSummary("************");

                    } catch (Exception e){
                        System.out.println(e);
                    }
                });

        // update phone number in firebase
        if (editPhone != null) {
            editPhone.setOnPreferenceChangeListener((preference, newValue) -> {
                if (validate.isEmptyPhoneNumber(newValue.toString())) {
                    return false;
                }

                db.collection("users")
                        .document(mAuth.getUid())
                        .update("phoneNumber", newValue.toString());
                updateSummary(preference, newValue.toString());
                return true;
            });
        }

        // update birthday in firebase
        if (editBirthday != null) {
            editBirthday.setOnPreferenceChangeListener((preference, newValue) -> {
                if (validate.isEmptyDateOfBirth(newValue.toString())) {
                    return false;
                }

                db.collection("users")
                        .document(mAuth.getUid())
                        .update("dob", newValue.toString());
                updateSummary(preference, newValue.toString());
                return true;
            });
        }

        // update email in firebase
        if (editEmail != null) {
            editEmail.setOnPreferenceChangeListener((preference, newValue) -> {
                if (validate.isEmptyEmail(newValue.toString()) || !validate.validateEmail(newValue.toString())) {
                    return false;
                }

                db.collection("users")
                        .document(mAuth.getUid())
                        .update("email", newValue.toString());
                updateSummary(preference, newValue.toString());
                return true;
            });
        }

        // update password on preference click
        if (editPassword != null) {
            editPassword.setOnPreferenceClickListener(preference -> {
                // get current user
                FirebaseUser user = mAuth.getCurrentUser();

                // get current user's email
                String email = userEmail;

                // open custom dialog_edit_password
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_password, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // get current user's password
                String password = ((EditTextPreference) findPreference("currentPassword")).getText();

                // get new password
                String newPassword = ((EditTextPreference) findPreference("newPassword")).getText();

                // re-authenticate user
                updateUserPassword(user, email, password, newPassword);

                return true;
            });
        }


        // check to see if name preference is clicked, and open dialogue
        if (editName != null) {
            editName.setOnPreferenceClickListener(preference -> {
                // open custom dialog_edit_name
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setPositiveButton("OK",
                        (dialog, id) -> {
                            // update fName, mInitial, and lName in firebase if value exists
                            System.out.println("lryanle "+dialog.toString() + " +");

                        });
                builder.setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_name, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                try {
                    String fName = ((EditText) dialogView.findViewById(R.id.fName)).getText().toString();
                    String mInitial = ((EditText) dialogView.findViewById(R.id.mInitial)).getText().toString();
                    String lName = ((EditText) dialogView.findViewById(R.id.lName)).getText().toString();

                    if (!fName.isEmpty()) {
                        db.collection("users")
                                .document(mAuth.getUid())
                                .update("fName", fName);
                    }
                    if (!mInitial.isEmpty()) {
                        db.collection("users")
                                .document(mAuth.getUid())
                                .update("mInitial", mInitial);
                    }
                    if (!lName.isEmpty()) {
                        db.collection("users")
                                .document(mAuth.getUid())
                                .update("lName", lName);
                    }

                    // update summary
                    updateSummary(preference, (fName + " " + mInitial + " " + lName).trim().replaceAll(" +", " "));
                } catch (Exception e) {
                    System.out.println(e);
                }




                return true;
            });
        }

        // signout if session logout button is clicked
        if (sessionLogout != null) {
            sessionLogout.setOnPreferenceClickListener(preference -> {
                logout();
                return true;
            });
        }
    }


    private void updateUserPassword(FirebaseUser user, String email, String currentPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider
            .getCredential(email, currentPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Password updated");
                                } else {
                                    Log.d(TAG, "Error password not updated");
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                    }
                }
            });
    }

    private void updateSummary(Preference preference, String newValue) {
        preference.setSummary(newValue);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        // Add the buttons
        builder.setView(inflater.inflate(R.layout.dialog_logout, null))
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    // Sign Out
                    mAuth.signOut();
                    requireActivity().finish();
//                    requireActivity().overridePendingTransition(0, 0);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
