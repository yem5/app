package com.example.myapplication3.Personal;

import java.io.Serializable;

public class Personal implements Serializable {
    String id;
    String uid;
    String nombres;
    String apellidos;
    String telefono;
    String correo;
    String foto;
    String area;
    String cumpleanos;

    public Personal() {
    }

    public Personal(String id, String uid, String nombres, String apellidos, String telefono, String correo, String foto, String area, String cumpleanos) {
        this.id = id;
        this.uid = uid;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.foto = foto;
        this.area = area;
        this.cumpleanos = cumpleanos;
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

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCumpleanos() {
        return cumpleanos;
    }

    public void setCumpleanos(String cumpleanos) {
        this.cumpleanos = cumpleanos;
    }
}
