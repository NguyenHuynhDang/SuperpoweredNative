package com.example.superpowerednative;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.superpowerednative.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private boolean playing = false;
    private boolean recording = false;
    private KaraokeService karaokeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button record = findViewById(R.id.recordbutton);
        record.setVisibility(View.GONE);

        // Checking permissions.
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        };
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }

        // Got all permissions
        setKaraokeService();
        showButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Called when the user answers to the permission dialogs.
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length)) return;
        boolean hasAllPermissions = true;

        for (int grantResult:grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false;
            Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
        }

        if (hasAllPermissions)
            showButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        karaokeService.StopRecording();
    }

    private void setKaraokeService()
    {
        // Get the device's sample rate and buffer size to enable
        // low-latency Android audio output, if available.
        String samplerateString = null, buffersizeString = null;
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        if (samplerateString == null) samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "480";
        int samplerate = Integer.parseInt(samplerateString);
        int buffersize = Integer.parseInt(buffersizeString);

        karaokeService = new KaraokeService(samplerate, buffersize);
    }

    private void showButton() {
        Button b = findViewById(R.id.recordbutton);
        b.setVisibility(View.VISIBLE);
    }

    private void updateButton() {
        Button record = findViewById(R.id.recordbutton);
        record.setText(recording ? "Stop" : "Start");
    }

    public void StartStop(View button)
    {
        recording = !recording;
        if (recording)
        {
            karaokeService.startRecord();
            updateButton();
        } else
        {
            karaokeService.StopRecording();
            updateButton();
        }
    }
}