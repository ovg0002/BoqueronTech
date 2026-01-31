/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes;

import com.boquerontech.supermercadoboqueron.informes.items.ItemVenta;
import com.boquerontech.supermercadoboqueron.Inicio;
import com.boquerontech.supermercadoboqueron.informes.modelo.VentaRow;
import com.boquerontech.supermercadoboqueron.database.informes.VentaDAO;
import java.util.List;
import javax.swing.JPanel;
/**
 *
 * @author navas
 */
public class ConsultarVentas extends javax.swing.JPanel {
private Inicio inicioInstance;
private List<VentaRow> listaCompleta;
    private int paginaActual = 1;
    private final int ITEMS_POR_PAGINA = 7;
    /**
     * Creates new form ListadodeProductos
     */
    public ConsultarVentas() {
        initComponents();
        cargarDatos();
    }
    public ConsultarVentas(Inicio inicioInstance) {
        initComponents();
        this.inicioInstance = inicioInstance;
        
        // Cargar opciones del combo si no están
        if (CBFiltrar.getItemCount() == 0 || CBFiltrar.getItemAt(0).startsWith("Item")) {
            CBFiltrar.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Recientes", "Por Empleado", "Forma de Pago" }
            ));
        }
        
        configurarListeners();
        cargarDatos();
    }
    private void configurarListeners() {
        // Buscador por fecha
        TFBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarDatos();
            }
        });
        
        TFBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if(TFBuscar.getText().equals("Buscar")) TFBuscar.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if(TFBuscar.getText().isEmpty()) TFBuscar.setText("Buscar");
            }
        });
        
        // Filtro
        CBFiltrar.addActionListener(evt -> cargarDatos());
        
        // Paginación (Combo de abajo)
        CBPaginas.addActionListener(evt -> {
            if(CBPaginas.getSelectedItem() != null) {
                try {
                    paginaActual = Integer.parseInt(CBPaginas.getSelectedItem().toString());
                    pintarPagina();
                } catch(NumberFormatException e) {}
            }
        });
    }

    private void cargarDatos() {
        String texto = TFBuscar.getText().trim();
        if(texto.equals("Buscar")) texto = "";
        
        int filtro = CBFiltrar.getSelectedIndex();
        
        // 1. Obtener datos del DAO
        listaCompleta = VentaDAO.listarVentasInforme(texto, filtro);
        
        // 2. Calcular páginas
        int totalItems = listaCompleta.size();
        int totalPaginas = (int) Math.ceil((double) totalItems / ITEMS_POR_PAGINA);
        if(totalPaginas == 0) totalPaginas = 1;
        
        // 3. Rellenar combo paginación (evitando bucles de eventos)
        java.awt.event.ActionListener[] listeners = CBPaginas.getActionListeners();
        for(java.awt.event.ActionListener l : listeners) CBPaginas.removeActionListener(l);
        
        CBPaginas.removeAllItems();
        for(int i=1; i<=totalPaginas; i++) {
            CBPaginas.addItem(String.valueOf(i));
        }
        
        for(java.awt.event.ActionListener l : listeners) CBPaginas.addActionListener(l);
        
        // 4. Pintar
        paginaActual = 1;
        pintarPagina();
    }
    
    private void pintarPagina() {
        PnlCentralItems.removeAll();
        // Layout vertical para la lista
        PnlCentralItems.setLayout(new javax.swing.BoxLayout(PnlCentralItems, javax.swing.BoxLayout.Y_AXIS));
        
        if (listaCompleta != null && !listaCompleta.isEmpty()) {
            int inicio = (paginaActual - 1) * ITEMS_POR_PAGINA;
            int fin = Math.min(inicio + ITEMS_POR_PAGINA, listaCompleta.size());
            
            for (int i = inicio; i < fin; i++) {
                VentaRow v = listaCompleta.get(i);
                
                com.boquerontech.supermercadoboqueron.informes.items.ItemVenta item = 
                        new com.boquerontech.supermercadoboqueron.informes.items.ItemVenta();
                
                item.setDatos(v);
                PnlCentralItems.add(item);
                PnlCentralItems.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
            }
        } else {
            PnlCentralItems.add(new javax.swing.JLabel("No se encontraron ventas."));
        }
        
        PnlCentralItems.revalidate();
        PnlCentralItems.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PanelMedioItemVentas = new javax.swing.JPanel();
        BtnSalir = new javax.swing.JButton();
        CBPaginas = new javax.swing.JComboBox<>();
        PanelInferiorConsultaVentas = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        PnlCentralItems = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        TFBuscar = new javax.swing.JTextField();
        CBFiltrar = new javax.swing.JComboBox<>();
        pnlCabecera = new javax.swing.JPanel();
        lblTittle = new javax.swing.JLabel();
        relleno = new javax.swing.JPanel();
        trabajadorPanel = new javax.swing.JPanel();
        pnlUser = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(233, 253, 253));
        setLayout(new java.awt.BorderLayout());

        PanelMedioItemVentas.setOpaque(false);
        PanelMedioItemVentas.setLayout(new java.awt.GridBagLayout());

        BtnSalir.setForeground(new java.awt.Color(199, 108, 108));
        BtnSalir.setText("Salir");
        BtnSalir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(199, 108, 108)));
        BtnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        BtnSalir.setOpaque(true);
        BtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSalirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 67;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        PanelMedioItemVentas.add(BtnSalir, gridBagConstraints);

        CBPaginas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CBPaginas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        CBPaginas.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        PanelMedioItemVentas.add(CBPaginas, gridBagConstraints);

        add(PanelMedioItemVentas, java.awt.BorderLayout.PAGE_END);

        PanelInferiorConsultaVentas.setOpaque(false);
        PanelInferiorConsultaVentas.setLayout(new java.awt.GridBagLayout());

        scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setViewportView(PnlCentralItems);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        PanelInferiorConsultaVentas.add(scroll, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        TFBuscar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TFBuscar.setText("Buscar");
        TFBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TFBuscar.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(TFBuscar, gridBagConstraints);

        CBFiltrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CBFiltrar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(CBFiltrar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        PanelInferiorConsultaVentas.add(jPanel1, gridBagConstraints);

        add(PanelInferiorConsultaVentas, java.awt.BorderLayout.CENTER);

        pnlCabecera.setBackground(new java.awt.Color(233, 253, 253));
        pnlCabecera.setLayout(new java.awt.GridBagLayout());

        lblTittle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTittle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTittle.setText("Consultar Ventas");
        lblTittle.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        pnlCabecera.add(lblTittle, gridBagConstraints);

        relleno.setBackground(new java.awt.Color(204, 204, 204));
        relleno.setOpaque(false);
        relleno.setPreferredSize(new java.awt.Dimension(200, 50));

        javax.swing.GroupLayout rellenoLayout = new javax.swing.GroupLayout(relleno);
        relleno.setLayout(rellenoLayout);
        rellenoLayout.setHorizontalGroup(
            rellenoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        rellenoLayout.setVerticalGroup(
            rellenoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 0);
        pnlCabecera.add(relleno, gridBagConstraints);

        trabajadorPanel.setBackground(new java.awt.Color(204, 204, 204));
        trabajadorPanel.setPreferredSize(new java.awt.Dimension(200, 60));

        pnlUser.setBackground(new java.awt.Color(255, 255, 255));
        pnlUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlUser.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlUser.setPreferredSize(new java.awt.Dimension(161, 60));
        pnlUser.setLayout(new java.awt.GridBagLayout());

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/img_torrente.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Torrente Segura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Jefe Boqueron");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel7, gridBagConstraints);

        javax.swing.GroupLayout trabajadorPanelLayout = new javax.swing.GroupLayout(trabajadorPanel);
        trabajadorPanel.setLayout(trabajadorPanelLayout);
        trabajadorPanelLayout.setHorizontalGroup(
            trabajadorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
            .addGroup(trabajadorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        trabajadorPanelLayout.setVerticalGroup(
            trabajadorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
            .addGroup(trabajadorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 20);
        pnlCabecera.add(trabajadorPanel, gridBagConstraints);

        add(pnlCabecera, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSalirActionPerformed
        // TODO add your handling code here:
        // Volver a Inicio de Analisis
        if (inicioInstance != null) {
            inicioInstance.colocarPanel(new InicioAnalisisyConsultas(inicioInstance));
        }
    }//GEN-LAST:event_BtnSalirActionPerformed

    // Esto añade más items al panel del scroll
    private void ponerItems() {
        //panelVentasRealizadas.add(new ItemConsultasVentas());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnSalir;
    private javax.swing.JComboBox<String> CBFiltrar;
    private javax.swing.JComboBox<String> CBPaginas;
    private javax.swing.JPanel PanelInferiorConsultaVentas;
    private javax.swing.JPanel PanelMedioItemVentas;
    private javax.swing.JPanel PnlCentralItems;
    private javax.swing.JTextField TFBuscar;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTittle;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JPanel relleno;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JPanel trabajadorPanel;
    // End of variables declaration//GEN-END:variables
}
