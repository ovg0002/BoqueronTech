/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.informes;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.informes.modelo.CierreCaja;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class CierreCajaDAO {

    // Método para insertar el cierre (ya lo tenías)
    public static boolean insertarCierre(CierreCaja cierre) {
        String sql = "INSERT INTO CierreCaja (fecha, validacion, incidencia, Empleado_idEmpleado) VALUES (?, ?, ?, ?)";

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(cierre.getFecha()));
            pstmt.setString(2, cierre.getValidacion());
            pstmt.setString(3, cierre.getIncidencia());
            pstmt.setInt(4, cierre.getIdEmpleado());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException es) {
            es.printStackTrace();
            return false;
        }
    }

    // --- ESTE ES EL MÉTODO QUE TE FALTABA ---
    // Recupera el ID del cierre que acabamos de guardar para poder imprimirlo en el PDF
    public static int obtenerUltimoIdCierre(int idEmpleado, java.time.LocalDate fecha) {
        // Seleccionamos el ID más alto (el último creado) para ese empleado y esa fecha
        String sql = "SELECT MAX(idCierre) as ultimoId FROM CierreCaja WHERE Empleado_idEmpleado = ? AND fecha = ?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEmpleado);
            pstmt.setDate(2, Date.valueOf(fecha)); // Convertimos LocalDate a SQL Date
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Si encontramos algo, devolvemos el número
                    // OJO: Si devuelve 0 es que no encontró nada, pero MAX suele devolver null si está vacío.
                    int id = rs.getInt("ultimoId");
                    if (rs.wasNull()) return -1; // Si es null, devolvemos error
                    return id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 si hubo algún fallo
    }
}