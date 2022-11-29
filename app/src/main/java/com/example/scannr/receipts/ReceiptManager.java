package com.example.scannr.receipts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.scannr.R;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ReceiptManager extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiptmanager);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
    }

    public void buttonReadText(View view){
        try {
            String stringFileName = "/storage/emulated/0/Download/" + editText.getText().toString();
            bitmap = BitmapFactory.decodeFile(stringFileName);
            imageView.setImageBitmap(bitmap);
            TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);



            //            Frame frameImage = new Frame.Builder().setBitmap(bitmap).build();
//            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);
//            String stringImageText = "";
//            for (int i = 0; i<textBlockSparseArray.size();i++){
//                TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
//                stringImageText = stringImageText + " " + textBlock.getValue();
//            }
//            textView.setText(stringImageText);
        }
        catch (Exception e){
            textView.setText("Failed");
        }
    }
}