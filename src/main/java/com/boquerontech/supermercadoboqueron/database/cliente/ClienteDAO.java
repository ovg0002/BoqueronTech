package com.boquerontech.supermercadoboqueron.database.cliente;

import com.boquerontech.supermercadoboqueron.database.DDBBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.UUID;
import com.boquerontech.supermercadoboqueron.clientes.Cliente;

/**
 * DAO para las operaciones de la tabla Cliente en la base de datos.
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo cliente en la base de datos.
     * Genera un código de cliente único antes de la inserción.
     * @param cliente El objeto Cliente con los datos a insertar (excepto id, puntos y codigoCliente).
     * @return true si el cliente fue creado exitosamente, false en caso contrario.
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
                pstmt.setDate(3, Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDni());
            pstmt.setString(6, cliente.getCodigoCliente());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Imprimir un mensaje más específico para errores de duplicados
            if (e.getErrorCode() == 1062) { // Código de error para entrada duplicada en MySQL
                System.err.println("Error al crear el cliente: Ya existe un cliente con ese DNI, teléfono o código.");
            } else {
                System.err.println("Error al crear el cliente: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
}
