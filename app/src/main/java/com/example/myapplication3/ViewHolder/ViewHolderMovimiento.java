package com.example.myapplication3.ViewHolder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.google.android.material.card.MaterialCardView;

public class ViewHolderMovimiento extends RecyclerView.ViewHolder {
    View mView;

    public ViewHolderMovimiento(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setearDatos(Context context, String nombre, String codigo, String tipo, String cantidad, String fecha) {
        TextView tvNombre = mView.findViewById(R.id.tvNombreProdMov);
        TextView tvCodigo = mView.findViewById(R.id.tvCodigoProdMov);
        TextView tvFecha = mView.findViewById(R.id.tvFechaMov);
        TextView tvCant = mView.findViewById(R.id.tvCantidadMov);
        MaterialCardView cardTipo = mView.findViewById(R.id.cardTipoMov);
        ImageView imgTipo = mView.findViewById(R.id.imgTipoMov);

        tvNombre.setText(nombre);
        tvCodigo.setText("CÓDIGO: " + codigo.toUpperCase());
        tvFecha.setText(fecha);

        if (tipo.equalsIgnoreCase("Recibir")) {
            tvCant.setText("+" + cantidad);
            tvCant.setTextColor(Color.parseColor("#00BCD4")); // Cyan
            cardTipo.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E0F7FA")));
            imgTipo.setImageResource(R.drawable.add_icon);
            imgTipo.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));
        } else {
            tvCant.setText("-" + cantidad);
            tvCant.setTextColor(Color.parseColor("#FF5722")); // Orange/Red
            cardTipo.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FBE9E7")));
            imgTipo.setImageResource(R.drawable.borrar_icon);
            imgTipo.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
        }
    }
}
