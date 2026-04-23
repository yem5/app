package com.example.myapplication3.ViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication3.R;

public class ViewHolderInventario extends RecyclerView.ViewHolder {
    View mView;
    private clicklistener mClickListener;

    public interface clicklistener {
        void onitemClick(View view, int position);
        void onitemLonClick(View view, int position);
    }

    public void setOnClickListener(clicklistener clickListener) {
        this.mClickListener = clickListener;
    }

    public ViewHolderInventario(@NonNull View itemView) {
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

    public void setearDatos(Context context, String nombre, String categoria, String stock, String precio, String foto, String codigo) {
        TextView tvNombre = mView.findViewById(R.id.tvNombreProductoI);
        TextView tvCategoria = mView.findViewById(R.id.tvCategoriaI);
        TextView tvCodigo = mView.findViewById(R.id.tvCodigoI);
        TextView tvStock = mView.findViewById(R.id.tvStockI);
        TextView tvPrecio = mView.findViewById(R.id.tvPrecioI);
        ImageView imgProducto = mView.findViewById(R.id.imgProductoI);

        tvNombre.setText(nombre);
        tvCategoria.setText(categoria);
        tvCodigo.setText("CÓDIGO: " + (codigo != null ? codigo.toUpperCase() : "N/A"));
        tvStock.setText("Stock: " + stock);
        tvPrecio.setText("$ " + precio);

        // Alerta de stock bajo (Ejemplo: menos de 5)
        try {
            int stockInt = Integer.parseInt(stock);
            if (stockInt < 5) {
                tvStock.setTextColor(android.graphics.Color.RED);
                tvStock.setText("¡BAJO STOCK! (" + stock + ")");
            } else {
                tvStock.setTextColor(context.getResources().getColor(R.color.colorMagenta));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (foto != null && !foto.equals("null") && !foto.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(foto, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imgProducto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imgProducto.setImageResource(R.drawable.inventario);
        }
    }
}
