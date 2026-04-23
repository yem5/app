package com.example.myapplication3.Clientes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.Cliente;
import com.example.myapplication3.R;
import com.example.myapplication3.ViewHolder.ViewHolderCliente;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ViewHolderCliente> {

    private Context context;
    private List<Cliente> clienteList;

    public ClienteAdapter(Context context, List<Cliente> clienteList) {
        this.context = context;
        this.clienteList = clienteList;
    }

    @NonNull
    @Override
    public ViewHolderCliente onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        ViewHolderCliente viewHolderCliente = new ViewHolderCliente(view);
        viewHolderCliente.setMclicklistener(new ViewHolderCliente.clicklistener() {
            @Override
            public void onitemClick(View view, int position) {
                showOpcionesDialog(clienteList.get(position));
            }

            @Override
            public void onitemLonClick(View view, int position) {
                // Opcional: También podemos mostrar el diálogo con click largo
                showOpcionesDialog(clienteList.get(position));
            }
        });
        return viewHolderCliente;
    }

    private void showOpcionesDialog(Cliente cliente) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_opciones_cliente, null);
        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btnEditarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, AgregarClienteActivity.class);
            intent.putExtra("CLIENTE", cliente);
            context.startActivity(intent);
        });

        view.findViewById(R.id.btnEliminarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            confirmarEliminacion(cliente);
        });

        dialog.show();
    }

    private void confirmarEliminacion(Cliente cliente) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Eliminar Cliente")
                .setMessage("¿Estás seguro de que deseas eliminar a " + cliente.getNombres() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    com.google.firebase.database.DatabaseReference ref = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Usuarios")
                            .child(cliente.getUid_cliente())
                            .child("clientes")
                            .child(cliente.getId_cliente());

                    ref.removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCliente holder, int position) {
        Cliente cliente = clienteList.get(position);
        holder.setearDatosCliente(
                context,
                cliente.getId_cliente(),
                cliente.getUid_cliente(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getCorreo(),
                cliente.getDni(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getFoto()
        );
    }

    @Override
    public int getItemCount() {
        return clienteList.size();
    }
}
