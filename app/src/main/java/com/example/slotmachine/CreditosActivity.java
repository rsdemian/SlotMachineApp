package com.example.slotmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

public class CreditosActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);
        Musica();
    }

    private void Musica(){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,R.raw.creditos);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                StopMusic();
            }
        });

        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void StopMusic(){
        //mediaPlayer.release();
        //mediaPlayer = null;
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopMusic();
    }
}