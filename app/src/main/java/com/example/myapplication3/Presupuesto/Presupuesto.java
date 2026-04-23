package com.example.myapplication3.Presupuesto;

import java.io.Serializable;

public class Presupuesto implements Serializable {
    String id;
    String uid;
    String tipo; // Ingreso o Egreso
    String concepto;
    String monto;
    String area;
    String fecha;
    String descripcion;
    String idProductoRelacionado; // Link to Inventory Item

    public Presupuesto() {
    }

    public Presupuesto(String id, String uid, String tipo, String concepto, String monto, String area, String fecha, String descripcion, String idProductoRelacionado) {
        this.id = id;
        this.uid = uid;
        this.tipo = tipo;
        this.concepto = concepto;
        this.monto = monto;
        this.area = area;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.idProductoRelacionado = idProductoRelacionado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdProductoRelacionado() {
        return idProductoRelacionado;
    }

    public void setIdProductoRelacionado(String idProductoRelacionado) {
        this.idProductoRelacionado = idProductoRelacionado;
    }
}
