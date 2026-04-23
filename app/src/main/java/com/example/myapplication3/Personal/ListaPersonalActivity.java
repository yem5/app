package com.example.myapplication3.Personal;

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

public class ListaPersonalActivity extends AppCompatActivity {

    SearchView searchViewPersonal;
    RecyclerView recyclerviewPersonal;
    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton fabAgregarPersonal;

    PersonalAdapter adapter;
    List<Personal> personalList = new ArrayList<>();
    List<Personal> personalListFull = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_personal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabAgregarPersonal = findViewById(R.id.fabAgregarPersonal);
        recyclerviewPersonal = findViewById(R.id.recyclerviewPersonal);
        recyclerviewPersonal.setHasFixedSize(true);
        recyclerviewPersonal.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios"); // For other user data if needed


        fabAgregarPersonal.setOnClickListener(v -> {
            startActivity(new Intent(this, AgregarPersonalActivity.class));
        });

        searchViewPersonal = findViewById(R.id.searchViewPersonal);
        searchViewPersonal.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarPersonal(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarPersonal(newText);
                return false;
            }
        });

        listarPersonal();
    }

    private void buscarPersonal(String texto) {
        personalList.clear();
        if (texto == null || texto.trim().isEmpty()) {
            personalList.addAll(personalListFull);
        } else {
            String q = texto.toLowerCase();
            for (Personal p : personalListFull) {
                if (p.getNombres().toLowerCase().contains(q) ||
                    p.getApellidos().toLowerCase().contains(q) ||
                    p.getArea().toLowerCase().contains(q)) {
                    personalList.add(p);
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void listarPersonal() {
        DatabaseReference refStaff = FirebaseDatabase.getInstance().getReference("Staff");

        refStaff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    // SILENT MIGRATION
                    com.example.myapplication3.Utils.DataImportHelper.importPersonalJsonToFirebase(ListaPersonalActivity.this, null);
                    return;
                }
                personalListFull.clear();
                personalList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Personal p = ds.getValue(Personal.class);
                    if (p != null) {
                        personalListFull.add(p);
                        personalList.add(p);
                    }
                }
                adapter = new PersonalAdapter(ListaPersonalActivity.this, personalList);
                recyclerviewPersonal.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaPersonalActivity.this, "Error al cargar personal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
