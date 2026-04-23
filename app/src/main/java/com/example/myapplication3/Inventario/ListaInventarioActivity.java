package com.example.myapplication3.Inventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.widget.Button;
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

public class ListaInventarioActivity extends AppCompatActivity {

    SearchView searchViewInventario;
    RecyclerView recyclerviewInventario;
    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton fabAgregarInventario;
    Button btnVerProductos;

    MovimientosAdapter adapter;
    List<MovimientoInventario> mList = new ArrayList<>();
    List<MovimientoInventario> mListFull = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabAgregarInventario = findViewById(R.id.fabAgregarInventario);
        btnVerProductos = findViewById(R.id.btnVerProductos);
        recyclerviewInventario = findViewById(R.id.recyclerviewInventario);
        recyclerviewInventario.setHasFixedSize(true);
        recyclerviewInventario.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        fabAgregarInventario.setOnClickListener(v -> {
            startActivity(new Intent(ListaInventarioActivity.this, AgregarInventarioActivity.class));
        });

        btnVerProductos.setOnClickListener(v -> {
            startActivity(new Intent(ListaInventarioActivity.this, ProductosInventarioActivity.class));
        });

        searchViewInventario = findViewById(R.id.searchViewInventario);
        searchViewInventario.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarEnHistorial(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarEnHistorial(newText);
                return false;
            }
        });

        listarHistorial();
    }

    private void buscarEnHistorial(String texto) {
        mList.clear();
        if (texto == null || texto.trim().isEmpty()) {
            mList.addAll(mListFull);
        } else {
            String q = texto.toLowerCase();
            for (MovimientoInventario item : mListFull) {
                if (item.getNombreProducto().toLowerCase().contains(q) ||
                    item.getCodigoProducto().toLowerCase().contains(q) ||
                    item.getTipo().toLowerCase().contains(q)) {
                    mList.add(item);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void listarHistorial() {
        if (firebaseUser == null) return;

        BD_Usuarios.child(firebaseUser.getUid()).child("MovimientosInventario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListFull.clear();
                mList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MovimientoInventario item = ds.getValue(MovimientoInventario.class);
                    if (item != null) {
                        mListFull.add(0, item); // Newest first
                        mList.add(0, item);
                    }
                }
                adapter = new MovimientosAdapter(ListaInventarioActivity.this, mList);
                recyclerviewInventario.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaInventarioActivity.this, "Error al cargar historial", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
