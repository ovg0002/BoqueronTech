/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes.modelo;

public class VentaRow {
    private String productos; // Lista de productos en esa venta
    private String estado;    // "Realizado"
    private String fecha;
    private String empleado;
    private String metodoPago;

    public VentaRow(String productos, String estado, String fecha, String empleado, String metodoPago) {
        this.productos = productos;
        this.estado = estado;
        this.fecha = fecha;
        this.empleado = empleado;
        this.metodoPago = metodoPago;
    }

    public String getProductos() { return productos; }
    public String getEstado() { return estado; }
    public String getFecha() { return fecha; }
    public String getEmpleado() { return empleado; }
    public String getMetodoPago() { return metodoPago; }
}