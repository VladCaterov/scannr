package com.example.scannr.receipts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.util.SparseIntArray;
import android.view.Surface;

import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.content.Context.CAMERA_SERVICE;

public class ImageVision {
    private static final String MY_CAMERA_ID = "cam";

    private void imageFromBitmap(Bitmap bitmap) {
        int rotationDegree = 0;
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
    }

    private void imageFromMediaImage(Image mediaImage, int rotation) {
        InputImage image = InputImage.fromMediaImage(mediaImage, rotation);
    }

    private void imageFromBuffer(ByteBuffer byteBuffer, int rotationDegrees) {
        InputImage image = InputImage.fromByteBuffer(byteBuffer,
                /* image width */ 480,
                /* image height */ 360,
                rotationDegrees,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12 or ImageFormat.YUV_420_888
        );
    }

    private void imageFromArray(byte[] byteArray, int rotation) {
        InputImage image = InputImage.fromByteArray(
                byteArray,
                /* image width */480,
                /* image height */360,
                rotation,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12 or ImageFormat.YUV_420_888
        );
    }

    private void imageFromPath(Context context, Uri uri) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // [START get_rotation]
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    // Get the angle by which an image must be rotated given the device's current orientation.
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing) throws CameraAccessException {
            // Get the device's current rotation relative to its "native" orientation.
            // Then, from the ORIENTATIONS table, look up the angle the image must be
            // rotated to compensate for the device's rotation.
            int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int rotationCompensation = ORIENTATIONS.get(deviceRotation);

            // Get the device's sensor orientation.
            CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
            int sensorOrientation = cameraManager
                    .getCameraCharacteristics(cameraId)
                    .get(CameraCharacteristics.SENSOR_ORIENTATION);

            if (isFrontFacing) {
                rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
            } else { // back-facing
                rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
            }
            return rotationCompensation;
        }

    private void getCompensation(Activity activity, boolean isFrontFacing) throws CameraAccessException {
        // Get the ID of the camera using CameraManager. Then:
        int rotation = getRotationCompensation(MY_CAMERA_ID, activity, isFrontFacing);
    }
}
