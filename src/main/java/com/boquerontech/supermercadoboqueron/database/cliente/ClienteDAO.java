/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database.cliente;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import com.boquerontech.supermercadoboqueron.clientes.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO Unificado para la gestión de Clientes.
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo cliente en la base de datos.
     * Genera un código de cliente único antes de la inserción.
     */
    public boolean crearCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (nombre, apellidos, fechaNacimiento, telefono, dni, codigoCliente) VALUES (?, ?, ?, ?, ?, ?)";

        // Generar un código de cliente único
        String codigoCliente = UUID.randomUUID().toString();
        cliente.setCodigoCliente(codigoCliente);

        try (Connection conn = DDBBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellidos());

            if (cliente.getFechaNacimiento() != null) {
                // Convertimos LocalDate a java.sql.Date
                pstmt.setDate(3, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDni());
            pstmt.setString(6, cliente.getCodigoCliente());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.err.println("Error: Ya existe un cliente con ese DNI, teléfono o código.");
            } else {
                System.err.println("Error al crear el cliente: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene clientes con filtros y ordenación para los informes.
     */
    public static List<Cliente> listarClientes(String busqueda, int orden) {
        List<Cliente> lista = new ArrayList<>();
        
        // SQL: Recuperamos datos básicos + Total de compras calculado
        String sql = """
            SELECT c.idCliente, c.nombre, c.apellidos, c.telefono, c.puntosCliente, 
                   COUNT(v.idVenta) as totalCompras
            FROM Cliente c
            LEFT JOIN Venta v ON c.idCliente = v.Cliente_idCliente
            WHERE (c.nombre LIKE ? OR c.apellidos LIKE ? OR CAST(c.idCliente AS CHAR) LIKE ?)
            GROUP BY c.idCliente
        """;

        // Ordenación
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
                    // Creamos el cliente. OJO: Necesitas un constructor en Cliente que acepte esto.
                    // Si no tienes fecha nacimiento en la select, pasamos null o la ignoramos en este contexto.
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("idCliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidos(rs.getString("apellidos"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setPuntosCliente(rs.getInt("puntosCliente"));
                    
                    // Asignamos el campo calculado (Necesitas añadir este setter en tu clase Cliente)
                    c.setTotalCompras(rs.getInt("totalCompras"));
                    
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
                return lista;
            }
        
            public List<Cliente> buscarClientesPorNombreOApellido(String nombreOApellido) {
                List<Cliente> lista = new ArrayList<>();
                String sql = "SELECT * FROM Cliente WHERE nombre LIKE ? OR apellidos LIKE ?";
        
                try (Connection conn = DDBBConnector.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
                    String searchPattern = "%" + nombreOApellido + "%";
                    pstmt.setString(1, searchPattern);
                    pstmt.setString(2, searchPattern);
        
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Cliente c = new Cliente();
                            c.setIdCliente(rs.getInt("idCliente"));
                            c.setNombre(rs.getString("nombre"));
                            c.setApellidos(rs.getString("apellidos"));
                            Date fechaNacimiento = rs.getDate("fechaNacimiento");
                            if (fechaNacimiento != null) {
                                c.setFechaNacimiento(fechaNacimiento.toLocalDate());
                            }
                            c.setTelefono(rs.getString("telefono"));
                            c.setDni(rs.getString("dni"));
                            c.setPuntosCliente(rs.getInt("puntosCliente"));
                            c.setCodigoCliente(rs.getString("codigoCliente"));
                            lista.add(c);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return lista;
            }
            
            public boolean eliminarCliente(int idCliente) {
                String sql = "DELETE FROM Cliente WHERE idCliente = ?";
        
                try (Connection conn = DDBBConnector.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
                    pstmt.setInt(1, idCliente);
                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;
        
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            public boolean actualizarCliente(Cliente cliente) {
                String sql = "UPDATE Cliente SET nombre = ?, apellidos = ?, fechaNacimiento = ?, telefono = ?, dni = ? WHERE idCliente = ?";

                try (Connection conn = DDBBConnector.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, cliente.getNombre());
                    pstmt.setString(2, cliente.getApellidos());
                    if (cliente.getFechaNacimiento() != null) {
                        pstmt.setDate(3, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
                    } else {
                        pstmt.setNull(3, java.sql.Types.DATE);
                    }
                    pstmt.setString(4, cliente.getTelefono());
                    pstmt.setString(5, cliente.getDni());
                    pstmt.setInt(6, cliente.getIdCliente());

                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        