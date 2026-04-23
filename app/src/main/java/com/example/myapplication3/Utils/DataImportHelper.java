package com.example.myapplication3.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication3.Inventario.Inventario;
import com.example.myapplication3.Personal.Personal;
import com.example.myapplication3.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataImportHelper {

    private static final String TAG = "DataImportHelper";

    public interface ImportListener {
        void onProgress(int current, int total);
        void onFinished(int total);
        void onError(String error);
    }

    public static void importJsonDataToFirebase(Context context, ImportListener listener) {
        try {
            // Read JSON from raw resources
            InputStream is = context.getResources().openRawResource(R.raw.stock_actual);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            int total = jsonArray.length();
            DatabaseReference refProductos = FirebaseDatabase.getInstance().getReference("Productos");

            String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            for (int i = 0; i < total; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String id = String.valueOf(obj.optInt("id"));
                String codigo = obj.optString("codigo");
                String nombre = obj.optString("nombre");
                String marca = obj.optString("marca");
                String linea = obj.optString("linea");
                String pventa = String.valueOf(obj.optDouble("pventa"));
                String stock = String.valueOf(obj.optInt("stock"));
                String costo = String.valueOf(obj.optDouble("costo"));

                // Map to Inventario object
                // Using description to store Marca and Costo for now
                String descripcion = "Marca: " + marca + " | Costo: " + costo;

                Inventario item = new Inventario(
                        id,
                        "SISTEMA", // uid
                        nombre,
                        linea, // categoria
                        stock,
                        pventa, // precio
                        descripcion,
                        "", // foto
                        fecha,
                        codigo
                );

                refProductos.child(id).setValue(item);

                if (listener != null) {
                    listener.onProgress(i + 1, total);
                }
            }

            if (listener != null) {
                listener.onFinished(total);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error importing data", e);
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    public static void importPersonalJsonToFirebase(Context context, ImportListener listener) {
        try {
            // Read JSON from raw resources
            InputStream is = context.getResources().openRawResource(R.raw.personal);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            int total = jsonArray.length();
            DatabaseReference refStaff = FirebaseDatabase.getInstance().getReference("Staff");

            for (int i = 0; i < total; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String nombres = obj.optString("NOMBRES");
                String apellidos = obj.optString("APELLIDOS");
                String telefono = obj.optString("TELÉFONO");
                String correo = obj.optString("CORREO");
                String area = obj.optString("ÁREA");
                String cumpleanos = obj.optString("CUMPLEAÑOS");

                DatabaseReference newRef = refStaff.push();
                String id = newRef.getKey();

                Personal p = new Personal(
                        id,
                        "SISTEMA", // uid
                        nombres,
                        apellidos,
                        telefono,
                        correo,
                        "", // foto
                        area,
                        cumpleanos
                );

                newRef.setValue(p);

                if (listener != null) {
                    listener.onProgress(i + 1, total);
                }
            }

            if (listener != null) {
                listener.onFinished(total);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error importing personal data", e);
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }
}
