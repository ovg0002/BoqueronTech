/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.promociones;

/**
 *
 * @author navas
 */
import java.time.LocalDate;

public class Promocion {
    private int idPromociones;
    private String nombrePromocion;
    private int unidadesAfectadas;
    private double precioPorUnidad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int idCategoria;

    public Promocion() {
    }

    // Constructor sin ID (para insertar)
    public Promocion(String nombrePromocion, int unidadesAfectadas, double precioPorUnidad, LocalDate fechaInicio, LocalDate fechaFin, int idCategoria) {
        this.nombrePromocion = nombrePromocion;
        this.unidadesAfectadas = unidadesAfectadas;
        this.precioPorUnidad = precioPorUnidad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idCategoria = idCategoria;
    }

    // Getters y Setters
    public int getIdPromociones() { return idPromociones; }
    public void setIdPromociones(int idPromociones) { this.idPromociones = idPromociones; }

    public String getNombrePromocion() { return nombrePromocion; }
    public void setNombrePromocion(String nombrePromocion) { this.nombrePromocion = nombrePromocion; }

    public int getUnidadesAfectadas() { return unidadesAfectadas; }
    public void setUnidadesAfectadas(int unidadesAfectadas) { this.unidadesAfectadas = unidadesAfectadas; }

    public double getPrecioPorUnidad() { return precioPorUnidad; }
    public void setPrecioPorUnidad(double precioPorUnidad) { this.precioPorUnidad = precioPorUnidad; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}
