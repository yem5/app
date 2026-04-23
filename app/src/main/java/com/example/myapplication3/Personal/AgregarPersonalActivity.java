package com.example.myapplication3.Personal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AgregarPersonalActivity extends AppCompatActivity {

    EditText etNombresPersonal, etApellidosPersonal, etDNIPersonal, etTelefonoPersonal, etCorreoPersonal;
    AutoCompleteTextView autoCompleteAreaPersonal;
    Button btnGuardarPersonal, btnTomarFotoPersonal, btnGaleriaPersonal;
    ImageView imgFotoPersonal;
    TextView tvHeaderPersonal;

    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String fotoBase64 = "";
    String idParaEditar = "";
    boolean esEdicion = false;

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_GALLERY = 2;
    int CAMERA_PERMISSION_CODE = 100;

    String[] areas = {"Ventas", "Finanzas", "Marketing", "RRHH", "Sistemas", "Soporte"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVariables();

        if (getIntent().hasExtra("ITEM_PERSONAL")) {
            Personal p = (Personal) getIntent().getSerializableExtra("ITEM_PERSONAL");
            if (p != null) {
                esEdicion = true;
                idParaEditar = p.getId();
                llenarDatosEdicion(p);
                tvHeaderPersonal.setText("Editar Personal");
                btnGuardarPersonal.setText("Actualizar Staff");
            }
        }

        btnGuardarPersonal.setOnClickListener(v -> {
            if (esEdicion) {
                actualizarPersonal();
            } else {
                guardarPersonal();
            }
        });

        btnTomarFotoPersonal.setOnClickListener(v -> comprobarPermisosCamara());
        btnGaleriaPersonal.setOnClickListener(v -> abrirGaleria());
    }

    private void inicializarVariables() {
        etNombresPersonal = findViewById(R.id.etNombresPersonal);
        etApellidosPersonal = findViewById(R.id.etApellidosPersonal);
        etDNIPersonal = findViewById(R.id.etDNIPersonal);
        etTelefonoPersonal = findViewById(R.id.etTelefonoPersonal);
        etCorreoPersonal = findViewById(R.id.etCorreoPersonal);
        autoCompleteAreaPersonal = findViewById(R.id.autoCompleteAreaPersonal);
        btnGuardarPersonal = findViewById(R.id.btnGuardarPersonal);
        btnTomarFotoPersonal = findViewById(R.id.btnTomarFotoPersonal);
        btnGaleriaPersonal = findViewById(R.id.btnGaleriaPersonal);
        imgFotoPersonal = findViewById(R.id.imgFotoPersonal);
        tvHeaderPersonal = findViewById(R.id.tvHeaderPersonal);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, areas);
        autoCompleteAreaPersonal.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void llenarDatosEdicion(Personal p) {
        etNombresPersonal.setText(p.getNombres());
        etApellidosPersonal.setText(p.getApellidos());
        etDNIPersonal.setText(p.getDni());
        etTelefonoPersonal.setText(p.getTelefono());
        etCorreoPersonal.setText(p.getCorreo());
        autoCompleteAreaPersonal.setText(p.getArea(), false);
        fotoBase64 = p.getFoto();

        if (fotoBase64 != null && !fotoBase64.isEmpty() && !fotoBase64.equals("null")) {
            byte[] decodedBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imgFotoPersonal.setImageBitmap(bitmap);
        }
    }

    private void guardarPersonal() {
        String uid = firebaseUser.getUid();
        String nombres = etNombresPersonal.getText().toString().trim();
        String apellidos = etApellidosPersonal.getText().toString().trim();
        String dni = etDNIPersonal.getText().toString().trim();
        String telefono = etTelefonoPersonal.getText().toString().trim();
        String correo = etCorreoPersonal.getText().toString().trim();
        String area = autoCompleteAreaPersonal.getText().toString().trim();

        if (!nombres.isEmpty() && !apellidos.isEmpty()) {
            DatabaseReference ref = BD_Usuarios.child(uid).child("Personal").push();
            String id = ref.getKey();

            Personal p = new Personal(id, uid, nombres, apellidos, dni, telefono, correo, fotoBase64, area);

            ref.setValue(p).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Personal registrado", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Nombre y Apellido son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPersonal() {
        String uid = firebaseUser.getUid();
        String nombres = etNombresPersonal.getText().toString().trim();
        String apellidos = etApellidosPersonal.getText().toString().trim();
        String dni = etDNIPersonal.getText().toString().trim();
        String telefono = etTelefonoPersonal.getText().toString().trim();
        String correo = etCorreoPersonal.getText().toString().trim();
        String area = autoCompleteAreaPersonal.getText().toString().trim();

        if (!nombres.isEmpty()) {
            Personal p = new Personal(idParaEditar, uid, nombres, apellidos, dni, telefono, correo, fotoBase64, area);

            BD_Usuarios.child(uid).child("Personal").child(idParaEditar).setValue(p)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private void comprobarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgFotoPersonal.setImageBitmap(imageBitmap);
                fotoBase64 = convertirBitmapABase64(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgFotoPersonal.setImageBitmap(selectedImage);
                    fotoBase64 = convertirBitmapABase64(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            }
        }
    }
}
