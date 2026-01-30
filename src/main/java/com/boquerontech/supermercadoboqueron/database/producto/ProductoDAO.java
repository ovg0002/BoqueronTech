/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.producto;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.productos.Categoria;
import com.boquerontech.supermercadoboqueron.productos.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author velag
 */
public class ProductoDAO {

    // QUERY BASE: Usamos un JOIN para traer los datos del producto Y su categoría de golpe.
    // Usamos alias (c.nombre AS nombre_categoria) para no confundirlo con el nombre del producto.
    private static final String BASE_QUERY = """
        SELECT p.idProducto, p.nombre, p.precio, p.stock, p.minStock, p.Categoria_idCategoria,
               c.nombre AS nombre_categoria
        FROM Producto p
        INNER JOIN Categoria c ON p.Categoria_idCategoria = c.idCategoria
    """;

    public static Producto getProductoByID(int id) {
        Producto producto = null;
        String sql = BASE_QUERY + " WHERE p.idProducto = ?";
        
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
        String sql = BASE_QUERY + " WHERE p.nombre = ?";
        
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
        String sql = BASE_QUERY + " WHERE p.stock >= ?";
        
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
    
    public static void updateProduct(int id, String nombre, double precio, int minStock, String categoryName) {
        // Obtenemos el ID de la categoría nueva
        int idCategoria = CategoriaDAO.getCategoryIdByName(categoryName);
        if (idCategoria == 0) return; // Si no existe la categoría, abortamos (o lanzamos error)

        // AHORA EL UPDATE ES DIRECTO Y MUCHO MÁS LIMPIO
        String updateProducto = """
            UPDATE Producto
                SET nombre = ?, precio = ?, minStock = ?, Categoria_idCategoria = ?
                WHERE idProducto = ?
        """;
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateProducto)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setInt(3, minStock);
            pstmt.setInt(4, idCategoria); // Actualizamos la FK directamente
            pstmt.setInt(5, id);
            
            pstmt.executeUpdate();
            
            // YA NO NECESITAMOS BORRAR E INSERTAR EN LA TABLA INTERMEDIA PARA LA CATEGORÍA PRINCIPAL
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    // Método para borrar (Lógico o Físico, tú decides, aquí pongo el Delete físico simple adaptado)
    public static void deleteProducto(int idProducto) {
        // Recuerda que si tienes ON DELETE NO ACTION, primero tendrías que borrar dependencias 
        // o usar el método del Soft Delete (activo = 0) si modificas la tabla.
        String sql = "DELETE FROM Producto WHERE idProducto = ?";
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    // ==============================
    //            HELPERS
    // ==============================
    private static Producto crearProductoDeResultSet(ResultSet rs) throws SQLException {
        // Creamos el objeto Categoría al vuelo con los datos del JOIN
        Categoria cat = new Categoria(
            rs.getInt("Categoria_idCategoria"),
            rs.getString("nombre_categoria") // OJO AL ALIAS DEL SQL
        );
        
        return new Producto(
            rs.getInt("idProducto"),
            rs.getString("nombre"),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getInt("minStock"),
            cat // Asignamos el objeto único
        );
    }
}