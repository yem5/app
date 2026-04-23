package com.example.myapplication3.Inventario;

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
import com.example.myapplication3.ViewHolder.ViewHolderInventario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<ViewHolderInventario> {

    private Context context;
    private List<Inventario> inventarioList;

    public InventarioAdapter(Context context, List<Inventario> inventarioList) {
        this.context = context;
        this.inventarioList = inventarioList;
    }

    @NonNull
    @Override
    public ViewHolderInventario onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventario, parent, false);
        ViewHolderInventario viewHolder = new ViewHolderInventario(view);
        viewHolder.setOnClickListener(new ViewHolderInventario.clicklistener() {
            @Override
            public void onitemClick(View view, int position) {
                showOpcionesDialog(inventarioList.get(position));
            }

            @Override
            public void onitemLonClick(View view, int position) {
                showOpcionesDialog(inventarioList.get(position));
            }
        });
        return viewHolder;
    }

    private void showOpcionesDialog(Inventario item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_opciones_cliente, null); // Reutilizando el layout de opciones
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btnEditarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, AgregarInventarioActivity.class);
            intent.putExtra("ITEM_INVENTARIO", item);
            context.startActivity(intent);
        });

        view.findViewById(R.id.btnEliminarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            confirmarEliminacion(item);
        });

        dialog.show();
    }

    private void confirmarEliminacion(Inventario item) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de que deseas eliminar " + item.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(item.getUid())
                            .child("Inventario")
                            .child(item.getId());

                    ref.removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                        eliminarPresupuestosVinculados(item.getUid(), item.getId());
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPresupuestosVinculados(String uid, String productId) {
        DatabaseReference presuRef = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(uid)
                .child("Presupuestos");

        presuRef.orderByChild("idProductoRelacionado").equalTo(productId)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInventario holder, int position) {
        Inventario item = inventarioList.get(position);
        holder.setearDatos(
                context,
                item.getNombre(),
                item.getCategoria(),
                item.getStock(),
                item.getPrecio(),
                item.getFoto(),
                item.getCodigo()
        );
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }
}
