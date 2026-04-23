package com.example.myapplication3.Inventario;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.example.myapplication3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgregarInventarioActivity extends AppCompatActivity {

    EditText etNombreP, etCategoriaP, etStockP, etPrecioP, etDescripcionP, etStockActual, etCantidadMovP, etCodigoP;
    Button btnGuardarP, btnTomarFotoP, btnGaleriaP;
    ImageView imgFotoProducto;
    TextView tvHeaderInventario, tvInfoStockCodigo;
    LinearLayout layoutStockNuevo, layoutStockMovimiento;
    MaterialButtonToggleGroup toggleMovimiento;

    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String fotoBase64 = "";
    String idItemParaEditar = "";
    boolean esEdicion = false;

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_GALLERY = 2;
    int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVariables();

        if (getIntent().hasExtra("ITEM_INVENTARIO")) {
            Inventario item = (Inventario) getIntent().getSerializableExtra("ITEM_INVENTARIO");
            if (item != null) {
                esEdicion = true;
                idItemParaEditar = item.getId();
                llenarDatosEdicion(item);
                tvHeaderInventario.setText("Editar Producto IT");
                btnGuardarP.setText("Actualizar Inventario");

                // Swapping layouts for Edit mode
                layoutStockNuevo.setVisibility(View.GONE);
                layoutStockMovimiento.setVisibility(View.VISIBLE);
            }
        }

        btnGuardarP.setOnClickListener(v -> {
            if (esEdicion) {
                actualizarProducto();
            } else {
                agregarProducto();
            }
        });

        btnTomarFotoP.setOnClickListener(v -> comprobarPermisosCamara());
        btnGaleriaP.setOnClickListener(v -> abrirGaleria());

        configurarBusquedaCodigo();
    }

    private void configurarBusquedaCodigo() {
        etCodigoP.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                String code = s.toString().trim();
                if (code.length() >= 3) {
                    buscarProductoPorCodigo(code);
                } else {
                    resetearFormulario();
                }
            }
        });
    }

    private void buscarProductoPorCodigo(String code) {
        String uid = firebaseUser.getUid();
        BD_Usuarios.child(uid).child("Inventario")
                .orderByChild("codigo").equalTo(code)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()) {
                                Inventario item = ds.getValue(Inventario.class);
                                if (item != null) {
                                    aplicarModoExistente(item);
                                }
                            }
                        } else {
                            resetearFormulario();
                        }
                    }
                    @Override public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                });
    }

    private void aplicarModoExistente(Inventario item) {
        esEdicion = true;
        idItemParaEditar = item.getId();
        tvInfoStockCodigo.setText("STOCK ACTUAL: " + item.getStock());
        tvInfoStockCodigo.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));

        // Fill fields
        etNombreP.setText(item.getNombre());
        etCategoriaP.setText(item.getCategoria());
        etPrecioP.setText(item.getPrecio());
        etStockActual.setText(item.getStock());

        layoutStockNuevo.setVisibility(View.GONE);
        layoutStockMovimiento.setVisibility(View.VISIBLE);
        btnGuardarP.setText("Registrar Movimiento");
    }

    private void resetearFormulario() {
        if (getIntent().hasExtra("ITEM_INVENTARIO")) return; // Don't reset if we came for editing

        esEdicion = false;
        idItemParaEditar = "";
        tvInfoStockCodigo.setText("PRODUCTO NUEVO");
        tvInfoStockCodigo.setTextColor(ContextCompat.getColor(this, R.color.colorMagenta));

        layoutStockNuevo.setVisibility(View.VISIBLE);
        layoutStockMovimiento.setVisibility(View.GONE);
        btnGuardarP.setText("Guardar Producto");
    }

    private void inicializarVariables() {
        etNombreP = findViewById(R.id.etNombreP);
        etCategoriaP = findViewById(R.id.etCategoriaP);
        etStockP = findViewById(R.id.etStockP);
        etPrecioP = findViewById(R.id.etPrecioP);
        etDescripcionP = findViewById(R.id.etDescripcionP);
        btnGuardarP = findViewById(R.id.btnGuardarP);
        btnTomarFotoP = findViewById(R.id.btnTomarFotoP);
        btnGaleriaP = findViewById(R.id.btnGaleriaP);
        imgFotoProducto = findViewById(R.id.imgFotoProducto);
        tvHeaderInventario = findViewById(R.id.tvHeaderInventario);

        // Movement components
        layoutStockNuevo = findViewById(R.id.layoutStockNuevo);
        layoutStockMovimiento = findViewById(R.id.layoutStockMovimiento);
        etStockActual = findViewById(R.id.etStockActual);
        etCantidadMovP = findViewById(R.id.etCantidadMovP);
        toggleMovimiento = findViewById(R.id.toggleMovimiento);
        etCodigoP = findViewById(R.id.etCodigoP);
        tvInfoStockCodigo = findViewById(R.id.tvInfoStockCodigo);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void llenarDatosEdicion(Inventario item) {
        etNombreP.setText(item.getNombre());
        etCodigoP.setText(item.getCodigo());
        etCategoriaP.setText(item.getCategoria());
        etStockActual.setText(item.getStock()); // Fill the reference field
        etPrecioP.setText(item.getPrecio());
        etDescripcionP.setText(item.getDescripcion());
        fotoBase64 = item.getFoto();

        if (fotoBase64 != null && !fotoBase64.isEmpty() && !fotoBase64.equals("null")) {
            byte[] decodedBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imgFotoProducto.setImageBitmap(bitmap);
        }
    }

    private void agregarProducto() {
        String uid = firebaseUser.getUid();
        String nombre = etNombreP.getText().toString().trim();
        String codigo = etCodigoP.getText().toString().trim();
        String categoria = etCategoriaP.getText().toString().trim();
        String stock = etStockP.getText().toString().trim();
        String precio = etPrecioP.getText().toString().trim();
        String descripcion = etDescripcionP.getText().toString().trim();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (!nombre.isEmpty() && !codigo.isEmpty()) {
            DatabaseReference ref = BD_Usuarios.child(uid).child("Inventario").push();
            String id = ref.getKey();

            Inventario item = new Inventario(id, uid, nombre, categoria, stock, precio, descripcion, fotoBase64, fecha, codigo);

            ref.setValue(item).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Producto agregado al inventario", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarProducto() {
        String uid = firebaseUser.getUid();
        String nombre = etNombreP.getText().toString().trim();
        String categoria = etCategoriaP.getText().toString().trim();
        String precio = etPrecioP.getText().toString().trim();
        String descripcion = etDescripcionP.getText().toString().trim();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        String stockActualStr = etStockActual.getText().toString().trim();
        String cantidadMovStr = etCantidadMovP.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        int stockActual = Integer.parseInt(stockActualStr);
        int finalStock = stockActual;
        String tipoMov = "Recibir";

        if (!cantidadMovStr.isEmpty()) {
            int cantidadMov = Integer.parseInt(cantidadMovStr);
            if (toggleMovimiento.getCheckedButtonId() == R.id.btnRecibido) {
                finalStock += cantidadMov;
                tipoMov = "Recibir";
            } else {
                if (cantidadMov > stockActual) {
                    Toast.makeText(this, "No hay suficiente stock", Toast.LENGTH_SHORT).show();
                    return;
                }
                finalStock -= cantidadMov;
                tipoMov = "Despachar";
            }
            registrarMovimiento(nombre, etCodigoP.getText().toString(), tipoMov, cantidadMovStr);
        }

        Inventario item = new Inventario(idItemParaEditar, uid, nombre, categoria, String.valueOf(finalStock), precio, descripcion, fotoBase64, fecha, etCodigoP.getText().toString());

        final int resultStock = finalStock;
        BD_Usuarios.child(uid).child("Inventario").child(idItemParaEditar).setValue(item)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Inventario actualizado (" + resultStock + " total)", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void registrarMovimiento(String nombre, String codigo, String tipo, String cantidad) {
        String uid = firebaseUser.getUid();
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        DatabaseReference refMov = BD_Usuarios.child(uid).child("MovimientosInventario").push();
        String idMov = refMov.getKey();

        MovimientoInventario mov = new MovimientoInventario(idMov, uid, idItemParaEditar, codigo, nombre, tipo, cantidad, fecha);
        refMov.setValue(mov);
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
                imgFotoProducto.setImageBitmap(imageBitmap);
                fotoBase64 = convertirBitmapABase64(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgFotoProducto.setImageBitmap(selectedImage);
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
