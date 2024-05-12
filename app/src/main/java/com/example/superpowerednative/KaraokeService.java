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

public class KaraokeService extends Service {
    public static final String CHANNELID = "KaraokeServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (HandleAction(intent) == START_NOT_STICKY)
            return START_NOT_STICKY;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNELID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNELID).setContentTitle("Karaoke Service").build();
        startForeground(1, notification);

        initialize(intent);

        return START_NOT_STICKY;
    }

    private int HandleAction(Intent intent)
    {
        String action = intent.getAction();
        if (action == null) return -1;

        switch (action)
        {
            case "stop":
                stopForeground(true);
                stopSelf();
                return START_NOT_STICKY;
            case "play/pause":
                TogglePlayBack();
                return START_NOT_STICKY;
        }

        return -1;
    }

    private void initialize(Intent intent)
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

        System.loadLibrary("superpowerednative"); // Load native library.
        Karaoke(samplerate, buffersize, intent.getIntExtra("fileDescriptor", 0));
        OpenFileFromAPK(samplerate, buffersize);
    }

    private void OpenFileFromAPK(int samplerate, int buffersize)
    {
        // Files under res/raw are not zipped, just copied into the APK.
        // Get the offset and length to know where our file is located.
        AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.myriadoflight);
        int fileOffset = (int)fd.getStartOffset();
        int fileLength = (int)fd.getLength();
        try {
            fd.getParcelFileDescriptor().close();
        } catch (IOException e) {
            Log.e("SuperpoweredNative", "Close error.");
        }

        String path = getPackageResourcePath();         // get path to APK package
        OpenFileFromAPK(path, fileOffset, fileLength); // open audio file from APK
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        StopRecording();
        CleanUp();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    public native void Karaoke(int samplerate, int buffersize, int destinationfd);
    public native void OpenFileFromAPK(String path, int offset, int length);

    public native void TogglePlayBack();
    public native void CleanUp();
    public native void StopRecording();
}