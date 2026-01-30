package com.boquerontech.supermercadoboqueron.clientes;

import java.time.LocalDate;

/**
 * Clase que representa el modelo de datos para un Cliente.
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String dni;
    private int puntosCliente;
    private String codigoCliente;

    private int totalCompras;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String apellidos, LocalDate fechaNacimiento, String telefono, String dni, int puntosCliente, String codigoCliente) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.dni = dni;
        this.puntosCliente = puntosCliente;
        this.codigoCliente = codigoCliente;
    }
    
    

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getPuntosCliente() {
        return puntosCliente;
    }

    public void setPuntosCliente(int puntosCliente) {
        this.puntosCliente = puntosCliente;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }
  
public int getTotalCompras() {
    return totalCompras;
}

public void setTotalCompras(int totalCompras) {
    this.totalCompras = totalCompras;
}


}
