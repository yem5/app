package com.example.myapplication3;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class MisdatosActivity extends AppCompatActivity {

    ImageView torta, tel, bienv;
    TextView tvcorreoUsuario, tvcodigoUsuario;
    EditText etnombre, etapellido, etfechanacimiento, ettelefono, ettiktok, etdomicilio, etprofesion;
    TextInputEditText etedad;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference Usuarios;

    int REQUEST_IMAGE_CAPTURE = 1;
    int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_misdatos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVariables();
        configurarEventos();
    }

    private void inicializarVariables() {
        tvcorreoUsuario = findViewById(R.id.tvCorreoMD);
        tvcodigoUsuario = findViewById(R.id.tvCodigoMD);

        etnombre       = findViewById(R.id.etNombreMD);
        etapellido     = findViewById(R.id.etApellidoMD);
        etedad         = findViewById(R.id.etEdadMD);          // FIX: ahora el id está en el TextInputEditText
        etfechanacimiento = findViewById(R.id.etFechanacimientoMD);
        ettelefono     = findViewById(R.id.etTelefonoMD);
        ettiktok       = findViewById(R.id.etTiktokMD);
        etdomicilio    = findViewById(R.id.etDomiciloMD);
        etprofesion    = findViewById(R.id.etProfesionMD);     // FIX: campo profesión agregado

        torta = findViewById(R.id.torta);
        tel   = findViewById(R.id.tel);
        bienv = findViewById(R.id.bienv);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void configurarEventos() {
        // FIX: botón Guardar conectado
        findViewById(R.id.btnGuardar).setOnClickListener(v -> actualizarDatos());

        torta.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes  = calendario.get(Calendar.MONTH);
            int dia  = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MisdatosActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etfechanacimiento.setText(fechaSeleccionada);

                        Calendar hoy = Calendar.getInstance();
                        int edad = hoy.get(Calendar.YEAR) - year;
                        if (hoy.get(Calendar.MONTH) < month ||
                                (hoy.get(Calendar.MONTH) == month && hoy.get(Calendar.DAY_OF_MONTH) < dayOfMonth)) {
                            edad--;
                        }
                        etedad.setText(String.valueOf(edad));
                    },
                    anio, mes, dia
            );
            datePickerDialog.show();
        });

        tel.setOnClickListener(v -> {
            ettelefono.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(ettelefono, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        bienv.setOnClickListener(v -> {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private String safeGet(DataSnapshot snap, String key) {
        Object val = snap.child(key).getValue();
        return (val != null) ? val.toString() : "";
    }
    private void lecturaDatos() {
        Usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid      = "" + snapshot.child("uid").getValue();
                    String nombre   = "" + snapshot.child("nombre").getValue();
                    String correo   = "" + snapshot.child("correo").getValue();
                    String apellido = "" + snapshot.child("apellido").getValue();
                    String foto     = "" + snapshot.child("foto").getValue();
                    String fecha    = "" + snapshot.child("fecha_nacimiento").getValue();
                    String edad     = "" + snapshot.child("edad").getValue();
                    String telefono = "" + snapshot.child("telefono").getValue();
                    String domicilio= "" + snapshot.child("domicilio").getValue();
                    String tiktok   = "" + snapshot.child("tiktok").getValue();
                    String profesion= "" + snapshot.child("profesion").getValue();

                    tvcorreoUsuario.setText(correo);
                    tvcodigoUsuario.setText(uid);
                    etnombre.setText(nombre);
                    etapellido.setText(apellido);
                    etfechanacimiento.setText(fecha);
                    etedad.setText(edad);
                    ettelefono.setText(telefono);
                    etdomicilio.setText(domicilio);
                    ettiktok.setText(tiktok);
                    etprofesion.setText(profesion);

                    if (foto != null && !foto.equals("null")) {
                        byte[] decodedBytes = Base64.decode(foto, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        bienv.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(MisdatosActivity.this, "Esperando datos...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MisdatosActivity.this, "Ocurrió un problema: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void comprobarSesion() {
        if (firebaseUser != null) {
            lecturaDatos();
        } else {
            startActivity(new Intent(MisdatosActivity.this, DashboardActivity.class));
        }
    }

    @Override
    protected void onStart() {
        comprobarSesion();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bienv.setImageBitmap(imageBitmap);
            guardarFotoFirebase(imageBitmap);
        }
    }

    private void guardarFotoFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String imagenBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Usuarios.child(firebaseUser.getUid()).child("foto").setValue(imagenBase64)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
                );
    }

    private void actualizarDatos() {
        String nombre_act        = etnombre.getText().toString().trim();
        String apellido_act      = etapellido.getText().toString().trim();
        String fechanacimiento_act = etfechanacimiento.getText().toString().trim();
        String edad_act          = etedad.getText().toString().trim();
        String telefono_act      = ettelefono.getText().toString().trim();
        String domicilio_act     = etdomicilio.getText().toString().trim();
        String tiktok_act        = ettiktok.getText().toString().trim();
        String profesion_act     = etprofesion.getText().toString().trim(); // FIX: profesión incluida

        HashMap<String, Object> datos_actualizar = new HashMap<>();
        datos_actualizar.put("nombre",           nombre_act);
        datos_actualizar.put("apellido",         apellido_act);
        datos_actualizar.put("fecha_nacimiento", fechanacimiento_act);
        datos_actualizar.put("edad",             edad_act);
        datos_actualizar.put("telefono",         telefono_act);
        datos_actualizar.put("domicilio",        domicilio_act);
        datos_actualizar.put("tiktok",           tiktok_act);
        datos_actualizar.put("profesion",        profesion_act); // FIX: profesión guardada

        Usuarios.child(firebaseUser.getUid()).updateChildren(datos_actualizar)
                .addOnSuccessListener(unused ->
                        Toast.makeText(MisdatosActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(MisdatosActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}