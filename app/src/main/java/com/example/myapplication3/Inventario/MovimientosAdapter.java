package com.example.myapplication3.Inventario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.ViewHolder.ViewHolderMovimiento;

import java.util.List;

public class MovimientosAdapter extends RecyclerView.Adapter<ViewHolderMovimiento> {

    private Context context;
    private List<MovimientoInventario> mList;

    public MovimientosAdapter(Context context, List<MovimientoInventario> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolderMovimiento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
        return new ViewHolderMovimiento(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMovimiento holder, int position) {
        MovimientoInventario mov = mList.get(position);
        holder.setearDatos(
                context,
                mov.getNombreProducto(),
                mov.getCodigoProducto(),
                mov.getTipo(),
                mov.getCantidad(),
                mov.getFecha()
        );
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
