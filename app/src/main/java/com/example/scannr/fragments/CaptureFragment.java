package com.example.scannr.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.scannr.R;
import com.example.scannr.family.ChildAccountManager;
import com.example.scannr.receipts.PurchaseHistoryManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;

public class CaptureFragment<textRecognizer> extends Fragment {
    String TAG = "CaptureFragment";
    // UI Views
    public FloatingActionButton convertButton;
    public FloatingActionButton filesButton;
    private ShapeableImageView imageView;

    // URI of the image
    private Uri imageUri = null;

    public static final int CAMERA_REQUEST_CODE = 5432653;
    public static final int STORAGE_REQUEST_CODE = 5432654;

    // array of permissions required to pick image from camera and gallery
    private String[] cameraPermissions;
    private String[] storagePermissions;

    // progress dialog
    private ProgressDialog pd;

    // text recognizer
    private TextRecognizer textRecognizer;

    public CaptureFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI Views
        convertButton = view.findViewById(R.id.convertButton);
        filesButton = view.findViewById(R.id.filesButton);
        imageView = view.findViewById(R.id.cameraViewOutput);

        // Initialize Permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading...");
        pd.setCanceledOnTouchOutside(false);

        // initialize text recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Set onClickListeners
        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialogue(); // output
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    Toast.makeText(getActivity(), "Select image first", Toast.LENGTH_SHORT).show();
                } else {
                    recognizeTextFromImage(); // output
                }
            }
        });
    }

    private void sortContents(String text) { // TODO: move to PHM
        String[] cache = new String[3];
        Arrays.fill(cache, "0");
        cache[1] = "2022.11.29"; // TODO: REMOVE
        cache[2] = "2.09"; // TODO: REMOVE
        ArrayList<Float> moneyList = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(text.trim());
        while (st.hasMoreTokens()) { // We need 3 things: Business name (0), Date (1), Receipt Total (2)
            String currToken = st.nextToken();
            if (currToken.contains("Texas") || currToken.contains("BON")) { // Business name
                cache[0] = currToken;
            } else if (currToken.contains("/") || currToken.contains("-") && (currToken.matches(".*/.*/.*") || currToken.matches(".*-.*-.*"))) { // Date
                cache[1] = currToken;
            } else if ((currToken.contains("$") || currToken.contains(".")) && currToken.replaceAll("[^\\d.]", "").matches("[-+]?[0-9]*\\.?[0-9]+")) { // Receipt Total (with $)
                moneyList.add(Float.parseFloat(currToken.replaceAll("[^\\d.]", "")));
            }
//            else if (currToken.contains(".")) {// (with .)
//                // TODO: Handle decimals
//            }
        }

        if (moneyList.size() > 0) {
            //traverse moneyList and find the biggest value and put it in cache[3]
            float max = 0;
            int index = 0;
            for (int i = 0; i < moneyList.size(); i++) {
                if (moneyList.get(i) > max) {
                    max = moneyList.get(i);
                    index = i;
                }
            }

            //set cache[3] to the biggest value
            cache[2] = moneyList.get(index).toString() ;
        }

        // alert dialog builder to show business name, date, and total
        final EditText bNameOutputField = new EditText(getActivity());
        final EditText dateField = new EditText(getActivity());
        final EditText totalField = new EditText(getActivity());
        bNameOutputField.setText(cache[0]);
        dateField.setText(cache[1]);
        totalField.setText(cache[2]);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(bNameOutputField);
        linearLayout.addView(dateField);
        linearLayout.addView(totalField);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Receipt Information");
//        builder.setMessage("Business Name: " + cache[0] + "\nDate: " + cache[1] + "\nTotal: " + cache[2]);
        builder.setView(linearLayout);
        builder.setPositiveButton("Save", (dialog, id) -> {
            cache[0] = bNameOutputField.getText().toString();
            cache[1] = dateField.getText().toString();
            cache[2] = totalField.getText().toString();
        });
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        builder.create().show();

        // add to PHM
        try {
            PurchaseHistoryManager.addReceipt(cache[0], cache[1], Float.parseFloat(cache[2]));
            Toast.makeText(getActivity(), "Receipt added successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "addReceipt: Error adding document" + e);
            Toast.makeText(getActivity(), "Error adding receipt", Toast.LENGTH_SHORT).show();
        }

    }

    private void recognizeTextFromImage() {
        Log.d("CameraFragment", "recognizeTextFromImage: ");
        pd.setMessage("Recognizing Image...");
        pd.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(getActivity(), imageUri);

            pd.setMessage("Recognizing Text...");

            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            pd.dismiss();
                            String recognizedText = text.getText();

                            // TODO: IMAGE OUTPUT
                            sortContents(recognizedText);
                            Log.d(TAG, "onSuccess: recognizedText" + recognizedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Log.e(TAG, "onFailure: ", e);
                            Toast.makeText(getActivity(), "Failed recognizing text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            // exception occured while preparing InputImage, dismiss dialog, show reason in toast
            pd.dismiss();
            Log.e(TAG, "recognizeTextFromImage: ", e);
            Toast.makeText(getActivity(), "Failed preparing image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputImageDialogue() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), filesButton);

        // Add items to the popup menu
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        // Set onClickListener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int id = item.getItemId();
                if (id == 1) {
                    Log.d("CameraFragment", "onMenuItemClick: Camera Clicked");
                    if (checkCameraPermissions()) {
                        Log.d("CameraFragment", "onMenuItemClick: Camera Clicked; Permissions Granted");
                        pickImageCamera();
                    } else {
                        requestCameraPermissions();
                    }
                } else if (id == 2) {
                    Log.d("CameraFragment", "onMenuItemClick: Gallery Clicked");
                    if (checkStoragePermissions()) {
                        pickImageGallery();
                    } else {
                        requestStoragePermissions();
                    }
                }
                return true;
            }
        });
    }

    private void pickImageGallery() {
        Log.d("CameraFragment", "pickImageGallery: ");
        // open gallery
        Intent intent = new Intent(Intent.ACTION_PICK);

        // Set intent type to image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: galleryActivityResultLauncher");
                    // if image is selected
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();

                        Log.d("CameraFragment", "onActivityResult: imageUri: " + imageUri);

                        imageView.setImageURI(imageUri);
                    } else {
                        Log.d("CameraFragment", "onActivityResult: cancelled");
                        // if image is not selected (cancelled)
                        Toast.makeText(getActivity(), "Upload Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera() {
        Log.d("CameraFragment", "pickImageCamera: ");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: cameraActivityResultLauncher");
                    // if image is captured from camera
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Log.d("CameraFragment", "onActivityResult: imageUri: " + imageUri);

                        imageView.setImageURI(imageUri);
                    } else {
                        Log.d("CameraFragment", "onActivityResult: cancelled");
                        // if image is not captured (cancelled)
                        Toast.makeText(getActivity(), "Capture Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    boolean checkStoragePermissions() {
        Log.d(TAG, "checkStoragePermissions: ");
        // check if storage permission is enabled or not
        // return true if enabled and false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions() {
        Log.d(TAG, "requestStoragePermissions: ");
        // request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        // check if camera permission is enabled or not
        // return true if enabled and false if not enabled
        boolean cameraResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        Log.d(TAG, "checkCameraPermissions: cameraResult: " + cameraResult + " | storageResult: " + storageResult);
        Log.d(TAG, "checkCameraPermissions: cameraPermissions: " + Arrays.toString(cameraPermissions));

        return cameraResult && storageResult;
    }

    private void requestCameraPermissions() {
        Log.d(TAG, "requestCameraPermissions: ");
        // request runtime camera permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // if camera permission is granted
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        // if camera permission is denied
                        Toast.makeText(getActivity(), "Camera Permissions Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // if camera permission is denied
                    Toast.makeText(getActivity(), "Capture Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                // if storage permission is granted
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        // permission granted
                        pickImageGallery();
                    } else {
                        // if storage permission is denied
                        Toast.makeText(getActivity(), "Storage Permissions Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // if storage permission is denied
                    Toast.makeText(getActivity(), "Upload Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
}