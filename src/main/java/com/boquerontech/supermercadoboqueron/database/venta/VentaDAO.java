/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.venta;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.database.cliente.ClienteDAO;
import com.boquerontech.supermercadoboqueron.productos.Producto;
import com.boquerontech.supermercadoboqueron.ventas.Venta;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 *
 * @author velag
 */
public class VentaDAO {
    public static boolean registrarVentaCompleta(Venta venta, Map<Producto, Integer> carrito, double totalVenta) {
        String sqlVenta = "INSERT INTO Venta (fecha, metodoPago, Empleado_idEmpleado, Cliente_idCliente) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO DetalleVenta (Producto_idProducto, Venta_idVenta, precioUnitarioHistorico, cantidad) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ?";

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;

        try {
            conn = DDBBConnector.getConnection();
            // 1. INICIAR TRANSACCIÓN (Desactivar guardado automático)
            conn.setAutoCommit(false);

            // --- PASO 1: INSERTAR VENTA ---
            psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setDate(1, Date.valueOf(venta.getFecha()));
            psVenta.setString(2, venta.getMetodoPago());
            psVenta.setInt(3, venta.getIdEmpleado());
            psVenta.setInt(4, venta.getIdCliente());
            
            int filasAfectadas = psVenta.executeUpdate();
            if (filasAfectadas == 0) throw new SQLException("Fallo al crear la venta, no se insertaron filas.");

            // Obtener el ID generado para la Venta
            int idVentaGenerado = -1;
            try (ResultSet rs = psVenta.getGeneratedKeys()) {
                if (rs.next()) {
                    idVentaGenerado = rs.getInt(1);
                } else {
                    throw new SQLException("Fallo al crear la venta, no se obtuvo el ID.");
                }
            }

            // --- PASO 2 Y 3: DETALLES Y STOCK (Bucle) ---
            psDetalle = conn.prepareStatement(sqlDetalle);
            psStock = conn.prepareStatement(sqlUpdateStock);

            for (Map.Entry<Producto, Integer> linea : carrito.entrySet()) {
                Producto prod = linea.getKey();
                Integer cantidad = linea.getValue();

                // Insertar Detalle
                psDetalle.setInt(1, prod.getId());
                psDetalle.setInt(2, idVentaGenerado);
                psDetalle.setDouble(3, prod.getPrecio()); // Precio original histórico
                psDetalle.setInt(4, cantidad);
                psDetalle.addBatch(); // Añadir al lote

                // Descontar Stock
                psStock.setInt(1, cantidad);
                psStock.setInt(2, prod.getId());
                psStock.addBatch(); // Añadir al lote
            }

            // Ejecutar lotes
            psDetalle.executeBatch();
            psStock.executeBatch();
            
            // --- PASO 4: SUMAR PUNTOS AL CLIENTE ---
            // Regla: 1 punto por céntimo -> Total * 100
            int puntosGanados = (int) (totalVenta * 100);
            
            // Llamamos al método del ClienteDAO pasando ESTA conexión (para mantener la transacción)
            boolean puntosActualizados = ClienteDAO.sumarPuntos(conn, venta.getIdCliente(), puntosGanados);
            if (!puntosActualizados) throw new SQLException("Error al actualizar puntos del cliente.");

            // 2. CONFIRMAR TRANSACCIÓN (Todo ha ido bien)
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Error en la transacción. Deshaciendo cambios (Rollback)...");
                    conn.rollback(); // DESHACER TODO SI HAY ERROR
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Restaurar estado y cerrar recursos
            try {
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (psStock != null) psStock.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
