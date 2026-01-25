/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.producto;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.productos.Categoria;
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
public class CategoriaDAO {
    public static List<Categoria> getProductCategories(int idProducto) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = """
        SELECT c.idCategoria, c.nombre FROM Categoria c
            INNER JOIN Producto_has_Categoria pc
                ON c.idCategoria = pc.Categoria_idCategoria
            WHERE pc.Producto_idProducto = ?
        """;
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("nombre")
                    );
                    categorias.add(cat);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return categorias;
    }
    
    public static List<Categoria> getAllCategories() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = """
        SELECT idCategoria, nombre
            FROM Categoria
        """;
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) categorias.add(new Categoria(rs.getInt("idCategoria"), rs.getString("nombre")));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return categorias;
    }
}
