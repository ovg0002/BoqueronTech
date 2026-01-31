/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes.modelo;

import java.time.LocalDate;

public class CierreCaja {
    private int idCierre;
    private int idEmpleado;
    private LocalDate fecha;
    private String validacion;
    private String incidencia;

    public CierreCaja(int idEmpleado, LocalDate fecha, String validacion, String incidencia) {
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.validacion = validacion;
        this.incidencia = incidencia;
    }

    // Getters (necesarios para el DAO)
    public int getIdEmpleado() { return idEmpleado; }
    public LocalDate getFecha() { return fecha; }
    public String getValidacion() { return validacion; }
    public String getIncidencia() { return incidencia; }
}