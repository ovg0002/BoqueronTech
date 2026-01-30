/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.producto;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.productos.Categoria;
import com.boquerontech.supermercadoboqueron.productos.Producto;
import com.boquerontech.supermercadoboqueron.informes.modelo.ProductoRow; // Asegúrate de importar esto donde lo tengas
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

    // ==========================================
    // MÉTODOS ORIGINALES (Inventario, etc.)
    // ==========================================

    public static Producto getProductoByID(int id) {
        Producto producto = null;
        String sql = "SELECT idProducto, nombre, precio, stock, minStock FROM Producto WHERE idProducto = ?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto = crearProductoDeResultSet(rs);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return producto;
    }
    
    public static Producto getProductoByName(String name) {
        Producto producto = null;
        String sql = "SELECT idProducto, nombre, precio, stock, minStock FROM Producto WHERE nombre = ?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto = crearProductoDeResultSet(rs);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return producto;
    }
    
    public static List<Producto> getProductsByMinCurrentStock(int minCurrentStock) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombre, precio, stock, minStock FROM Producto WHERE stock >= ?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, minCurrentStock);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(crearProductoDeResultSet(rs));
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return productos;
    }

    // Helper privado para los métodos originales
    private static Producto crearProductoDeResultSet(ResultSet rs) throws SQLException {
        int productID = rs.getInt("idProducto");
        // Asegúrate de que CategoriaDAO existe y tiene este método
        List<Categoria> categorias = CategoriaDAO.getProductCategories(productID); 
        
        return new Producto(
                productID,
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getInt("minStock"),
                categorias
        );
    }

    // ==========================================
    // NUEVOS MÉTODOS (Para Gestión de Productos - Informes)
    // ==========================================

    /**
     * Método específico para la pantalla de "Gestión de Productos".
     * Realiza un JOIN entre Producto, Categoria y Proveedor, y cuenta las ventas.
     */
    public static List<ProductoRow> listarProductosGestion(String busqueda, int filtro) {
        List<ProductoRow> lista = new ArrayList<>();
        
        // Consulta compleja para el informe
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

        // Filtros
        if (filtro == 1) { // Stock bajo
            sql += " AND p.stock <= p.minStock ";
        }
        
        // Ordenación
        switch (filtro) {
            case 0: sql += " ORDER BY c.nombre ASC, p.nombre ASC"; break;
            case 1: sql += " ORDER BY p.stock ASC"; break;
            case 2: sql += " ORDER BY prov.nombre ASC, p.nombre ASC"; break;
            case 3: sql += " ORDER BY totalVentas DESC"; break;
            default: sql += " ORDER BY p.nombre ASC";
        }

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + busqueda + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String categoria = rs.getString("catNombre");
                    double precio = rs.getDouble("precio");
                    int stock = rs.getInt("stock");
                    
                    String proveedor = rs.getString("provNombre");
                    if (proveedor == null) proveedor = "Sin Proveedor";
                    
                    int numVentas = rs.getInt("totalVentas");

                    // Usamos la clase auxiliar ProductoRow
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