package com.example.slotmachine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int moeda;
    int pontuacao;

    int pontuacaoMaquina;
    int moedaMaquina;

    int trilhaInicioJogo =  R.raw.music5;
    int trilhaPerdeu = R.raw.perdeu;
    int trilhaGanhou = R.raw.winner;

    private ListView lvRanking;
    private ArrayAdapter adapter;
    private List<Ranking> listaRanking;

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayerPerdeu;
    MediaPlayer mediaPlayerGanhou;

    private String nomePlayerPerdeu;
    private String nomePlayerGanhou;
    private Ranking ranking;

    private Button btnInfo;

    private Button btnUm;
    private Button btnDois;
    private Button btnTres;
    private Button btnApostar;
    private Button btnNovoJogo;
    private Button btnSair;
    private Button btnRanking;
    private Button btnCreditos;
    private TextView lblResultadoMoeda;
    private TextView lblResultadoPontuacao;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    public FirebaseAuth auth;
    public FirebaseAuth.AuthStateListener authStateListener;

    private ChildEventListener childEventListener;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = null;

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //verificar se existe um usuário corrente(logado)
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user ==  null){
                    finish();
                }
            }
        };

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();//pegando a referência do banco inteiro

        //lvRanking = findViewById(R.id.lvRanking);
        //listaRanking = new ArrayList<>();
        //adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaRanking);
        //lvRanking.setAdapter(adapter);

        btnUm = findViewById(R.id.btnPrimeiro);
        btnDois = findViewById(R.id.btnSegundo);
        btnTres = findViewById(R.id.btnTerceiro);
        btnApostar = findViewById(R.id.btnApostar);
        btnNovoJogo = findViewById(R.id.btnNovoJogo);
        btnSair = findViewById(R.id.btnSair);
        btnRanking = findViewById(R.id.btnRanking);
        btnCreditos = findViewById(R.id.btnCreditos);
        lblResultadoMoeda = findViewById(R.id.lblResultadoMoeda);
        lblResultadoPontuacao = findViewById(R.id.lblResultadoPontuacao);

        btnInfo = findViewById(R.id.btnInfo);

        //Intent intent  = new Intent(MainActivity.this,Orientacoes.class);
        //startActivity(intent);

        btnNovoJogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Musica(trilhaInicioJogo);
                moeda = 20;
                pontuacao = 0;
                btnUm.setEnabled(true);
                btnDois.setEnabled(true);
                btnTres.setEnabled(true);
                btnApostar.setEnabled(true);
                lblResultadoMoeda.setText("" + moeda);
                lblResultadoPontuacao.setText("" + pontuacao);
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                StopMusic();
            }
        });

        btnApostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gerar números de 0 a 10 randomicamente
                int valorUm;
                int valorDois;
                int valorTres;

                Random randomUm = new Random();
                Random randomDois = new Random();
                Random randomTres = new Random();

                valorUm = randomUm.nextInt(10);
                valorDois = randomDois.nextInt(10);
                valorTres = randomTres.nextInt(10);

                btnUm.setText(String.valueOf(valorUm));
                btnDois.setText(String.valueOf(valorDois));
                btnTres.setText(String.valueOf(valorTres));

                ValidaJogo(valorUm,valorDois,valorTres);

            }
        });

        // Carregar Ranking
        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RankingActivity.class);
                startActivity(intent);
                StopMusic();
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Orientacoes.class);
                startActivity(intent);
                StopMusic();
            }
        });

        btnCreditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreditosActivity.class);
                startActivity(intent);
                StopMusic();
            }
        });

    }

    /*@Override
    protected void onStop() {
        super.onStop();
        StopMusic();
    }*/

    private void Musica(int trilha){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,trilha);
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

    private void MusicaPerdeu(int trilha){
        if(mediaPlayerPerdeu == null){
            mediaPlayerPerdeu = MediaPlayer.create(this,trilha);
        }
        mediaPlayerPerdeu.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //StopMusic();
            }
        });
        mediaPlayerPerdeu.start();
        mediaPlayerPerdeu.setLooping(false);
    }

    private void MusicaGanhou(int trilhaGanhou){
        if(mediaPlayerGanhou == null){
            mediaPlayerGanhou = MediaPlayer.create(this,trilhaGanhou);
        }
        mediaPlayerGanhou.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //StopMusic();
            }
        });
        mediaPlayerGanhou.start();
        mediaPlayerGanhou.setLooping(false);
    }

    private void StopMusic(){
        //mediaPlayer.release();
        //mediaPlayer = null;
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void GravarNoBanco(String nomePlayer, int pontuacao){
        ranking = new Ranking();
        ranking.setNomePlayer(nomePlayer);
        ranking.setPontuacao(pontuacao);

        reference.child("Ranking").push().setValue(ranking);
    }

    public void ValidaJogo(int valorA, int valorB, int valorC){
        //Se der três 7 o jogador ganha da máquina!
        if((valorA == 7) && (valorB == 7) && (valorC == 7)){
            btnUm.setBackgroundColor(Color.BLUE);
            btnDois.setBackgroundColor(Color.BLUE);
            btnTres.setBackgroundColor(Color.BLUE);
            moeda = moeda + 100;
            pontuacao = pontuacao + 100;
            AlertDialog.Builder alertaGanhou = new AlertDialog.Builder(this);
            alertaGanhou.setTitle("VITÓRIA!");
            StopMusic();
            MusicaGanhou(trilhaGanhou);
            alertaGanhou.setMessage("Parabéns, você estourou a máquina !");
            alertaGanhou.setIcon(android.R.drawable.star_on);
            //alertaGanhou.setPositiveButton("OK",null);
            alertaGanhou.setMessage("Digite seu nome jogador...");
            final  EditText etNome = new EditText(MainActivity.this);
            etNome.setInputType(InputType.TYPE_CLASS_TEXT);
            alertaGanhou.setView(etNome);
            alertaGanhou.setIcon(android.R.drawable.star_on);
            //alertaGanhou.setPositiveButton("OK",null);
            alertaGanhou.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    nomePlayerGanhou = etNome.getText().toString();
                    Toast.makeText(MainActivity.this,"Nome do jogador: " + nomePlayerGanhou,Toast.LENGTH_LONG);
                    GravarNoBanco(nomePlayerGanhou,pontuacao);
                    //GravarNoBanco("SlotMachine",pontuacaoMaquina);
                }
            });
            alertaGanhou.create();
            alertaGanhou.show();
            lblResultadoMoeda.setText(String.valueOf(moeda));
            lblResultadoPontuacao.setText(String.valueOf(pontuacao));
            btnUm.setEnabled(false);
            btnDois.setEnabled(false);
            btnTres.setEnabled(false);
            btnApostar.setEnabled(false);
        }else{
            if (((valorA == 7) && (valorB == 7)) || (valorA == 7) && (valorC == 7) || ((valorB == 7))) {
                //Musica(trilhaMoeda);
                MudaCoresA(valorA,valorB,valorC);
                moeda = moeda + 2;
                pontuacao = pontuacao + 2;
                pontuacaoMaquina = pontuacaoMaquina - 1;
                lblResultadoMoeda.setText(String.valueOf(moeda));
                lblResultadoPontuacao.setText(String.valueOf(pontuacao));
            } else {
                if((valorA == 7) || (valorB == 7) || (valorC == 7)){
                   // Musica(trilhaMoeda);
                    MudaCoresB(valorA,valorB,valorC);
                    moeda = moeda + 1;
                    pontuacao = pontuacao + 1;
                    pontuacaoMaquina = pontuacaoMaquina - 1;
                    lblResultadoMoeda.setText(String.valueOf(moeda));
                    lblResultadoPontuacao.setText(String.valueOf(pontuacao));
                }
                else{
                    if((valorA != 7) && (valorB != 7) && (valorC != 7)){
                        btnUm.setBackgroundColor(Color.RED);
                        btnDois.setBackgroundColor(Color.RED);
                        btnTres.setBackgroundColor(Color.RED);
                        moeda = moeda - 1;
                        //moeda e pontuação da máquina
                        pontuacaoMaquina++;
                        moedaMaquina++;
                        lblResultadoMoeda.setText(String.valueOf(moeda));
                        lblResultadoPontuacao.setText(String.valueOf(pontuacao));
                        if(moeda == 0){
                            AlertDialog.Builder alertaPerdeu = new AlertDialog.Builder(this);
                            alertaPerdeu.setTitle("PERDEU!");
                            StopMusic();
                            MusicaPerdeu(trilhaPerdeu);
                            //alertaPerdeu.setMessage("Infelizmente você perdeu !");
                            alertaPerdeu.setMessage("Digite seu nome jogador...");
                            final  EditText etNome = new EditText(MainActivity.this);
                            etNome.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertaPerdeu.setView(etNome);
                            alertaPerdeu.setIcon(android.R.drawable.ic_delete);
                            //alertaPerdeu.setPositiveButton("OK",null);
                            alertaPerdeu.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    nomePlayerPerdeu = etNome.getText().toString();
                                    Toast.makeText(MainActivity.this,"Nome do jogador: " + nomePlayerPerdeu,Toast.LENGTH_LONG);
                                    GravarNoBanco(nomePlayerPerdeu,pontuacao);
                                    GravarNoBanco("SlotMachine",pontuacaoMaquina);
                                }
                            });

                            alertaPerdeu.create();
                            alertaPerdeu.show();

                            btnUm.setEnabled(false);
                            btnDois.setEnabled(false);
                            btnTres.setEnabled(false);
                            btnApostar.setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    private void MudaCoresA(int valorA,int valorB, int valorC){
        if(valorA == 7 && valorB == 7){
            btnUm.setBackgroundColor(Color.GREEN);
            btnDois.setBackgroundColor(Color.GREEN);
            btnTres.setBackgroundColor(Color.RED);
        }else if (valorA == 7 && valorC == 7){
            btnUm.setBackgroundColor(Color.GREEN);
            btnDois.setBackgroundColor(Color.RED);
            btnTres.setBackgroundColor(Color.GREEN);
        }else if(valorB == 7 && valorC == 7){
            btnUm.setBackgroundColor(Color.RED);
            btnDois.setBackgroundColor(Color.GREEN);
            btnTres.setBackgroundColor(Color.GREEN);
        }
        else if(valorA == 7){
            btnUm.setBackgroundColor(Color.GREEN);
            btnDois.setBackgroundColor(Color.RED);
            btnTres.setBackgroundColor(Color.RED);
        }
        else if(valorB == 7){
            btnDois.setBackgroundColor(Color.GREEN);
            btnUm.setBackgroundColor(Color.RED);
            btnTres.setBackgroundColor(Color.RED);
        }else if(valorC == 7){
            btnTres.setBackgroundColor(Color.GREEN);
            btnUm.setBackgroundColor(Color.RED);
            btnDois.setBackgroundColor(Color.RED);
        }
    }

    private void MudaCoresB(int valorA,int valorB,int valorC){
        if(valorA == 7){
            btnUm.setBackgroundColor(Color.GREEN);
            btnDois.setBackgroundColor(Color.RED);
            btnTres.setBackgroundColor(Color.RED);
        }
        else if(valorB == 7){
            btnDois.setBackgroundColor(Color.GREEN);
            btnUm.setBackgroundColor(Color.RED);
            btnTres.setBackgroundColor(Color.RED);
        }else if(valorC == 7){
            btnTres.setBackgroundColor(Color.GREEN);
            btnUm.setBackgroundColor(Color.RED);
            btnDois.setBackgroundColor(Color.RED);
        }
    }



}