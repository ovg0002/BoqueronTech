package com.boquerontech.supermercadoboqueron.database.informes;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.informes.modelo.VentaRow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public static List<VentaRow> listarVentasInforme(String fechaBusqueda, int filtro) {
        List<VentaRow> lista = new ArrayList<>();
        
        // SQL: Unimos Venta con Empleado y con los Productos (agrupándolos)
        String sql = """
            SELECT v.fecha, v.metodoPago, e.nombre as nombreEmp,
                   GROUP_CONCAT(p.nombre SEPARATOR ', ') as listaProductos
            FROM Venta v
            JOIN Empleado e ON v.Empleado_idEmpleado = e.idEmpleado
            JOIN DetalleVenta dv ON v.idVenta = dv.Venta_idVenta
            JOIN Producto p ON dv.Producto_idProducto = p.idProducto
            WHERE CAST(v.fecha AS CHAR) LIKE ?
            GROUP BY v.idVenta
        """;

        // Ordenación según el filtro del combo
        switch(filtro) {
            case 1: sql += " ORDER BY e.nombre ASC"; break; // Por Empleado
            case 2: sql += " ORDER BY v.metodoPago ASC"; break; // Por Forma de Pago
            default: sql += " ORDER BY v.fecha DESC"; // Por defecto (Más recientes)
        }

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + fechaBusqueda + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String prods = rs.getString("listaProductos");
                    if(prods.length() > 30) prods = prods.substring(0, 27) + "..."; // Acortar si es muy largo
                    
                    lista.add(new VentaRow(
                        prods,
                        "Realizado", // Asumimos que si está en Venta, está realizada
                        rs.getString("fecha"),
                        rs.getString("nombreEmp"),
                        rs.getString("metodoPago")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}