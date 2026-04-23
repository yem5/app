package com.example.myapplication3.Presupuesto;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import android.view.View;
import android.widget.LinearLayout;
import com.example.myapplication3.Inventario.Inventario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import com.example.myapplication3.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgregarPresupuestoActivity extends AppCompatActivity {

    EditText etConceptoP, etMontoP, etDescripcionTrans;
    AutoCompleteTextView autoCompleteArea;
    MaterialButtonToggleGroup toggleGroupTipo;
    Button btnGuardarTrans;
    TextView tvHeaderPresupuesto;

    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String idParaEditar = "";
    boolean esEdicion = false;
    String tipoSeleccionado = "Ingreso";

    // Variables para Integracion con Inventario
    LinearLayout layoutLinkInventario;
    AutoCompleteTextView autoCompleteProducto;
    EditText etCantidadVenta;
    List<Inventario> inventarioList = new ArrayList<>();
    Inventario productoSeleccionado = null;

    String[] areas = {"Ventas", "Finanzas", "Marketing", "RRHH", "Sistemas", "Soporte"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_presupuesto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVariables();

        if (getIntent().hasExtra("ITEM_PRESUPUESTO")) {
            Presupuesto p = (Presupuesto) getIntent().getSerializableExtra("ITEM_PRESUPUESTO");
            if (p != null) {
                esEdicion = true;
                idParaEditar = p.getId();
                llenarDatosEdicion(p);
                tvHeaderPresupuesto.setText("Editar Transacción");
                btnGuardarTrans.setText("Actualizar Registro");
            }
        }

        toggleGroupTipo.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnIngreso) {
                    tipoSeleccionado = "Ingreso";
                } else if (checkedId == R.id.btnEgreso) {
                    tipoSeleccionado = "Egreso";
                }
                gestionarVisibilidadInventario(autoCompleteArea.getText().toString(), tipoSeleccionado);
            }
        });

        btnGuardarTrans.setOnClickListener(v -> {
            if (esEdicion) {
                actualizarTransaccion();
            } else {
                guardarTransaccion();
            }
        });
    }

    private void inicializarVariables() {
        etConceptoP = findViewById(R.id.etConceptoP);
        etMontoP = findViewById(R.id.etMontoP);
        etDescripcionTrans = findViewById(R.id.etDescripcionTrans);
        autoCompleteArea = findViewById(R.id.autoCompleteArea);
        toggleGroupTipo = findViewById(R.id.toggleGroupTipo);
        btnGuardarTrans = findViewById(R.id.btnGuardarTrans);
        tvHeaderPresupuesto = findViewById(R.id.tvHeaderPresupuesto);

        // Integracion
        layoutLinkInventario = findViewById(R.id.layoutLinkInventario);
        autoCompleteProducto = findViewById(R.id.autoCompleteProducto);
        etCantidadVenta = findViewById(R.id.etCantidadVenta);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, areas);
        autoCompleteArea.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        cargarInventario();

        autoCompleteArea.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = areas[position];
            gestionarVisibilidadInventario(seleccion, tipoSeleccionado);
        });
    }

    private void gestionarVisibilidadInventario(String area, String tipo) {
        if ("Ventas".equalsIgnoreCase(area) && "Ingreso".equalsIgnoreCase(tipo)) {
            layoutLinkInventario.setVisibility(View.VISIBLE);
        } else {
            layoutLinkInventario.setVisibility(View.GONE);
            productoSeleccionado = null;
            autoCompleteProducto.setText("");
        }
    }

    private void cargarInventario() {
        DatabaseReference refProd = FirebaseDatabase.getInstance().getReference("Productos");
        refProd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventarioList.clear();
                List<String> nombresProductos = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Inventario item = ds.getValue(Inventario.class);
                    if (item != null) {
                        inventarioList.add(item);
                        nombresProductos.add(item.getNombre());
                    }
                }
                ArrayAdapter<String> prodAdapter = new ArrayAdapter<>(AgregarPresupuestoActivity.this, android.R.layout.simple_list_item_1, nombresProductos);
                autoCompleteProducto.setAdapter(prodAdapter);
                autoCompleteProducto.setOnItemClickListener((parent, view, position, id) -> {
                    productoSeleccionado = inventarioList.get(position);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void llenarDatosEdicion(Presupuesto p) {
        etConceptoP.setText(p.getConcepto());
        etMontoP.setText(p.getMonto());
        autoCompleteArea.setText(p.getArea(), false);
        etDescripcionTrans.setText(p.getDescripcion());
        tipoSeleccionado = p.getTipo();

        if ("Ingreso".equalsIgnoreCase(tipoSeleccionado)) {
            toggleGroupTipo.check(R.id.btnIngreso);
        } else {
            toggleGroupTipo.check(R.id.btnEgreso);
        }

        // Restaurar link a inventario si existe
        if (p.getIdProductoRelacionado() != null && !p.getIdProductoRelacionado().isEmpty()) {
            for (Inventario inv : inventarioList) {
                if (inv.getId().equals(p.getIdProductoRelacionado())) {
                    productoSeleccionado = inv;
                    autoCompleteProducto.setText(inv.getNombre(), false);
                    gestionarVisibilidadInventario(p.getArea(), p.getTipo());
                    break;
                }
            }
        }
    }

    private void guardarTransaccion() {
        String uid = firebaseUser.getUid();
        String concepto = etConceptoP.getText().toString().trim();
        String monto = etMontoP.getText().toString().trim();
        String area = autoCompleteArea.getText().toString().trim();
        String desc = etDescripcionTrans.getText().toString().trim();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (!concepto.isEmpty() && !monto.isEmpty()) {
            DatabaseReference ref = BD_Usuarios.child(uid).child("Presupuestos").push();
            String id = ref.getKey();

            String prodId = (productoSeleccionado != null) ? productoSeleccionado.getId() : "";
            Presupuesto p = new Presupuesto(id, uid, tipoSeleccionado, concepto, monto, area, fecha, desc, prodId);

            ref.setValue(p).addOnSuccessListener(unused -> {
                if (productoSeleccionado != null) {
                    descontarStock(productoSeleccionado, etCantidadVenta.getText().toString());
                }
                Toast.makeText(this, "Transacción guardada", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private void descontarStock(Inventario item, String cantidadStr) {
        try {
            int cantidadAVender = Integer.parseInt(cantidadStr);
            int stockActual = Integer.parseInt(item.getStock());
            int nuevoStock = stockActual - cantidadAVender;

            FirebaseDatabase.getInstance().getReference("Productos")
                    .child(item.getId())
                    .child("stock")
                    .setValue(String.valueOf(nuevoStock));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarTransaccion() {
        String uid = firebaseUser.getUid();
        String concepto = etConceptoP.getText().toString().trim();
        String monto = etMontoP.getText().toString().trim();
        String area = autoCompleteArea.getText().toString().trim();
        String desc = etDescripcionTrans.getText().toString().trim();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (!concepto.isEmpty() && !monto.isEmpty()) {
            String prodId = (productoSeleccionado != null) ? productoSeleccionado.getId() : "";
            Presupuesto p = new Presupuesto(idParaEditar, uid, tipoSeleccionado, concepto, monto, area, fecha, desc, prodId);

            BD_Usuarios.child(uid).child("Presupuestos").child(idParaEditar).setValue(p)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
