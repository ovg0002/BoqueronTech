/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.clientes;

/**
 *
 * @author navas
 */
public class Cliente {
   private int idCliente;
    private String nombre;
    private String apellidos;
    private String telefono;
    private int puntos;
    
    private int numCompras; 

    public Cliente() {}

    public Cliente(int idCliente, String nombre, String apellidos, String telefono, int puntos, int numCompras) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.puntos = puntos;
        this.numCompras = numCompras;
    }

    // Getters y Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public int getNumCompras() { return numCompras; }
    public void setNumCompras(int numCompras) { this.numCompras = numCompras; }
    
    // Método auxiliar para nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    } 
}
