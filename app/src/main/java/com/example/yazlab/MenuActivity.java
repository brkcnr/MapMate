package com.example.yazlab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yazlab.R;

import java.util.ArrayList;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LANGUAGE_INPUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.dil_ses);
        mediaPlayer.start();

        new Handler().postDelayed(this::startSpeechInput, 5000);
    }

    private void startSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        startActivityForResult(intent, REQUEST_CODE_LANGUAGE_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LANGUAGE_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String selectedLanguage = result.get(0).toLowerCase();

                    // Seçilen dili SharedPreferences ile kaydet
                    saveSelectedLanguage(selectedLanguage);

                    // Ana aktiviteye geri dön
                    goToMainActivity();
                }
            } else {
                // Hata durumunda kullanıcıya geri bildirim göster
                Toast.makeText(this, "Dil seçme işlemi başarısız oldu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void saveSelectedLanguage(String selectedLanguage) {
        // Seçilen dili SharedPreferences ile kaydet
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_language", selectedLanguage);
        editor.apply();
    }

    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
