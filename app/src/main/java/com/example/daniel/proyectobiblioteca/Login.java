package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText txEmail;
    private EditText txPassword;
    private Button btIniciarSesion;
    private Button btRestablecerPass;
    private ImageView imagenUsuario;
    private Button btRedirectRegister;

    private Firebase firebase;

    private TextInputLayout tlLogin;
    private TextInputLayout tlPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("");

        inicializar();
        btIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicioSesion(txEmail.getText().toString(), txPassword.getText().toString());
            }
        });

        btRestablecerPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent restablecer = new Intent(Login.this, RestablecerPassword.class);
                startActivity(restablecer);
            }
        });

        btRedirectRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), Register.class);
                startActivity(register);
            }
        });
    }

    public void inicializar(){
        txEmail=findViewById(R.id.txEmail_actLogin);
        txPassword=findViewById(R.id.txPassword_actLogin);
        btIniciarSesion=findViewById(R.id.bt_login);
        btRestablecerPass=findViewById(R.id.bt_olvidar_contra);
        tlLogin = findViewById(R.id.til_username);
        tlPass = findViewById(R.id.til_password);
        btRedirectRegister = findViewById(R.id.bt_redirect_register);

        //Firebase--
        FirebaseApp.initializeApp(this);
        imagenUsuario=findViewById(R.id.image_actLogin);

        //-----firebase----
        FirebaseApp.initializeApp(this);
        firebase = new Firebase(getApplicationContext());

    }

    public void inicioSesion(String user, String pass){
       // FirebaseApp.initializeApp(this);
       // firebase = new Firebase(getApplicationContext());
    if (!user.isEmpty() && !pass.isEmpty()) {
            firebase.autentificar(user, pass);
            if (firebase.usuarioLogueado() == true){ // Se ha logeado

          Intent i = new Intent(Login.this, Lecturas.class);
         startActivity(i);
      }else{
            controlErrores(user, pass);
      }
    }
    }

    public void controlErrores(String user, String pass){
        if (user.isEmpty()){
            tlLogin.setError(getString(R.string.email_vacio));
        }
        else if (pass.isEmpty()){
            tlPass.setError(getString(R.string.password_vacia));
        }
        else { //si el usuario y la contraseña no estan vacios, el error es de usuario o contraseña incorrectos
            Toast.makeText(this, "Usuario o contraseña incorrectos, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
        }
    }

}

