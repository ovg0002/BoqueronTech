/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.promocion;

/**
 *
 * @author navas
 */
import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.promociones.Promocion; 
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PromocionDAO {

    // Método para insertar una nueva promoción
    public static boolean insertarPromocion(Promocion promo) {
        String sql = """
            INSERT INTO Promociones 
            (nombrePromocion, unidadesAfectadas, precioPorUnidad, fechaInicio, fechaFin, Categoria_idCategoria) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, promo.getNombrePromocion());
            pstmt.setInt(2, promo.getUnidadesAfectadas());
            pstmt.setDouble(3, promo.getPrecioPorUnidad());
            pstmt.setDate(4, Date.valueOf(promo.getFechaInicio())); // Conversión LocalDate a SQL Date
            
            if (promo.getFechaFin() != null) {
                pstmt.setDate(5, Date.valueOf(promo.getFechaFin()));
            } else {
                pstmt.setDate(5, null);
            }
            
            pstmt.setInt(6, promo.getIdCategoria());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }
    // Método para LISTAR todas las promociones
    public static java.util.List<Promocion> listarPromociones() {
        java.util.List<Promocion> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Promociones";

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Promocion p = new Promocion();
                p.setIdPromociones(rs.getInt("idPromociones"));
                p.setNombrePromocion(rs.getString("nombrePromocion"));
                p.setUnidadesAfectadas(rs.getInt("unidadesAfectadas"));
                p.setPrecioPorUnidad(rs.getDouble("precioPorUnidad"));
                
                // Convertir java.sql.Date a java.time.LocalDate
                java.sql.Date fechaIni = rs.getDate("fechaInicio");
                if (fechaIni != null) p.setFechaInicio(fechaIni.toLocalDate());
                
                java.sql.Date fechaFin = rs.getDate("fechaFin");
                if (fechaFin != null) p.setFechaFin(fechaFin.toLocalDate());
                
                p.setIdCategoria(rs.getInt("Categoria_idCategoria"));
                
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Método para ACTUALIZAR una promoción existente
    public static boolean actualizarPromocion(com.boquerontech.supermercadoboqueron.promociones.Promocion promo) {
        String sql = "UPDATE Promociones SET nombrePromocion=?, unidadesAfectadas=?, precioPorUnidad=?, fechaInicio=?, fechaFin=?, Categoria_idCategoria=? WHERE idPromociones=?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, promo.getNombrePromocion());
            pstmt.setInt(2, promo.getUnidadesAfectadas());
            pstmt.setDouble(3, promo.getPrecioPorUnidad());
            pstmt.setDate(4, Date.valueOf(promo.getFechaInicio()));
            
            if (promo.getFechaFin() != null) {
                pstmt.setDate(5, Date.valueOf(promo.getFechaFin()));
            } else {
                pstmt.setDate(5, null);
            }
            
            pstmt.setInt(6, promo.getIdCategoria());
            // El ID es fundamental para saber cuál actualizar
            pstmt.setInt(7, promo.getIdPromociones()); 

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Método para ELIMINAR (Dar de baja) una promoción
    public static boolean eliminarPromocion(int idPromocion) {
        // OJO: Si tienes productos asociados en la tabla intermedia, 
        // deberías borrarlos primero o la base de datos podría dar error por restricciones (Foreign Key).
        // Intentamos borrar la promoción directamente:
        String sql = "DELETE FROM Promociones WHERE idPromociones = ?";
        
        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPromocion);

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            // Si falla por claves foráneas, habría que borrar primero de Promociones_has_Producto
            return false;
        }
    }
    // Método para ACTIVAR o DESACTIVAR una promoción
    public static boolean cambiarEstadoPromocion(int idPromocion, boolean activar) {
        String sql;
        // Si activamos -> Ponemos fecha fin NULL (indefinida) o una fecha futura lejana
        // Si desactivamos -> Ponemos fecha fin a AYER para que caduque
        if (activar) {
            sql = "UPDATE Promociones SET fechaFin = NULL WHERE idPromociones = ?";
        } else {
            // Usamos fecha de ayer para "matar" la promo
            sql = "UPDATE Promociones SET fechaFin = DATE_SUB(CURDATE(), INTERVAL 1 DAY) WHERE idPromociones = ?";
        }

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPromocion);
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
