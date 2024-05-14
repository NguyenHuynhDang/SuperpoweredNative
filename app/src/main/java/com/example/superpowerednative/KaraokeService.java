package com.example.superpowerednative;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class KaraokeService{
    static {
        System.loadLibrary("superpowerednative"); // Load native library.
    }
    private int samplerate, buffersize;

    public KaraokeService(int samplerate, int buffersize)
    {
        this.samplerate = samplerate;
        this.buffersize = buffersize;
    }

    public void startRecord()
    {
        Karaoke(samplerate, buffersize);
    }

    public native void Karaoke(int samplerate, int buffersize);
    public native void StopRecording();
}