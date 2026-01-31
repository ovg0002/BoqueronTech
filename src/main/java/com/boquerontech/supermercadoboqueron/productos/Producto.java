/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author velag
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
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
    
    // CAMBIO: Ahora es un objeto único, no una lista
    @Getter @Setter
    private Categoria categoria; 
    
    @Getter @Setter
    @Builder.Default
    private boolean activo = true;
}
