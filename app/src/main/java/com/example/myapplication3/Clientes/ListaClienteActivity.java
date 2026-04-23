package com.example.myapplication3.Clientes;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.Cliente;

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

public class ListaClienteActivity extends AppCompatActivity {
    SearchView searchViewClientes;
    RecyclerView recycleviewClientes;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_ususarios = FirebaseDatabase.getInstance().getReference("clientes");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton btnAgregarCliente;
    
    ClienteAdapter clienteAdapter;
    List<Cliente> clientesList = new ArrayList<>();
    List<Cliente> clientesListFull = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAgregarCliente=findViewById(R.id.btnagregarCliente);

        recycleviewClientes=findViewById(R.id.recycleviewClientes);
        recycleviewClientes.setHasFixedSize(true);

        firebaseDatabase=FirebaseDatabase.getInstance();
        BD_ususarios=firebaseDatabase.getReference("Usuarios");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        btnAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ListaClienteActivity.this, AgregarClienteActivity.class);
                startActivity(intent);
            }
        });
        
        listarClientes();
        
        searchViewClientes = findViewById(R.id.searchViewClientes);
        searchViewClientes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarClientes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarClientes(newText);
                return false;
            }
        });

    }
    
    private void buscarClientes(String texto) {
        clientesList.clear();
        if (texto == null || texto.trim().isEmpty()) {
            clientesList.addAll(clientesListFull);
        } else {
            String q = texto.toLowerCase();
            for (Cliente c : clientesListFull) {
                if ((c.getNombres() != null && c.getNombres().toLowerCase().contains(q)) ||
                    (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(q)) ||
                    (c.getDni() != null && c.getDni().toLowerCase().contains(q)) ||
                    (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(q)) ||
                    (c.getDireccion() != null && c.getDireccion().toLowerCase().contains(q))) {
                    clientesList.add(c);
                }
            }
        }
        if (clienteAdapter != null) {
            clienteAdapter.notifyDataSetChanged();
        }
    }

    private void listarClientes() {
        recycleviewClientes.setLayoutManager(new GridLayoutManager(ListaClienteActivity.this, 2));

        BD_ususarios.child(firebaseUser.getUid()).child("clientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clientesListFull.clear();
                clientesList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cliente cliente = ds.getValue(Cliente.class);
                    if (cliente != null) {
                        clientesListFull.add(cliente);
                        clientesList.add(cliente);
                    }
                }
                clienteAdapter = new ClienteAdapter(ListaClienteActivity.this, clientesList);
                recycleviewClientes.setAdapter(clienteAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaClienteActivity.this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
}