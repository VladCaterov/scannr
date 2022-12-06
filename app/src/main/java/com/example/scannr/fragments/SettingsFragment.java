package com.example.scannr.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.scannr.R;
import com.example.scannr.authentication.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
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
        Preference editName = findPreference("editName");
        Preference editPhone = findPreference("editPhone");
        Preference editBirthday = findPreference("editBirthday");
        Preference editEmail =  findPreference("editEmail");
        Preference editPassword = findPreference("editPassword");
        Preference sessionLogout = findPreference("sessionLogout");


        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        // ADD DEFAULT VALUES TO PREFERENCES
                        if (editName != null) {
                            if (Objects.equals(documentSnapshot.get("mInitial"), "")){
                                editName.setSummary((documentSnapshot.get("fName") + " " +
                                        documentSnapshot.get("lName")));
                            } else {
                                editName.setSummary((documentSnapshot.get("fName") + " " +
                                        documentSnapshot.get("mInitial") + ". " +
                                        documentSnapshot.get("lName")));
                            }
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

        // UPDATE USER NAME check to see if name preference is clicked, and open dialogue
        if (editName != null) {
            editName.setOnPreferenceClickListener(preference -> {

                // open custom dialog_edit_name
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_name, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                EditText fNameField = dialogView.findViewById(R.id.fName);
                EditText mInitialField = dialogView.findViewById(R.id.mInitial);
                EditText lNameField = dialogView.findViewById(R.id.lName);

                // set default values
                db.collection("users")
                        .document(Objects.requireNonNull(mAuth.getUid()))
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            try {
                                fNameField.setText((String) documentSnapshot.get("fName"));
                                mInitialField.setText((String) documentSnapshot.get("mInitial"));
                                lNameField.setText((String) documentSnapshot.get("lName"));
                            } catch (Exception e){
                                System.out.println(e);
                            }
                        });

                builder.setPositiveButton("SAVE", (dialog, id) -> {
                    // update fName, mInitial, and lName in firebase if value exists
                    String fName = fNameField.getText().toString();
                    String mInitial = mInitialField.getText().toString();
                    String lName = lNameField.getText().toString();


                    if (validate.isEmptyFirstName(fName)) {
                        Toast.makeText(getActivity(), "New First Name Must Not Be Empty",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (validate.isEmptyLastName(lName)) {
                        Toast.makeText(getActivity(), "New Last Name Must Not Be Empty",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            DocumentReference userDocument = db.collection("users").document(mAuth.getUid());

                            userDocument.update("fName", fName);
                            getActivity().getIntent().putExtra("fName", fName);
                            userDocument.update("mInitial", mInitial);
                            getActivity().getIntent().putExtra("mInitial", mInitial);
                            userDocument.update("lName", lName);
                            getActivity().getIntent().putExtra("lName", lName);

                            String displayName = fName.toUpperCase() + " " + lName.toUpperCase();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            assert mAuth.getCurrentUser() != null;
                            mAuth.getCurrentUser().updateProfile(profileUpdates);
                            // update summary
                            if (validate.isEmptyMiddleInitial(mInitial)){
                                updateSummary(preference, (fName + " " + lName));
                            } else {
                                updateSummary(preference, (fName + " " + mInitial + ". " + lName));
                            }
                            Toast.makeText(getActivity(), "Name Changed Successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                });
                builder.setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            });
        }

        // UPDATE PHONE NUMBER IN FIREBASE
        if (editPhone != null) {
            editPhone.setOnPreferenceClickListener(preference -> {
                // open custom dialog_edit_name
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_phone, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText editPhoneNumber = dialogView.findViewById(R.id.phoneEdit);
                editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
                builder.setPositiveButton("SAVE", (dialog, id) -> {
                    String phoneNumber = editPhoneNumber.getText().toString();
                    if (validate.isEmptyPhoneNumber(phoneNumber)){
                        Toast.makeText(getActivity(), "New Phone Number Must Not Be Empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } if (!validate.validatePhoneNumber(phoneNumber)){
                        Toast.makeText(getActivity(), "New Phone Number Must Be Valid",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        try {
                            DocumentReference userDocument = db.collection("users").document(mAuth.getUid());
                            userDocument.update("phoneNumber", phoneNumber);
                            updateSummary(preference, phoneNumber);
                            Toast.makeText(getActivity(), "Phone Number Changed Successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            });
        }
        // update birthday in firebase
        if (editBirthday != null) {
            editBirthday.setOnPreferenceClickListener(preference -> {
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_birthday, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText editBirthday1 = dialogView.findViewById(R.id.birthdayEdit);
                builder.setPositiveButton("SAVE", (dialog, id) -> {
                    String eb = editBirthday1.getText().toString();
                    if (validate.isEmptyDateOfBirth(eb)){
                        Toast.makeText(getActivity(), "New Birthday Must Not Be Empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        try {
                            DocumentReference userDocument = db.collection("users").document(mAuth.getUid());
                            userDocument.update("dob", eb);
                            updateSummary(preference, eb);
                            Toast.makeText(getActivity(), "Birthday Changed Successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            });
        }

        // update email in firebase
        if (editEmail != null) {
            editEmail.setOnPreferenceClickListener(preference -> {
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_email, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                EditText newEmail = dialogView.findViewById(R.id.emailNewEdit);
                EditText currentEmail = dialogView.findViewById(R.id.emailCurrentEdit);
                EditText currentPassword = dialogView.findViewById(R.id.passwordCurrentEdit);
                builder.setPositiveButton("SAVE", (dialog, id) -> {
                    String newEmailText = newEmail.getText().toString();
                    String currentEmailText = currentEmail.getText().toString();
                    String currentPasswordText = currentPassword.getText().toString();

                    if (validate.isEmptyEmail(newEmailText)){
                        Toast.makeText(getActivity(), "New Email Must Not Be Empty",
                                Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                    if (!validate.validateEmail(newEmailText)){
                        Toast.makeText(getActivity(), "New Email Must Be Valid",
                                Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        try {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(currentEmailText, currentPasswordText);

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updateEmail(newEmailText).addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        DocumentReference userDocument = db.collection("users").document(mAuth.getUid());
                                                        userDocument.update("email", newEmail);

                                                        updateSummary(preference, newEmailText);
                                                        Toast.makeText(getActivity(), "Email Changed Successfully",
                                                                Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Email updated");
                                                    } else {
                                                        Toast.makeText(getActivity(), "Unable to change Email",
                                                                Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Error Email not updated");
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getActivity(), "Unable to authenticate",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "Error auth failed");
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
                // open custom dialog_edit_name
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_password, viewGroup, false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setPositiveButton("SAVE", (dialog, id) -> {
                    // update fName, mInitial, and lName in firebase if value exists
                    String currentPassword = ((EditText) dialogView.findViewById(R.id.currentPassword)).getText().toString();
                    String newPassword = ((EditText) dialogView.findViewById(R.id.newPassword)).getText().toString();

                    if (validate.isEmptyPassword(newPassword)) {
                        dialog.cancel();
                    }
                    if (validate.validatePassword(newPassword)) {
                        dialog.cancel();
                    }

                    try {
                        // re-authenticate user
                        updateUserPassword(user, email, currentPassword, newPassword);

                        String hiddenString = "";
                        for (int i = 0; i < newPassword.length(); i++) {
                            hiddenString += "*";
                        }

                        // update summary
                        updateSummary(preference, hiddenString);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
                builder.setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

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
        builder.setView(inflater.inflate(R.layout.dialog_logout, null))
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    // Sign Out
                    mAuth.signOut();
                    requireActivity().finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
