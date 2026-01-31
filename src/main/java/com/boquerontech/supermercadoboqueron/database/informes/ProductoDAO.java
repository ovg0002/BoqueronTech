/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.informes;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.informes.modelo.ProductoRow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author navas
 */
public class ProductoDAO {

    /**
     * Método específico para la pantalla de "Gestión de Productos".
     * Realiza un JOIN entre Producto, Categoria y Proveedor, y cuenta las ventas.
     * * @param busqueda Texto para buscar por nombre del producto.
     * @param filtro Índice del ComboBox (0: Categoría, 1: Stock Bajo, 2: Proveedor, 3: Más vendidos).
     * @return Lista de objetos ProductoRow listos para pintar en la tabla.
     */
    public static List<ProductoRow> listarProductosGestion(String busqueda, int filtro) {
        List<ProductoRow> lista = new ArrayList<>();
        
        // Construimos la consulta SQL
        // Usamos LEFT JOIN para proveedores porque un producto podría no tener proveedor asignado aún
        // Usamos una subconsulta para contar las ventas en DetalleVenta
        String sql = """
            SELECT p.nombre, p.precio, p.stock, p.minStock,
                   c.nombre as catNombre,
                   prov.nombre as provNombre,
                   (SELECT COUNT(*) FROM DetalleVenta dv WHERE dv.Producto_idProducto = p.idProducto) as totalVentas
            FROM Producto p
            JOIN Categoria c ON p.Categoria_idCategoria = c.idCategoria
            LEFT JOIN Proveedor_has_Producto php ON p.idProducto = php.Producto_idProducto
            LEFT JOIN Proveedor prov ON php.Proveedor_idProveedor = prov.idProveedor
            WHERE p.nombre LIKE ?
        """;

        // APLICAR FILTROS LÓGICOS (WHERE)
        if (filtro == 1) { // "Stock bajo/agotado"
            sql += " AND p.stock <= p.minStock ";
        }
        
        // APLICAR ORDENACIÓN (ORDER BY)
        switch (filtro) {
            case 0: // "Categoría (A-Z)" (Por defecto)
                sql += " ORDER BY c.nombre ASC, p.nombre ASC"; 
                break;
            case 1: // "Stock bajo" (Ordenamos por stock ascendente para ver los de 0 primero)
                sql += " ORDER BY p.stock ASC"; 
                break;
            case 2: // "Proveedor"
                sql += " ORDER BY prov.nombre ASC, p.nombre ASC"; 
                break;
            case 3: // "Más vendidos"
                sql += " ORDER BY totalVentas DESC"; 
                break;
            default: 
                sql += " ORDER BY p.nombre ASC";
        }

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignamos el parámetro de búsqueda (el % sirve para buscar coincidencias parciales)
            pstmt.setString(1, "%" + busqueda + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Creamos el objeto auxiliar ProductoRow con los datos obtenidos
                    String nombre = rs.getString("nombre");
                    String categoria = rs.getString("catNombre");
                    double precio = rs.getDouble("precio");
                    int stock = rs.getInt("stock");
                    
                    // Si el proveedor es null (por el LEFT JOIN), ponemos texto informativo
                    String proveedor = rs.getString("provNombre");
                    if (proveedor == null) proveedor = "Sin Proveedor";
                    
                    int numVentas = rs.getInt("totalVentas");

                    // Añadimos a la lista
                    lista.add(new ProductoRow(nombre, categoria, precio, stock, proveedor, numVentas));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al listar productos en Gestión: " + e.getMessage());
        }
        
        return lista;
    }
}
