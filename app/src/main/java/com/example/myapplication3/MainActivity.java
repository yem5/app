package com.example.myapplication3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText etcorreo, etpassword;
    TextView lblregistro;
    Button btnregistrar;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    String correo="", password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lblregistro=findViewById(R.id.txtregistrar);
        etcorreo=findViewById(R.id.txtusuario);
        etpassword=findViewById(R.id.txtpassword);
        btnregistrar=findViewById(R.id.btningresar);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        lblregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RegistroActivity.class));
                finish();
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
    }

    private void validarDatos(){

        correo=etcorreo.getText().toString().trim();
        password=etpassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toast.makeText(this, "Ingrese correo valido", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Ingrese password", Toast.LENGTH_SHORT).show();

        }else{

            logearUsuario();
        }
    }

    private void logearUsuario(){

        progressDialog.setMessage("Iniciando Sesion");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correo, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressDialog.dismiss();

                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            finish();

                        }else{

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Verifique si el correo o password sean los correctos", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, "Ocurrio un problema al logearse", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}