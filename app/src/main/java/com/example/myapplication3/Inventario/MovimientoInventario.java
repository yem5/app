package com.example.myapplication3.Inventario;

import java.io.Serializable;

public class MovimientoInventario implements Serializable {
    String id;
    String uid;
    String idProducto;
    String codigoProducto;
    String nombreProducto;
    String tipo; // Recibir / Despachar
    String cantidad;
    String fecha;

    public MovimientoInventario() {
    }

    public MovimientoInventario(String id, String uid, String idProducto, String codigoProducto, String nombreProducto, String tipo, String cantidad, String fecha) {
        this.id = id;
        this.uid = uid;
        this.idProducto = idProducto;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCantidad() { return cantidad; }
    public void setCantidad(String cantidad) { this.cantidad = cantidad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
