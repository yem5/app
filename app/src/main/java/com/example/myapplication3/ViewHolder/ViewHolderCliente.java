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

public class ViewHolderCliente extends RecyclerView.ViewHolder {
    View mview;
    private ViewHolderCliente.clicklistener mclicklistener;
    public interface clicklistener{
        void onitemClick(View view, int position);
        void onitemLonClick(View view, int position);
    }

    public void setMclicklistener(clicklistener mclicklistener) {
        this.mclicklistener = mclicklistener;
    }
    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview=itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mclicklistener.onitemClick(view, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mclicklistener.onitemLonClick(view, getAdapterPosition());
                return false;
            }
        });
    }
    public void setearDatosCliente(Context context, String id_cliente, String uid_cliente, String nombres,
                                   String Apellidos, String correo, String dni, String telefono, String direccion, String foto) {
        TextView tvidclienteI, tvuidclienteI, tvnombreI, tvapellidoI, tvcorreoclienteI, tvdniclienteI, tvtelefonoI, tvdireccionclienteI;
        ImageView imgClienteI;

        tvidclienteI=mview.findViewById(R.id.tvidclienteI);
        tvuidclienteI=mview.findViewById(R.id.tvuidclienteI);
        tvnombreI=mview.findViewById(R.id.tvnombreI);
        tvapellidoI=mview.findViewById(R.id.tvapellidosI);
        tvcorreoclienteI=mview.findViewById(R.id.tvcorreoI);
        tvdniclienteI=mview.findViewById(R.id.tvdniI);
        tvtelefonoI=mview.findViewById(R.id.tvtelefonoI);
        tvdireccionclienteI=mview.findViewById(R.id.tvdireccionI);
        imgClienteI=mview.findViewById(R.id.imgClienteI);

        tvidclienteI.setText(id_cliente);
        tvuidclienteI.setText(uid_cliente);
        tvnombreI.setText(nombres);
        tvapellidoI.setText(Apellidos);
        tvcorreoclienteI.setText(correo);
        tvdniclienteI.setText(dni);
        tvtelefonoI.setText(telefono);
        tvdireccionclienteI.setText(direccion);

        if (foto != null && !foto.equals("null") && !foto.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(foto, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imgClienteI.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



















