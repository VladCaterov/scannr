package com.example.scannr;

import android.util.Patterns;

public class Validation {

    public boolean validateEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public boolean isEmptyEmail(String email){
        return email.isEmpty();
    }

    public boolean validatePassword(String password) {
        return password.length() < 6;
    }
    public boolean isEmptyPassword(String password){
        return password.isEmpty();
    }

    public boolean isEmptyFirstName(String fName) { return fName.isEmpty(); }
    public boolean isEmptyLastName(String lName) { return lName.isEmpty(); }
    public boolean isEmptyPhoneNumber(String phoneNumber) { return phoneNumber.isEmpty(); }


}
