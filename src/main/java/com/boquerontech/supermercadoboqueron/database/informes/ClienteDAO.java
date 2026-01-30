/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.informes;

/**
 *
 * @author navas
 */
import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.clientes.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Obtiene clientes con filtros y ordenación.
     * @param busqueda Texto a buscar en nombre/apellidos/id (puede ser vacío).
     * @param orden Criterio de ordenación: 0 (A-Z), 1 (Num Compras DESC), 2 (Puntos DESC).
     */
    public static List<Cliente> listarClientes(String busqueda, int orden) {
        List<Cliente> lista = new ArrayList<>();
        
        // SQL Base: Cliente + Conteo de Ventas
        String sql = """
            SELECT c.idCliente, c.nombre, c.apellidos, c.telefono, c.puntosCliente, 
                   COUNT(v.idVenta) as totalCompras
            FROM Cliente c
            LEFT JOIN Venta v ON c.idCliente = v.Cliente_idCliente
            WHERE (c.nombre LIKE ? OR c.apellidos LIKE ? OR CAST(c.idCliente AS CHAR) LIKE ?)
            GROUP BY c.idCliente
        """;

        // Añadir ordenación según el combo
        switch (orden) {
            case 0: sql += " ORDER BY c.nombre ASC, c.apellidos ASC"; break; // A-Z
            case 1: sql += " ORDER BY totalCompras DESC"; break;             // Num Compras
            case 2: sql += " ORDER BY c.puntosCliente DESC"; break;          // Puntos
            default: sql += " ORDER BY c.nombre ASC"; 
        }

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + busqueda + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getInt("puntosCliente"),
                        rs.getInt("totalCompras")
                    );
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}