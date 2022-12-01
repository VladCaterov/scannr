package com.example.scannr.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class DatabaseHandler {
    //USED: https://stackoverflow.com/questions/47244403/how-to-move-a-document-in-cloud-firestore
    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    toPath.set(document.getData())
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                fromPath.delete()
                                        .addOnSuccessListener(aVoid1 -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
