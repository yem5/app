package com.example.myapplication3.Presupuesto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

public class ListaPresupuestoActivity extends AppCompatActivity {

    TextView tvBalanceTotal;
    RecyclerView recyclerviewPresupuesto;
    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton fabAgregarPresupuesto;

    PresupuestoAdapter adapter;
    List<Presupuesto> presupuestoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_presupuesto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBalanceTotal = findViewById(R.id.tvBalanceTotal);
        recyclerviewPresupuesto = findViewById(R.id.recyclerviewPresupuesto);
        recyclerviewPresupuesto.setHasFixedSize(true);
        recyclerviewPresupuesto.setLayoutManager(new LinearLayoutManager(this));

        fabAgregarPresupuesto = findViewById(R.id.fabAgregarPresupuesto);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        BD_Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        fabAgregarPresupuesto.setOnClickListener(v -> {
            startActivity(new Intent(this, AgregarPresupuestoActivity.class));
        });

        listarPresupuestos();
    }

    private void listarPresupuestos() {
        if (firebaseUser == null) return;

        BD_Usuarios.child(firebaseUser.getUid()).child("Presupuestos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                presupuestoList.clear();
                double balance = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Presupuesto p = ds.getValue(Presupuesto.class);
                    if (p != null) {
                        presupuestoList.add(p);
                        
                        try {
                            double monto = Double.parseDouble(p.getMonto());
                            if ("Ingreso".equalsIgnoreCase(p.getTipo())) {
                                balance += monto;
                            } else {
                                balance -= monto;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                tvBalanceTotal.setText("$ " + String.format("%.2f", balance));
                if (balance >= 0) {
                    tvBalanceTotal.setTextColor(android.graphics.Color.BLACK);
                } else {
                    tvBalanceTotal.setTextColor(android.graphics.Color.RED);
                }

                adapter = new PresupuestoAdapter(ListaPresupuestoActivity.this, presupuestoList);
                recyclerviewPresupuesto.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaPresupuestoActivity.this, "Error al cargar presupuestos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
