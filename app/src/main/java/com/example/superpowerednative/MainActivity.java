package com.example.superpowerednative;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private ActivityResultLauncher<Intent> fileBrowserLauncher = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileBrowserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Handle the return of the save as dialog.
            if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                Intent resultData = result.getData();
                if (resultData != null) {
                    Uri u = resultData.getData();
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(u, "w");
                        if (pfd != null) {
                            Intent serviceIntent = new Intent(this, KaraokeService.class);
                            serviceIntent.putExtra("fileDescriptor", pfd.detachFd());
                            ContextCompat.startForegroundService(this, serviceIntent);
                            recording = true;
                            playing = true;
                            updateButton();
                        } else Log.d("Recorder", "File descriptor is null.");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.musicbutton);
        b.setVisibility(View.GONE);

        Button record = findViewById(R.id.recordbutton);
        record.setVisibility(View.GONE);

        PlayYoutubeVideo();


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
        StartStop(record);
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

    private void showButton() {
        Button b = findViewById(R.id.recordbutton);
        b.setVisibility(View.VISIBLE);
    }

    private void updateButton() {
        Button music = findViewById(R.id.musicbutton);

        if (recording)
        {
            music.setVisibility(View.VISIBLE);
            music.setText(playing ? "Pause" : "Resume");
        }
        else music.setVisibility(View.GONE);

        Button record = findViewById(R.id.recordbutton);
        record.setText(recording ? "Stop" : "Start");
    }

    private void startService(String action)
    {
        Intent serviceIntent = new Intent(this, KaraokeService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    public void PlayPause(View button)
    {
        playing = !playing;
        updateButton();
        startService("play/pause");
    }

    private void PlayYoutubeVideo()
    {
        WebView webView = findViewById(R.id.webView);
        String video = "<iframe id=\"ytplayer\" type=\"text/html\" width=\"720\" height=\"405\"\n" +
                "src=\"https://www.youtube.com/embed/lm3t3myPspk?autoplay=1\"\n" +
                "frameborder=\"0\" allowfullscreen>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(video, "text/html", "utf-8");
        webView.setWebChromeClient(new WebChromeClient());
    }

    public void StartStop(View button)
    {
        if (recording)
        {
            startService("stop");
            recording = false;
            updateButton();
        } else
        {
            // Open the file browser to pick a destination.
            android.content.Intent intent = new android.content.Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            intent.putExtra(Intent.EXTRA_TITLE, "recording.wav");
            fileBrowserLauncher.launch(intent);
        }
    }
}