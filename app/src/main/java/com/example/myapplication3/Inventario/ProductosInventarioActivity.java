package com.example.myapplication3.Inventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductosInventarioActivity extends AppCompatActivity {

    SearchView searchViewProductos;
    RecyclerView recyclerviewProductos;
    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton fabAgregarProducto;

    InventarioAdapter adapter;
    List<Inventario> inventarioList = new ArrayList<>();
    List<Inventario> inventarioListFull = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabAgregarProducto = findViewById(R.id.fabAgregarProducto);
        recyclerviewProductos = findViewById(R.id.recyclerviewProductos);
        recyclerviewProductos.setHasFixedSize(true);
        recyclerviewProductos.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        fabAgregarProducto.setOnClickListener(v -> {
            startActivity(new Intent(ProductosInventarioActivity.this, AgregarInventarioActivity.class));
        });

        searchViewProductos = findViewById(R.id.searchViewProductos);
        searchViewProductos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarProductos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarProductos(newText);
                return false;
            }
        });

        listarProductos();
    }

    private void buscarProductos(String texto) {
        inventarioList.clear();
        if (texto == null || texto.trim().isEmpty()) {
            inventarioList.addAll(inventarioListFull);
        } else {
            String q = texto.toLowerCase();
            for (Inventario item : inventarioListFull) {
                if (item.getNombre().toLowerCase().contains(q) ||
                    item.getCodigo() != null && item.getCodigo().toLowerCase().contains(q) ||
                    item.getCategoria().toLowerCase().contains(q)) {
                    inventarioList.add(item);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void listarProductos() {
        DatabaseReference BD_Productos = FirebaseDatabase.getInstance().getReference("Productos");

        BD_Productos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    // SILENT MIGRATION
                    com.example.myapplication3.Utils.DataImportHelper.importJsonDataToFirebase(ProductosInventarioActivity.this, null);
                    return;
                }
                inventarioListFull.clear();
                inventarioList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Inventario item = ds.getValue(Inventario.class);
                    if (item != null) {
                        inventarioListFull.add(item);
                        inventarioList.add(item);
                    }
                }
                adapter = new InventarioAdapter(ProductosInventarioActivity.this, inventarioList);
                recyclerviewProductos.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductosInventarioActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
