/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.empleado;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author oscar
 */
public class EmpleadoDAO {
    public static boolean isCorrectEmployee(String nombre, String contrasenia) {
        String sql = """
        SELECT COUNT(nombre)
            FROM Empleado
            WHERE nombre = ? AND contrasenia = ?
        """;
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, contrasenia);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt(1) == 1) return true;
                    else return false;
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        
        return false;
    }
}
