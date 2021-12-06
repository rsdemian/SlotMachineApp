package com.example.slotmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

public class Orientacoes extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientacoes);
        Musica();
    }

    private void Musica(){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,R.raw.starranking);
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