/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes.modelo;

/**
 *
 * @author navas
 */
public class ProductoRow {
   private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private String proveedor;
    private int numVentas;

    public ProductoRow(String nombre, String categoria, double precio, int stock, String proveedor, int numVentas) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.proveedor = proveedor;
        this.numVentas = numVentas;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getProveedor() { return proveedor; }
    public int getNumVentas() { return numVentas; }
 
}
