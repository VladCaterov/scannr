package com.example.scannr.authentication;

import android.util.Patterns;

import java.util.regex.Pattern;

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
    public boolean isEmptyMiddleInitial(String mInitial) { return mInitial.isEmpty(); }
    public boolean isEmptyLastName(String lName) { return lName.isEmpty(); }

    public boolean validatePhoneNumber(String phoneNumber){
        return phoneNumber.length() == 14;
    }
    public boolean isEmptyPhoneNumber(String phoneNumber) { return phoneNumber.isEmpty(); }
    public boolean isEmptyDateOfBirth(String dateOfBirth) { return dateOfBirth.isEmpty(); }

    public boolean isEmptyBankAccountNumber(String bankAccountNumber) { return bankAccountNumber.isEmpty(); }
    public boolean validateBankAccountNumber(String bankAccountNumber){
        return bankAccountNumber.length() > 8;
    }

    public boolean isEmptyBankRoutingNumber(String bankRoutingNumber) { return bankRoutingNumber.isEmpty(); }
    public boolean isEmptySpendingLimit(String spendingLimit) { return spendingLimit.isEmpty(); }





}
