/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.inventario;

import com.boquerontech.supermercadoboqueron.Inicio;
import com.boquerontech.supermercadoboqueron.database.producto.CategoriaDAO;
import com.boquerontech.supermercadoboqueron.database.producto.ProductoDAO;
import com.boquerontech.supermercadoboqueron.inventario.items.ProductoInventarioItem;
import com.boquerontech.supermercadoboqueron.productos.Categoria;
import com.boquerontech.supermercadoboqueron.productos.NuevoProducto;
import com.boquerontech.supermercadoboqueron.productos.Producto;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author MEDAC
 */
public class Inventario extends javax.swing.JPanel {

    // SOLO PARA PROBAR
    //private final int numProductos = 42;
    
    private final byte maxProdPagina = 20;
    
    // --------- ESTAS CAMBIAN SEGÚN LA BBDD ---------
    private final List<Producto> productosLista;
    private final List<Producto> productosFiltrados;
    private final List<Categoria> categoriasLista;
    
    /*
     * Creates new form Inventario
     */
    public Inventario() {
        initComponents();
        
        // Cargar de la BBDD
        productosLista = ProductoDAO.getProductsByMinCurrentStock(0);
        categoriasLista = CategoriaDAO.getAllCategories();
        
        // Rellenar el combo de las categorias
        rellenarComboCategorias();
        
        // Iniciar la lista de los productos filtrados
        productosFiltrados = new ArrayList<>(productosLista);
        
        // Configurar el buscador para que tenga el listener
        configurarBuscadorRealtime();
        
        rellenarConProductos();
    }
    
    // ---------------- MÉTODOS PRINCIPALES ----------------
    
