package com.example.myapplication3.Presupuesto;

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
import com.example.myapplication3.ViewHolder.ViewHolderPresupuesto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PresupuestoAdapter extends RecyclerView.Adapter<ViewHolderPresupuesto> {

    private Context context;
    private List<Presupuesto> presupuestoList;

    public PresupuestoAdapter(Context context, List<Presupuesto> presupuestoList) {
        this.context = context;
        this.presupuestoList = presupuestoList;
    }

    @NonNull
    @Override
    public ViewHolderPresupuesto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presupuesto, parent, false);
        ViewHolderPresupuesto holder = new ViewHolderPresupuesto(view);
        holder.setOnClickListener(new ViewHolderPresupuesto.clicklistener() {
            @Override
            public void onitemClick(View view, int position) {
                showOpcionesDialog(presupuestoList.get(position));
            }

            @Override
            public void onitemLonClick(View view, int position) {
                showOpcionesDialog(presupuestoList.get(position));
            }
        });
        return holder;
    }

    private void showOpcionesDialog(Presupuesto item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_opciones_cliente, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btnEditarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, AgregarPresupuestoActivity.class);
            intent.putExtra("ITEM_PRESUPUESTO", item);
            context.startActivity(intent);
        });

        view.findViewById(R.id.btnEliminarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            confirmarEliminacion(item);
        });

        dialog.show();
    }

    private void confirmarEliminacion(Presupuesto item) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Transacción")
                .setMessage("¿Deseas eliminar el registro de: " + item.getConcepto() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(item.getUid())
                            .child("Presupuestos")
                            .child(item.getId());

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
    public void onBindViewHolder(@NonNull ViewHolderPresupuesto holder, int position) {
        Presupuesto p = presupuestoList.get(position);
        holder.setearDatos(
                context,
                p.getConcepto(),
                p.getArea(),
                p.getMonto(),
                p.getTipo(),
                p.getFecha()
        );
    }

    @Override
    public int getItemCount() {
        return presupuestoList.size();
    }
}
