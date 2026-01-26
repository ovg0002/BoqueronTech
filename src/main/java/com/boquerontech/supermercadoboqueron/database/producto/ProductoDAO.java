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
    public static Producto getProductoByID(int id) {
        Producto producto = null;
        String sql = """
        SELECT idProducto, nombre, precio, stock, minStock FROM Producto
            WHERE idProducto = ?
        """;
        
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
        String sql = """
        SELECT idProducto, nombre, precio, stock, minStock FROM Producto
            WHERE nombre = ?
        """;
        
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
        String sql = """
        SELECT idProducto, nombre, precio, stock, minStock
            FROM Producto
            WHERE stock >= ?
        """;
        
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
    
    // ==============================
    //            HELPERS
    // ==============================
    private static Producto crearProductoDeResultSet(ResultSet rs) throws SQLException {
        int productID = rs.getInt("idProducto");
        
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
}