    private void rellenarConProductos() {
        mainPanel.removeAll();

        int paginaActual = (Integer) paginaSpin.getValue();
        
        int inicio = (paginaActual - 1) * maxProdPagina;
        
        int fin = Math.min(inicio + maxProdPagina, productosFiltrados.size());

        for (int i = inicio; i < fin; i++) {
            Producto p = productosFiltrados.get(i);
            mainPanel.add(new ProductoInventarioItem(this, p, categoriasLista)); 
        }

        int productosPintados = fin - inicio;
        int huecosFaltantes = maxProdPagina - productosPintados;
        
        for (int k = 0; k < huecosFaltantes; k++) {
            JPanel vacio = new JPanel();
            vacio.setOpaque(false);
            mainPanel.add(vacio);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
        
        //System.out.println("Página " + paginaActual + " pintada. Índices: " + inicio + " a " + fin);        
    }
    
    private void aplicarFiltrosGlobales() {
        String textoBusqueda = buscarTxt.getText().trim().toLowerCase();
        String categoriaSeleccionada = (categoriesCombo.getSelectedItem() != null) 
                ? categoriesCombo.getSelectedItem().toString() : "Todos";

        // Obtener valor del stock del combo
        int indiceStock = maxStockCombo.getSelectedIndex();
        int maxStockValue = switch (indiceStock) {
            case 0 -> 10;
            case 1 -> 25;
            case 2 -> 50;
            case 3 -> 100;
            case 4 -> 200;
            case 5 -> 500;
            case 6 -> Integer.MAX_VALUE; // Todos
            default -> Integer.MAX_VALUE;
        };

        // Reiniciamos la lista filtrada
        productosFiltrados.clear();

        for (Producto p : productosLista) {
            // 1. Chequeo de Nombre
            boolean coincideNombre = textoBusqueda.isEmpty() || 
                                     textoBusqueda.equals("buscar") || 
                                     p.getNombre().toLowerCase().contains(textoBusqueda);

            // 2. Chequeo de Categoría
            boolean coincideCategoria = categoriaSeleccionada.equalsIgnoreCase("Todos") || 
                                        Objects.equals(p.getCategoria().getNombre(), categoriaSeleccionada);

            // 3. Chequeo de Stock
            boolean coincideStock = p.getStock() <= maxStockValue;
            
            // 4. Chequeo de Activo
            boolean esActivo = p.isActivo(); 

            // Si cumple LAS CUATRO condiciones, se añade
            if (coincideNombre && coincideCategoria && coincideStock && esActivo) {
                productosFiltrados.add(p);
            }
        }

        // Resetear a página 1 y pintar
        paginaSpin.setValue(1);
        rellenarConProductos();
    }
    
    // ---------------- EVENTOS DE UI (LISTENERS) ----------------
    
    // Añade el listener al hueco de búsqueda
    private void configurarBuscadorRealtime() {
        buscarTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {aplicarFiltrosGlobales();}
            @Override public void removeUpdate(DocumentEvent e) {aplicarFiltrosGlobales();}
            @Override public void changedUpdate(DocumentEvent e) {aplicarFiltrosGlobales();}
        });
    }
    
    // ---------------- OTROS MÉTODOS DE GESTIÓN ----------------
    
    private void rellenarComboCategorias() {
        if (categoriasLista != null) {
            for (Categoria c : categoriasLista) {
                categoriesCombo.addItem(c.getNombre());
            }
        }
    }
    
    public void updateProductosOnDelete(Producto productoEliminar) {
        if (ProductoDAO.deleteProducto(productoEliminar.getId())) {
            this.productosLista.remove(productoEliminar);
            // Actualizar interfaz
            aplicarFiltrosGlobales();
            JOptionPane.showMessageDialog(
                this,
                "El producto \"" + productoEliminar.getNombre() + "\" ha sido eliminado con éxito de la base de datos.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Error al eliminar el producto \"" + productoEliminar.getNombre() + "\" de la base de datos.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public void updateProductosOnUpdate(Producto productoInicial, Producto productoModificado) {
        int indice = -1;
    
        // Búsqueda manual por ID para asegurar que lo encontramos
        for (int i = 0; i < productosLista.size(); i++) {
            if (productosLista.get(i).getId() == productoInicial.getId()) {
                indice = i;
                break;
            }
        }

        if (indice != -1) {
            this.productosLista.set(indice, productoModificado);
        } else {
            this.productosLista.add(productoModificado);
        }

        aplicarFiltrosGlobales();
    }
    
    public void addProductoALista(Producto producto) {
        this.productosLista.add(producto);
        aplicarFiltrosGlobales();
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

        topPanel = new javax.swing.JPanel();
        buscarTxt = new javax.swing.JTextField();
        maxStockCombo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        trabajadorPanel = new javax.swing.JPanel();
        pnlUser = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        categoriesCombo = new javax.swing.JComboBox<>();
        bottomPanel = new javax.swing.JPanel();
        paginaSpin = new javax.swing.JSpinner();
        anadirProductoBtn = new javax.swing.JButton();
        pedidosBtn = new javax.swing.JButton();
        atrasBtn = new javax.swing.JButton();
        alanteBtn = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(233, 253, 253));
        setLayout(new java.awt.BorderLayout());

        topPanel.setOpaque(false);
        topPanel.setLayout(new java.awt.GridBagLayout());

        buscarTxt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        buscarTxt.setForeground(new java.awt.Color(150, 150, 150));
        buscarTxt.setText("Buscar");
        buscarTxt.setMaximumSize(new java.awt.Dimension(200, 31));
        buscarTxt.setMinimumSize(new java.awt.Dimension(200, 31));
        buscarTxt.setPreferredSize(new java.awt.Dimension(200, 31));
        buscarTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                buscarTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                buscarTxtFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 10);
        topPanel.add(buscarTxt, gridBagConstraints);

        maxStockCombo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        maxStockCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Max. Stock 10", "Max. Stock 25", "Max. Stock 50", "Max. Stock 100", "Max. Stock 200", "Max. Stock 500", "Todos" }));
        maxStockCombo.setSelectedIndex(6);
        maxStockCombo.setMaximumSize(new java.awt.Dimension(200, 31));
        maxStockCombo.setMinimumSize(new java.awt.Dimension(200, 31));
        maxStockCombo.setPreferredSize(new java.awt.Dimension(200, 31));
        maxStockCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                maxStockFilterChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 10);
        topPanel.add(maxStockCombo, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("INVENTARIO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        topPanel.add(jLabel3, gridBagConstraints);

        jPanel1.setMaximumSize(new java.awt.Dimension(400, 31));
        jPanel1.setMinimumSize(new java.awt.Dimension(400, 31));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 31));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        topPanel.add(jPanel1, gridBagConstraints);

        trabajadorPanel.setBackground(new java.awt.Color(204, 204, 204));
        trabajadorPanel.setPreferredSize(new java.awt.Dimension(200, 60));

        pnlUser.setBackground(new java.awt.Color(255, 255, 255));
        pnlUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlUser.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlUser.setPreferredSize(new java.awt.Dimension(161, 60));
        pnlUser.setLayout(new java.awt.GridBagLayout());

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/img_torrente.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Torrente Segura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Jefe Boqueron");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlUser.add(jLabel9, gridBagConstraints);

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
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 20);
        topPanel.add(trabajadorPanel, gridBagConstraints);

        jSeparator1.setMaximumSize(new java.awt.Dimension(200, 0));
        jSeparator1.setMinimumSize(new java.awt.Dimension(200, 0));
        jSeparator1.setPreferredSize(new java.awt.Dimension(200, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        topPanel.add(jSeparator1, gridBagConstraints);

        categoriesCombo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        categoriesCombo.setMaximumRowCount(10);
        categoriesCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));
        categoriesCombo.setMaximumSize(new java.awt.Dimension(200, 31));
        categoriesCombo.setMinimumSize(new java.awt.Dimension(200, 31));
        categoriesCombo.setPreferredSize(new java.awt.Dimension(200, 31));
        categoriesCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                categoryFilterChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        topPanel.add(categoriesCombo, gridBagConstraints);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new java.awt.GridBagLayout());

        paginaSpin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        paginaSpin.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        paginaSpin.setEnabled(false);
        paginaSpin.setPreferredSize(new java.awt.Dimension(100, 31));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        bottomPanel.add(paginaSpin, gridBagConstraints);

        anadirProductoBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        anadirProductoBtn.setText("Añadir Producto");
        anadirProductoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        anadirProductoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anadirProducto(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        bottomPanel.add(anadirProductoBtn, gridBagConstraints);

        pedidosBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        pedidosBtn.setText("Pedidos");
        pedidosBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 0);
        bottomPanel.add(pedidosBtn, gridBagConstraints);

        atrasBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_atras.png"))); // NOI18N
        atrasBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        atrasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retrocederPagina(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 5);
        bottomPanel.add(atrasBtn, gridBagConstraints);

        alanteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_alante.png"))); // NOI18N
        alanteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        alanteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasarPagina(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 20);
        bottomPanel.add(alanteBtn, gridBagConstraints);

        add(bottomPanel, java.awt.BorderLayout.PAGE_END);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.GridLayout(5, 4, 20, 20));
        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void buscarTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_buscarTxtFocusLost
        if (buscarTxt.getText().trim().isEmpty()) {
            buscarTxt.setText("Buscar");
            buscarTxt.setForeground(new java.awt.Color(150, 150, 150)); // texto placeholder gris
        }
    }//GEN-LAST:event_buscarTxtFocusLost

    private void buscarTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_buscarTxtFocusGained
        if (buscarTxt.getText().equals("Buscar")) {
            buscarTxt.setText("");
            buscarTxt.setForeground(new java.awt.Color(0, 0, 0)); // texto normal
        }
    }//GEN-LAST:event_buscarTxtFocusGained

    private void pasarPagina(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasarPagina
        int paginaActual = (Integer) paginaSpin.getValue();
        int totalItems = productosFiltrados.size();
        
        if (paginaActual * maxProdPagina < totalItems) {
            paginaSpin.setValue(paginaActual + 1);
            rellenarConProductos();
        }
    }//GEN-LAST:event_pasarPagina

    private void retrocederPagina(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retrocederPagina
        int paginaActual = (Integer) paginaSpin.getValue();
        
        if (paginaActual > 1) {
            paginaSpin.setValue(paginaActual - 1);
            rellenarConProductos();
        }
    }//GEN-LAST:event_retrocederPagina

    private void anadirProducto(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anadirProducto
        NuevoProducto nuevoProducto = new NuevoProducto(Inicio.getInstance(), true, categoriasLista, this);
        nuevoProducto.setVisible(true);
    }//GEN-LAST:event_anadirProducto

    private void maxStockFilterChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_maxStockFilterChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            aplicarFiltrosGlobales();
        }
    }//GEN-LAST:event_maxStockFilterChanged

    private void categoryFilterChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_categoryFilterChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            aplicarFiltrosGlobales();
        }
    }//GEN-LAST:event_categoryFilterChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton alanteBtn;
    private javax.swing.JButton anadirProductoBtn;
    private javax.swing.JButton atrasBtn;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JTextField buscarTxt;
    private javax.swing.JComboBox<String> categoriesCombo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> maxStockCombo;
    private javax.swing.JSpinner paginaSpin;
    private javax.swing.JButton pedidosBtn;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel trabajadorPanel;
    // End of variables declaration//GEN-END:variables
}
