package com.example.slotmachine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etSenha;
    private Button btnEntrar;
    private Button btnCadastrarse;


    MediaPlayer mediaPlayer;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mediaPlayer = null;
        Musica();
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnCadastrarse = findViewById(R.id.btnCadastrarse);
        btnEntrar = findViewById(R.id.btnEntrar);


        auth = FirebaseAuth.getInstance();

        btnCadastrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cadastro();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entrar();
            }
        });
    }

    private void Musica(){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,R.raw.music);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Musica();
    }

    private void Cadastro(){
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this,"E-mail e Senha são obrigatórios!",Toast.LENGTH_LONG).show();
        }else{
            //Chamar metodo para criar usuário com email e senha
            auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        StopMusic();
                    }
                }
            });
        }

    }

    private void Entrar(){
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this,"E-mail e Senha são obrigatórios!",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,"E-mail ou senha inválidos!",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}