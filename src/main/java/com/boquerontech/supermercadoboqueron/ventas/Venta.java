/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.ventas;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author velag
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Venta {
    @Getter @Setter
    private int idVenta;
    
    @Getter @Setter
    private LocalDate fecha;
    
    @Getter @Setter
    private String metodoPago;
    
    @Getter @Setter
    private int idEmpleado; // FK
    
    @Getter @Setter
    private int idCliente;  // FK
}