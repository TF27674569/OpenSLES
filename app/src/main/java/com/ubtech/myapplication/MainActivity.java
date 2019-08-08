package com.ubtech.myapplication;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/南方姑娘[1].mp3";

    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ESAudio.createEngine();
        int sampleRate = 0;
        int bufSize = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager myAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String nativeParam = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            sampleRate = Integer.parseInt(nativeParam);
            nativeParam = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            bufSize = Integer.parseInt(nativeParam);
        }

        Log.e("TAG", "sampleRate: "+sampleRate+"    bufSize:"+bufSize );
        ESAudio.createBufferQueueAudioPlayer(sampleRate, bufSize);


        findViewById(R.id.sample_text)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ESAudio.createUriAudioPlayer(path);
                    }
                });

        findViewById(R.id.play)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isPlaying = !isPlaying;
                        ESAudio.setPlayingUriAudioPlayer(isPlaying);
                    }
                });
    }

}
