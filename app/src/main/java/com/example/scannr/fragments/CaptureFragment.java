package com.example.scannr.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class CaptureFragment<textRecognizer> extends Fragment {
    // UI Views
    public FloatingActionButton captureButton;
    public FloatingActionButton filesButton;
    private ShapeableImageView imageView;

    // URI of the image
    private Uri imageUri = null;

    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 101;

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
        captureButton = view.findViewById(R.id.captureButton);
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

        captureButton.setOnClickListener(new View.OnClickListener() {
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
                            Log.d("CaptureFragment", "onSuccess: recognizedText" + recognizedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Log.e("CaptureFragment", "onFailure: ", e);
                            Toast.makeText(getActivity(), "Failed recognizing text due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            // exception occured while preparing InputImage, dismiss dialog, show reason in toast
            pd.dismiss();
            Log.e("CaptureFragment", "recognizeTextFromImage: ", e);
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
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
        // check if storage permission is enabled or not
        // return true if enabled and false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions() {
        // request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        // check if camera permission is enabled or not
        // return true if enabled and false if not enabled
        boolean cameraResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;
    }

    private void requestCameraPermissions() {
        // request runtime camera permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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