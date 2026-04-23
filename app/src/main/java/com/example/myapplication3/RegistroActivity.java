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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

 EditText etnombre,etapellido,etcorreo,etpassword,etconfipassword;

 TextView lbllogin;

 Button btnregistrarusuario;

 FirebaseAuth firebaseAuth;

 ProgressDialog progressDialog;

 String nombre="",apellido="",correo="",password="",confipassword="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etnombre=findViewById(R.id.etnombrer);
        etapellido=findViewById(R.id.etapellidor);
        etcorreo=findViewById(R.id.etcorreor);
        etpassword=findViewById(R.id.etpasswordr);
        etconfipassword=findViewById(R.id.etconfipasswordr);
        lbllogin=findViewById(R.id.lbl_loginr);
        btnregistrarusuario=findViewById(R.id.btnregistarusuario);
        //generando las instancias para la base de datos
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(RegistroActivity.this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        lbllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this,MainActivity.class));
                finish();
            }
        });

        btnregistrarusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validarDatos();
            }

        });
    }

    private void validarDatos(){
        nombre=etnombre.getText().toString().trim();
        apellido=etapellido.getText().toString().trim();
        correo=etcorreo.getText().toString().trim();
        password=etpassword.getText().toString().trim();
        confipassword=etconfipassword.getText().toString().trim();

        if(TextUtils.isEmpty(nombre)) {
            Toast.makeText(this,"El campo nombre esta vacio", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(apellido)){
            Toast.makeText(this,"El campo apellido esta vacio", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toast.makeText(this,"Ingrese un correo valido", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)||password.length()<8){
            Toast.makeText(this,"Ingrese su password como minimo 8 caracteres", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confipassword)||confipassword.length()<8){
            Toast.makeText(this,"Repita su password para confirmar", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confipassword)){
            Toast.makeText(this,"Las passwords no coinciden", Toast.LENGTH_SHORT).show();
        }else{
            registrar();

        }
    }

    private void registrar() {
        progressDialog.setMessage("Registrando al usuario");
        progressDialog.dismiss();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        guardarUsuario();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroActivity.this, "Ocurrio un problema, revisa los campos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarUsuario(){
        progressDialog.setMessage("Guardando Informacion");
        progressDialog.show();

        String uid=firebaseAuth.getUid();
        HashMap<String,String>datousuario=new HashMap<>();
        datousuario.put("uid",uid);
        datousuario.put("nombre",nombre);
        datousuario.put("apellido",apellido);
        datousuario.put("correo",correo);
        datousuario.put("password",password);
        datousuario.put("fecha_nacimiento","");
        datousuario.put("edad","");
        datousuario.put("telefono","");
        datousuario.put("domicilio","");
        datousuario.put("tiktok","");

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid).setValue(datousuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegistroActivity.this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroActivity.this, DashboardActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, "Ocurrio un problema al guardar"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}

















