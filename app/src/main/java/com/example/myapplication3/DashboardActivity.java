package com.example.myapplication3;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication3.Clientes.ListaClienteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;

public class DashboardActivity extends AppCompatActivity {

    Button btnCerrarsesion, btnDesarrollador;
    TextView txtUID, txtNomApelli;

    MaterialCardView cvEmpresa, cvInventario, cvConfiguracion, cvPresupuesto, cvPersonal, cvMisDatos;

    ImageView imgPersonal;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference Usuarios;

    Dialog dialogDev;

    int REQUEST_IMAGE_CAPTURE = 1;
    int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCerrarsesion = findViewById(R.id.btnCerrarsesion);
        btnDesarrollador = findViewById(R.id.btnDesarrollador);

        cvEmpresa       = findViewById(R.id.cvEmpresa);
        cvInventario    = findViewById(R.id.cvInventario);
        cvConfiguracion = findViewById(R.id.cvConfiguracion);
        cvPresupuesto   = findViewById(R.id.cvPresupuesto);
        cvPersonal      = findViewById(R.id.cvPersonal);
        cvMisDatos      = findViewById(R.id.cvMisDatos);

        imgPersonal = findViewById(R.id.imgPersonal);

        txtUID = findViewById(R.id.txtUID);
        txtNomApelli = findViewById(R.id.txtNomApelli);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        btnCerrarsesion.setOnClickListener(v -> cerrarSession());

        cvMisDatos.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MisdatosActivity.class))
        );

        cvConfiguracion.setOnClickListener(v ->
                startActivity(new Intent(this, ConfiguracionActivity.class))
        );

        cvEmpresa.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaClienteActivity.class);
            startActivity(intent);
        });

        cvPersonal.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.myapplication3.Personal.ListaPersonalActivity.class))
        );

        cvPresupuesto.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.myapplication3.Presupuesto.ListaPresupuestoActivity.class))
        );

        cvInventario.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.myapplication3.Inventario.ProductosInventarioActivity.class))
        );

        /*
        CLICK EN FOTO → PEDIR PERMISO O ABRIR CAMARA
         */

        imgPersonal.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE
                );

            } else {

                abrirCamara();

            }

        });

        btnDesarrollador.setOnClickListener(v -> {
            // Este botón ahora solo abre los datos del desarrollador, sin migración visible
            Toast.makeText(this, "Modo Desarrollador", Toast.LENGTH_SHORT).show();
        });

    }

    private void abrirCamara() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    private void cerrarSession() {

        firebaseAuth.signOut();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        Toast.makeText(this, "Cerraste Session Exitosamente", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onStart() {

        super.onStart();
        comprobarSession();

    }

    private void comprobarSession() {

        if (firebaseUser != null) {

            cargarDatos();

        } else {

            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();

        }

    }

    private void cargarDatos() {
        String uid = firebaseAuth.getUid();

        Usuarios.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(DashboardActivity.this,
                        "Snapshot existe: " + snapshot.exists() +
                                " | Valor: " + snapshot.getValue(),
                        Toast.LENGTH_LONG).show();
                if (snapshot.exists()) {

                    String uid = "" + snapshot.child("uid").getValue();
                    String nombre = "" + snapshot.child("nombre").getValue();
                    String apellido = "" + snapshot.child("apellido").getValue();
                    String foto = "" + snapshot.child("foto").getValue();

                    txtNomApelli.setText(nombre + " " + apellido);
                    txtUID.setText(uid);


                    if (foto != null && !foto.equals("null")) {

                        byte[] decodedBytes = Base64.decode(foto, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        imgPersonal.setImageBitmap(bitmap);

                    }

                } else {
                    Toast.makeText(DashboardActivity.this, "Usuario no encontrado en DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Error Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imgPersonal.setImageBitmap(imageBitmap);

            guardarFotoFirebase(imageBitmap);

        }

    }

    private void guardarFotoFirebase(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] imageBytes = baos.toByteArray();

        String imagenBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Usuarios.child(firebaseAuth.getUid()).child("foto").setValue(imagenBase64)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
                );

    }

}