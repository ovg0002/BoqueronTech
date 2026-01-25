/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.productos;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author velag
 */
public class Producto {
    @Getter
    private int id;
    
    @Getter @Setter
    private String nombre;
    
    @Getter @Setter
    private double precio;
    
    @Getter @Setter
    private int stock;
    
    @Getter @Setter
    private int minStock;
    
    @Getter @Setter
    private List<Categoria> categorias;

    public Producto(int id, String nombre, double precio, int stock, int minStock, List<Categoria> categorias) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.minStock = minStock;
        this.categorias = categorias;
    }
}
