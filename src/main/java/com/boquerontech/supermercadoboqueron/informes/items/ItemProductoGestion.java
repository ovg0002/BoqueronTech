/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes.items;

import com.boquerontech.supermercadoboqueron.informes.modelo.ProductoRow;

/**
 *
 * @author navas
 */
public class ItemProductoGestion extends javax.swing.JPanel {

    public ItemProductoGestion() {
        initComponents();
    }
    
    public void setDatos(ProductoRow p) {
        lblNombre.setText(p.getNombre());
        lblCategoria.setText(p.getCategoria());
        lblPrecio.setText(String.format("%.2f €", p.getPrecio()));
        lblStock.setText(String.valueOf(p.getStock()));
        lblProveedor.setText(p.getProveedor());
        lblVentas.setText(String.valueOf(p.getNumVentas()));
        
        // Opcional: Poner en rojo si el stock es 0
        if(p.getStock() <= 0) {
            lblStock.setForeground(java.awt.Color.RED);
            lblNombre.setForeground(java.awt.Color.RED);
        }
    }

    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblNombre = new javax.swing.JLabel();
        lblCategoria = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        lblStock = new javax.swing.JLabel();
        lblProveedor = new javax.swing.JLabel();
        lblVentas = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(204, 204, 204)));
        setLayout(new java.awt.GridLayout(1, 6, 10, 0));

        lblNombre.setText("Nombre");
        add(lblNombre);

        lblCategoria.setText("Cat");
        add(lblCategoria);

        lblPrecio.setText("0.00");
        add(lblPrecio);

        lblStock.setText("0");
        add(lblStock);

        lblProveedor.setText("Prov");
        add(lblProveedor);

        lblVentas.setText("0");
        add(lblVentas);
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblProveedor;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblVentas;
    // End of variables declaration                   
}