/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.informes;

import com.boquerontech.supermercadoboqueron.Inicio;
import com.boquerontech.supermercadoboqueron.informes.modelo.ProductoRow;
import com.boquerontech.supermercadoboqueron.database.informes.ProductoDAO;
import java.util.List;
import javax.swing.JPanel;

public class GestiondeProductos extends javax.swing.JPanel {

    private Inicio inicioInstance;
    
    // Componentes manuales (ya que faltaban en tu initComponents original para buscar/filtrar)
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JComboBox<String> cmbFiltro;

    // Datos
    private List<ProductoRow> listaCompleta;
    private int paginaActual = 1;
    private final int ITEMS_POR_PAGINA = 7;

    public GestiondeProductos() {
        initComponents();
        initManualComponents();
        cargarDatos();
    }
    
    public GestiondeProductos(Inicio inicioInstance) {
        initComponents();
        this.inicioInstance = inicioInstance;
        initManualComponents();
        cargarDatos();
    }
    
    // Agregamos buscador y filtro a la cabecera manualmente
    private void initManualComponents() {
        txtBuscar = new javax.swing.JTextField("Producto");
        txtBuscar.setPreferredSize(new java.awt.Dimension(150, 30));
        
        cmbFiltro = new javax.swing.JComboBox<>(new String[] { "Categoría (A-Z)", "Stock bajo/agotado", "Proveedor", "Más vendidos" });
        cmbFiltro.setPreferredSize(new java.awt.Dimension(250, 30));
        
        // Configurar Listeners
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if(txtBuscar.getText().equals("Producto")) txtBuscar.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if(txtBuscar.getText().isEmpty()) txtBuscar.setText("Producto");
            }
        });
        
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarDatos();
            }
        });
        
        cmbFiltro.addActionListener(evt -> cargarDatos());
        
        // Listener del combo de paginación (abajo)
        CBPaginas.addActionListener(evt -> {
            if(CBPaginas.getSelectedItem() != null) {
                try {
                    paginaActual = Integer.parseInt(CBPaginas.getSelectedItem().toString());
                    pintarPagina();
                } catch(NumberFormatException e) {}
            }
        });

        // Añadirlos al panel de cabecera visualmente (usando GridBag constraints)
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new java.awt.Insets(0, 20, 0, 10);
        pnlCabecera.add(txtBuscar, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = java.awt.GridBagConstraints.WEST;
        pnlCabecera.add(cmbFiltro, gbc);
        
        // Ajustamos el título para que suba un poco
        java.awt.GridBagConstraints gbcTitulo = ((java.awt.GridBagLayout)pnlCabecera.getLayout()).getConstraints(lblTittle);
        gbcTitulo.gridwidth = 4; 
        pnlCabecera.remove(lblTittle);
        pnlCabecera.add(lblTittle, gbcTitulo);
    }

    private void cargarDatos() {
        String texto = txtBuscar.getText().trim();
        if(texto.equals("Producto")) texto = "";
        
        int filtro = cmbFiltro.getSelectedIndex();
        
        // 1. Obtener datos
        listaCompleta = ProductoDAO.listarProductosGestion(texto, filtro);
        
        // 2. Calcular páginas totales
        int totalItems = listaCompleta.size();
        int totalPaginas = (int) Math.ceil((double) totalItems / ITEMS_POR_PAGINA);
        if(totalPaginas == 0) totalPaginas = 1;
        
        // 3. Rellenar combo de paginación
        CBPaginas.removeAllItems();
        for(int i=1; i<=totalPaginas; i++) {
            CBPaginas.addItem(String.valueOf(i));
        }
        
        // 4. Pintar primera página
        paginaActual = 1;
        pintarPagina();
    }
    
    private void pintarPagina() {
        PnlCentral.removeAll();
        // Usamos BoxLayout vertical para la lista
        PnlCentral.setLayout(new javax.swing.BoxLayout(PnlCentral, javax.swing.BoxLayout.Y_AXIS));
        
        if (listaCompleta != null && !listaCompleta.isEmpty()) {
            int inicio = (paginaActual - 1) * ITEMS_POR_PAGINA;
            int fin = Math.min(inicio + ITEMS_POR_PAGINA, listaCompleta.size());
            
            for (int i = inicio; i < fin; i++) {
                ProductoRow p = listaCompleta.get(i);
                
                com.boquerontech.supermercadoboqueron.informes.items.ItemProductoGestion item = 
                        new com.boquerontech.supermercadoboqueron.informes.items.ItemProductoGestion();
                
                item.setDatos(p);
                PnlCentral.add(item);
                PnlCentral.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 5)));
            }
        } else {
            PnlCentral.add(new javax.swing.JLabel("No hay productos que coincidan."));
        }
        
        PnlCentral.revalidate();
        PnlCentral.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PnlBajo = new javax.swing.JPanel();
        BtnSalir = new javax.swing.JButton();
        CBPaginas = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        PnlCentral = new javax.swing.JPanel();
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

        PnlBajo.setOpaque(false);
        PnlBajo.setLayout(new java.awt.GridBagLayout());

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
        PnlBajo.add(BtnSalir, gridBagConstraints);

        CBPaginas.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        PnlBajo.add(CBPaginas, gridBagConstraints);

        add(PnlBajo, java.awt.BorderLayout.PAGE_END);

        PnlCentral.setOpaque(false);
        jScrollPane1.setViewportView(PnlCentral);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlCabecera.setBackground(new java.awt.Color(233, 253, 253));
        pnlCabecera.setLayout(new java.awt.GridBagLayout());

        lblTittle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTittle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTittle.setText("Listado de Productos");
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
    }// </editor-fold>                        

    private void BtnSalirActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        if(inicioInstance != null) {
            inicioInstance.colocarPanel(new InicioDocumentos(inicioInstance));
        }
    }                                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton BtnSalir;
    private javax.swing.JComboBox<String> CBPaginas;
    private javax.swing.JPanel PnlBajo;
    private javax.swing.JPanel PnlCentral;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTittle;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JPanel relleno;
    private javax.swing.JPanel trabajadorPanel;
    // End of variables declaration                   
}