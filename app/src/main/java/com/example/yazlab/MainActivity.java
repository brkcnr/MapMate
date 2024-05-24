package com.example.yazlab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yazlab.R;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button button;
    private EditText editTextDest;
    private TextView mTextTv;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextDest = findViewById(R.id.destination);
        videoView = findViewById(R.id.video);
        String selectedLanguage = getSelectedLanguage();

        loadVideo(selectedLanguage);
        if(selectedLanguage.equals("english"))
        {
            editTextDest.setText("Where do you want to go?");
        }
        else
        {
            editTextDest.setText("Nereye gitmek istersin?");
        }
        initializeComponents();

        new Handler().postDelayed(this::speak, 5000);
    }

    private String getSelectedLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("selected_language", "english");

    }

    private void loadVideo(String language) {
        int videoResource = language.equals("english") ? R.raw.eng_video : R.raw.tr_video;


        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoResource));
        videoView.start();
    }

    private void initializeComponents() {

        videoView = findViewById(R.id.video);
        editTextDest = findViewById(R.id.destination);
        button = findViewById(R.id.btnSubmit);
        //mTextTv = findViewById(R.id.textTv);


        videoView.setOnCompletionListener(mp -> {
            videoView.start();
            speak();
        });


        button.setOnClickListener(view -> {
            String destination = editTextDest.getText().toString();
            if (destination.equals("")) {
                Toast.makeText(getApplicationContext(), "Enter both source and destination", Toast.LENGTH_SHORT).show();
            } else {

                showMap(destination);
            }
        });
    }

    private void showMap(String destination) {
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin="
                + ""
                + "&destination="
                + destination
                + "&travelmode=walking&dir_action=navigate"
        );

        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } else {
            Toast.makeText(this, "Your device doesn't support speech input.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String recognizedText = result.get(0);
                    //mTextTv.setText(recognizedText);
                    editTextDest.setText(recognizedText);
                    button.performClick();
                }
            }
        }
    }
}
