package com.example.slotmachine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private ListView lvRanking;
    private ArrayAdapter adapter;
    private List<Ranking> listaRanking;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public FirebaseAuth auth;
    public FirebaseAuth.AuthStateListener authStateListener;

    private ChildEventListener childEventListener;
    private Query query;

    private Ranking objRanking;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mediaPlayer = null;
        Musica();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();//pegando a referência do banco inteiro

        lvRanking = findViewById(R.id.lvRanking);
        listaRanking = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaRanking);
        lvRanking.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RankingBanco();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopMusic();
    }

    private void Musica(){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,R.raw.music10);
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

    private void RankingBanco(){
        listaRanking.clear();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        query = databaseReference.child("Ranking").orderByChild("pontuacao").limitToLast(20);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Ranking ranking = new Ranking();
                ranking.setId(snapshot.getKey());
                ranking.setNomePlayer(snapshot.child("nomePlayer").getValue(String.class));
                ranking.setPontuacao(snapshot.child("pontuacao").getValue(Integer.class));

                listaRanking.add(ranking);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idRanking = snapshot.getKey();
                for(Ranking ranking : listaRanking){
                    if(ranking.getId().equals(idRanking)){
                        ranking.setNomePlayer(snapshot.child("nomePlayer").getValue(String.class));
                        ranking.setPontuacao(snapshot.child("pontuacao").getValue(Integer.class));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idRanking = snapshot.getKey();
                for(Ranking ranking : listaRanking){
                    if(ranking.getId().equals(idRanking)){
                        listaRanking.remove(ranking);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        query.addChildEventListener(childEventListener);
    }
}