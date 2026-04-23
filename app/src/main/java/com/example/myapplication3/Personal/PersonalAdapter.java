package com.example.myapplication3.Personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.ViewHolder.ViewHolderPersonal;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PersonalAdapter extends RecyclerView.Adapter<ViewHolderPersonal> {

    private Context context;
    private List<Personal> personalList;

    public PersonalAdapter(Context context, List<Personal> personalList) {
        this.context = context;
        this.personalList = personalList;
    }

    @NonNull
    @Override
    public ViewHolderPersonal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal, parent, false);
        ViewHolderPersonal holder = new ViewHolderPersonal(view);
        holder.setOnClickListener(new ViewHolderPersonal.clicklistener() {
            @Override
            public void onitemClick(View view, int position) {
                showOpcionesDialog(personalList.get(position));
            }

            @Override
            public void onitemLonClick(View view, int position) {
                showOpcionesDialog(personalList.get(position));
            }
        });
        return holder;
    }

    private void showOpcionesDialog(Personal p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_opciones_cliente, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btnEditarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, AgregarPersonalActivity.class);
            intent.putExtra("ITEM_PERSONAL", p);
            context.startActivity(intent);
        });

        view.findViewById(R.id.btnEliminarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            confirmarEliminacion(p);
        });

        dialog.show();
    }

    private void confirmarEliminacion(Personal p) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Personal")
                .setMessage("¿Deseas eliminar a " + p.getNombres() + " " + p.getApellidos() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(p.getUid())
                            .child("Personal")
                            .child(p.getId());

                    ref.removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPersonal holder, int position) {
        Personal p = personalList.get(position);
        holder.setearDatos(
                context,
                p.getNombres(),
                p.getApellidos(),
                p.getDni(),
                p.getTelefono(),
                p.getArea(),
                p.getFoto()
        );
    }

    @Override
    public int getItemCount() {
        return personalList.size();
    }
}
