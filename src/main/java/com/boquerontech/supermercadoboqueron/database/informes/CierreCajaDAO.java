/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.informes;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.informes.modelo.CierreCaja;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class CierreCajaDAO {

    public static boolean insertarCierre(CierreCaja cierre) {
        // Asegúrate de que el nombre de la tabla y columnas coinciden con tu BD
        String sql = "INSERT INTO CierreCaja (fecha, validacion, incidencia, Empleado_idEmpleado) VALUES (?, ?, ?, ?)";

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(cierre.getFecha()));
            pstmt.setString(2, cierre.getValidacion());
            pstmt.setString(3, cierre.getIncidencia());
            pstmt.setInt(4, cierre.getIdEmpleado());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
