package com.example.myapplication3.Clientes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
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

import com.example.myapplication3.Cliente;
import com.example.myapplication3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AgregarClienteActivity extends AppCompatActivity {
    TextView tvusuariocli;
    ImageView imgFotoCliente;
    EditText nombrescli, apellidoscli, correocli, dnicli, telefonocli, direccioncli;
    Button btnguardarcliente, btnTomarFoto, btnGaleria;
    DatabaseReference BD_clientes;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    
    String idClienteParaEditar = "";
    boolean esEdicion = false;
    String fotoBase64 = "";
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_GALLERY = 2;
    int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarlizarVariable();

        // Comprobar si es edición
        if (getIntent().hasExtra("CLIENTE")) {
            Cliente cliente = (Cliente) getIntent().getSerializableExtra("CLIENTE");
            if (cliente != null) {
                esEdicion = true;
                idClienteParaEditar = cliente.getId_cliente();
                llenarDatosEdicion(cliente);
                btnguardarcliente.setText("Actualizar Cliente");
                TextView title = findViewById(R.id.textView2); // Asumiendo que este es el título
                if (title != null) title.setText("Editar Cliente");
            }
        }

        btnguardarcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (esEdicion) {
                    actualizarCliente();
                } else {
                    agregarCliente();
                }
            }
        });
    }

    private void llenarDatosEdicion(Cliente cliente) {
        nombrescli.setText(cliente.getNombres());
        apellidoscli.setText(cliente.getApellidos());
        correocli.setText(cliente.getCorreo());
        dnicli.setText(cliente.getDni());
        telefonocli.setText(cliente.getTelefono());
        direccioncli.setText(cliente.getDireccion());
        fotoBase64 = cliente.getFoto();

        if (fotoBase64 != null && !fotoBase64.isEmpty() && !fotoBase64.equals("null")) {
            byte[] decodedBytes = android.util.Base64.decode(fotoBase64, android.util.Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imgFotoCliente.setImageBitmap(bitmap);
        }
    }

    private void actualizarCliente() {
        String uid = firebaseUser.getUid();
        String nombres = nombrescli.getText().toString().trim();
        String apellidos = apellidoscli.getText().toString().trim();
        String correo = correocli.getText().toString().trim();
        String dni = dnicli.getText().toString().trim();
        String telefono = telefonocli.getText().toString().trim();
        String direccion = direccioncli.getText().toString().trim();

        if (!nombres.equals("")) {
            Cliente cliente = new Cliente(
                    idClienteParaEditar,
                    uid,
                    nombres,
                    apellidos,
                    correo,
                    telefono,
                    dni,
                    direccion,
                    fotoBase64
            );

            BD_clientes.child("Usuarios").child(uid).child("clientes").child(idClienteParaEditar).setValue(cliente)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void agregarCliente() {
        String uid = firebaseUser.getUid();
        String nombres=nombrescli.getText().toString().trim();
        String apellidos=apellidoscli.getText().toString().trim();
        String correo=correocli.getText().toString().trim();
        String dni=dnicli.getText().toString().trim();
        String telefono=telefonocli.getText().toString().trim();
        String direccion=direccioncli.getText().toString().trim();

        if(!nombres.equals("")){
            String id_cliente=BD_clientes.child("Usuarios").child(uid).child("clientes").push().getKey();
            Cliente cliente=new Cliente(
                    id_cliente,
                    uid,
                    nombres,
                    apellidos,
                    correo,
                    telefono,
                    dni,
                    direccion,
                    fotoBase64
            );
            assert  id_cliente!=null;
            BD_clientes.child("Usuarios").child(uid).child("clientes").child(id_cliente).setValue(cliente)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Cliente Agregado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar el cliente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }else{
            Toast.makeText(this, "Complete al menos el Nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void comprobarPermisoCamara() {
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
                imgFotoCliente.setImageBitmap(imageBitmap);
                convertirFotoABase64(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgFotoCliente.setImageBitmap(selectedImage);
                    convertirFotoABase64(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void convertirFotoABase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        fotoBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
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


    private void iniciarlizarVariable() {
        tvusuariocli=findViewById(R.id.tvuidclientecli);
        imgFotoCliente=findViewById(R.id.imgFotoCliente);
        nombrescli=findViewById(R.id.etnombrescli);
        apellidoscli=findViewById(R.id.etapellidoscli);
        correocli=findViewById(R.id.etcorreocli);
        dnicli=findViewById(R.id.etdnicli);
        telefonocli=findViewById(R.id.ettelefonocli);
        direccioncli=findViewById(R.id.etdireccioncli);
        btnguardarcliente=findViewById(R.id.btnguardarcli);
        btnTomarFoto=findViewById(R.id.btnTomarFoto);
        btnGaleria=findViewById(R.id.btnGaleria);

        BD_clientes= FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            tvusuariocli.setText(firebaseUser.getUid());
        }

        btnTomarFoto.setOnClickListener(v -> comprobarPermisoCamara());
        btnGaleria.setOnClickListener(v -> abrirGaleria());
    }
}