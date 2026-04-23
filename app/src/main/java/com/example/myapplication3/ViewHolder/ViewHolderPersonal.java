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

public class ViewHolderPersonal extends RecyclerView.ViewHolder {
    View mView;
    private clicklistener mClickListener;

    public interface clicklistener {
        void onitemClick(View view, int position);
        void onitemLonClick(View view, int position);
    }

    public void setOnClickListener(clicklistener clickListener) {
        this.mClickListener = clickListener;
    }

    public ViewHolderPersonal(@NonNull View itemView) {
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

    public void setearDatos(Context context, String nombres, String apellidos, String dni, String telefono, String area, String foto) {
        TextView tvNombre = mView.findViewById(R.id.tvNombrePersonalP);
        TextView tvArea = mView.findViewById(R.id.tvAreaPersonalP);
        TextView tvDNI = mView.findViewById(R.id.tvDNIPersonalP);
        TextView tvTelefono = mView.findViewById(R.id.tvTelefonoPersonalP);
        ImageView imgFoto = mView.findViewById(R.id.imgFotoPersonalP);

        tvNombre.setText(nombres + " " + apellidos);
        tvArea.setText(area);
        tvDNI.setText("DNI: " + dni);
        tvTelefono.setText(telefono);

        if (foto != null && !foto.equals("null") && !foto.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(foto, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imgFoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imgFoto.setImageResource(R.drawable.icono_nombre);
        }
    }
}
