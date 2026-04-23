package com.example.myapplication3.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;

public class ViewHolderPresupuesto extends RecyclerView.ViewHolder {
    View mView;
    private clicklistener mClickListener;

    public interface clicklistener {
        void onitemClick(View view, int position);
        void onitemLonClick(View view, int position);
    }

    public void setOnClickListener(clicklistener clickListener) {
        this.mClickListener = clickListener;
    }

    public ViewHolderPresupuesto(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onitemClick(v, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onitemLonClick(v, getAdapterPosition());
            }
            return true;
        });
    }

    public void setearDatos(Context context, String concepto, String area, String monto, String tipo, String fecha) {
        TextView tvConcepto = mView.findViewById(R.id.tvConceptoP);
        TextView tvArea = mView.findViewById(R.id.tvAreaP);
        TextView tvMonto = mView.findViewById(R.id.tvMontoP);
        TextView tvFecha = mView.findViewById(R.id.tvFechaP);
        ImageView imgTipo = mView.findViewById(R.id.imgTipoP);

        tvConcepto.setText(concepto);
        tvArea.setText("Área: " + area);
        tvFecha.setText(fecha);

        if ("Ingreso".equalsIgnoreCase(tipo)) {
            tvMonto.setText("+ $ " + monto);
            tvMonto.setTextColor(Color.parseColor("#4CAF50")); // Green
            imgTipo.setImageResource(R.drawable.inventario); // Placeholder, ideally a plus icon
            imgTipo.setColorFilter(Color.parseColor("#4CAF50"));
        } else {
            tvMonto.setText("- $ " + monto);
            tvMonto.setTextColor(Color.parseColor("#F44336")); // Red
            imgTipo.setImageResource(R.drawable.inventario); // Placeholder, ideally a minus icon
            imgTipo.setColorFilter(Color.parseColor("#F44336"));
        }
    }
}
