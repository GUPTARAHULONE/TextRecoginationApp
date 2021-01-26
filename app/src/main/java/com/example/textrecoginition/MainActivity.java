package com.example.textrecoginition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button captureImageBtn,detectTextBtn,b1;
    private ImageView imageView;
    private TextView textView;
    Bitmap imageBitmap;
    TextToSpeech t1;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn=findViewById(R.id.capture_image_btn);
        detectTextBtn=findViewById(R.id.detect_text_image_btn);
        imageView=findViewById(R.id.image_view);
        textView=findViewById(R.id.text_display);
        b1=findViewById(R.id.speak_btn);
        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
                textView.setText("");
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detectTextFromImage();
            }
        });

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeak();
            }
        });


    }

    private void textToSpeak() {

        String toSpeak = text;
        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);


        /*public void onPause()
        {
            if(t1 !=null){
                t1.stop();
                t1.shutdown();
            }
            super.onPause();
        }  */
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector =
                FirebaseVision.getInstance().getVisionTextDetector();
         firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
             @Override
             public void onSuccess(FirebaseVisionText firebaseVisionText) {

                 Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                 displayTextFromImage( firebaseVisionText);
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(MainActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                 Log.d("Error ", e.getMessage());
             }
         });

    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0)
        {
            Toast.makeText(this, "NO TEXT IS FOUND", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(FirebaseVisionText.Block block:firebaseVisionText.getBlocks())
            {
                 text=block.getText();
                textView.setText(text);
            }
        }
    }



    }



