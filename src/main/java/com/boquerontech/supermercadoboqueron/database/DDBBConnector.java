/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author velag
 */
public class DDBBConnector {
    private static HikariDataSource dataSource;
    
    // Crear el pool con las conexiones una vez al iniciar la app
    static {
        try {
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl("jdbc:mysql://localhost:3306/superboqueron");
            config.setUsername("root");
            config.setPassword("superboqueron1234");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // MaximumPoolSize: Cuántas conexiones máximas quieres tener abiertas
            config.setMaximumPoolSize(10);
            
            // MinimumIdle: Cuántas conexiones quieres tener "dormidas" esperando
            // Poner 2 significa que siempre habrá 2 listas para usar instantáneamente
            config.setMinimumIdle(2);
            
            // IdleTimeout: Si una conexión sobra y lleva 5 minutos (300000ms) sin usarse, se borra
            config.setIdleTimeout(300000);
            
            // ConnectionTimeout: Si el pool está lleno, cuánto tiempo espera antes de dar error
            config.setConnectionTimeout(3000); // 3 segundos

            // Crear el Pool
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Error fatal iniciando el Pool de Conexiones");
            e.printStackTrace();
        }
    }
    
    // Constructor privado para evitar instanciaciones
    private DDBBConnector() {}
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado.");
        }
    }
}
