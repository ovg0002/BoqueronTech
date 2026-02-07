/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.boquerontech.supermercadoboqueron.ventas;

import com.boquerontech.supermercadoboqueron.clientes.Cliente;
import com.boquerontech.supermercadoboqueron.database.cliente.ClienteDAO;
import com.boquerontech.supermercadoboqueron.database.producto.ProductoDAO;
import com.boquerontech.supermercadoboqueron.database.promocion.PromocionDAO;
import com.boquerontech.supermercadoboqueron.database.venta.VentaDAO;
import com.boquerontech.supermercadoboqueron.productos.Producto;
import com.boquerontech.supermercadoboqueron.promociones.Promocion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author velag
 */
public class Ventas extends javax.swing.JPanel {
    private final List<Producto> productosActivosLista;
    private Cliente currentClient = null;
    
    private final List<Producto> ventaActual = new ArrayList<>();
    private final Map<Producto, Integer> carrito = new LinkedHashMap();

    public Ventas() {
        initComponents();
        configurarEventosPago();
        
        productosActivosLista = ProductoDAO.getActiveOnlyProductsByMinCurrentStock(0);
    }

    /*
    Método que sirve para cambiar el método seleccionado que aparece en el ticket virtual por el método
    seleccionado, este método evita tener que hacer 3 métodos separados (uno por cada opción de pago)
    */
    private void configurarEventosPago() {
        // Creamos el comportamiento común para los botones (la acción que realizará al cambiar el botón)
        ActionListener listenerPago = (ActionEvent evt) -> {
            // Obtenemos el botón que ha sido pulsado
            JRadioButton botonSeleccionado = (JRadioButton) evt.getSource();
            
            // Actualizamos el label con el texto del botón (Ej: "Efectivo", "Bizum", etc.)
            metodoPagoLabel.setText("Método de pago: " + botonSeleccionado.getText());
        };

        // Asignamos este comportamiento a tus tres botones
        efectivoOpt.addActionListener(listenerPago);
        tarjetaOpt.addActionListener(listenerPago);
        bizumOpt.addActionListener(listenerPago);

        // Como el método seleccionado por defecto es Efectivo, se pone éste por defecto en el ticket virutal
        if (efectivoOpt.isSelected()) {
            metodoPagoLabel.setText("Método de pago: " + efectivoOpt.getText());
        }
    }
    
    private void repintarVenta() {
        DefaultTableModel tablaEscaneadosModel = (DefaultTableModel) tablaEscaneados.getModel();
        DefaultTableModel tablaTickectModel = (DefaultTableModel) tablaTicket.getModel();
        
        // 1. Limpiar tablas
        tablaEscaneadosModel.setRowCount(0);
        tablaTickectModel.setRowCount(0);
        
        double totalBaseSinDescuentos = 0.0;
        
        // 2. Recorrer el carrito
        for (Map.Entry<Producto, Integer> entrada : carrito.entrySet()) {
            Producto p = entrada.getKey();
            Integer cantidad = entrada.getValue();
            
            // Cálculos
            Double precioTotalBase = p.getPrecio() * cantidad;
            Double precioConDescuento = calcularMejorPrecioLinea(p, cantidad);
            
            totalBaseSinDescuentos += precioTotalBase;
            
            // 3. Rellenar Tabla de Escaneados (Izquierda)
            tablaEscaneadosModel.addRow(new Object[] {
                p.getNombre(), // Usamos nombre, no el objeto entero para que se vea texto
                p.getCategoria().getNombre(),
                cantidad,
                p.getPrecio()
            });
            
            // 4. Rellenar Tabla Ticket Virtual (Derecha)
            tablaTickectModel.addRow(new Object[] {
                p.getNombre(),
                p.getCategoria().getNombre(),
                cantidad,
                p.getPrecio(),
                precioTotalBase,      // Precio normal
                precioConDescuento    // Precio con promo (si hay)
            });
        }
        
        // 5. Actualizar los Labels de Totales en la pantalla
        Double totalFinal = calcularPrecioTotalTrasPromociones();
        
        totalEscaneadosLabel.setText("Total sin descuentos: " + String.format("%.2f €", totalBaseSinDescuentos));
        totalTicketVirtualLabel.setText("TOTAL A PAGAR: " + String.format("%.2f €", totalFinal));
        
        // Actualizar información del ticket virtual
        if(currentClient != null){
            atendidoLabel.setText("Cliente: " + currentClient.getNombre() + " (Puntos: " + currentClient.getPuntosCliente() + ")");
        } else {
            atendidoLabel.setText("Cliente: Anónimo");
        }
    }
    
    private Double calcularPrecioTotalTrasPromociones() {
        double totalAcumulado = 0.0;
        
        for (Map.Entry<Producto, Integer> entrada : carrito.entrySet()) {
            Producto p = entrada.getKey();
            Integer cantidad = entrada.getValue();
            
            // Sumamos el precio optimizado de esta línea
            totalAcumulado += calcularMejorPrecioLinea(p, cantidad);
        }
        
        return totalAcumulado;
    }

    private double calcularMejorPrecioLinea(Producto p, int cantidad) {
        // 1. Precio base (Sin promoción) -> El peor escenario
        double mejorPrecio = p.getPrecio() * cantidad;

        // 2. Traer promociones activas de este producto desde la BBDD
        // Usamos el DAO que ya tienes implementado
        List<Promocion> promos = PromocionDAO.getPromocionesPorProducto(p.getId());

        // 3. Evaluar cada promoción
        for (Promocion promo : promos) {
            int unidadesPack = promo.getUnidadesAfectadas(); // Ej: 3 (para un 3x2)
            double precioPromoUnitario = promo.getPrecioPorUnidad(); // El precio rebajado

            // Solo aplicamos si la cantidad comprada alcanza para al menos un pack
            if (unidadesPack > 0 && cantidad >= unidadesPack) {
                int numPacks = cantidad / unidadesPack; // Cuántos packs completos (Ej: 2 packs de 3)
                int resto = cantidad % unidadesPack;    // Unidades sueltas fuera de promo

                // Fórmula: (Packs * Unidades/Pack * PrecioRebajado) + (Sueltas * PrecioNormal)
                double precioConPromo = (numPacks * unidadesPack * precioPromoUnitario) + (resto * p.getPrecio());

                // Si esta promoción sale más barata que la anterior (o que el precio base), nos la quedamos
                if (precioConPromo < mejorPrecio) {
                    mejorPrecio = precioConPromo;
                }
            }
        }
        return mejorPrecio;
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

        metodosDePagoGroup = new javax.swing.ButtonGroup();
        cabecera = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        relleno = new javax.swing.JPanel();
        trabajadorPanel = new javax.swing.JPanel();
        pnlUser = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        bodyPanel = new javax.swing.JPanel();
        panelCodigoProd = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        codigoProductoTxt = new javax.swing.JTextField();
        anadirProductoBtn = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        mainPanel = new javax.swing.JPanel();
        listaLbl = new javax.swing.JLabel();
        usuarioLbl = new javax.swing.JLabel();
        usuarioPanel = new javax.swing.JPanel();
        clienteFoto = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nombreClienteTxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        telefonoTxt = new javax.swing.JTextField();
        enviarClienteBtn = new javax.swing.JButton();
        puntosLbl = new javax.swing.JLabel();
        prefijoTlfTxt = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        codigoClienteTxt = new javax.swing.JTextField();
        metodoPagoLbl = new javax.swing.JLabel();
        metodoPagoPanel = new javax.swing.JPanel();
        efectivoOpt = new javax.swing.JRadioButton();
        tarjetaOpt = new javax.swing.JRadioButton();
        bizumOpt = new javax.swing.JRadioButton();
        ticketVirtualLbl = new javax.swing.JLabel();
        finalizarBtn = new javax.swing.JButton();
        escaneadosPanel = new javax.swing.JPanel();
        bottomEscaneados = new javax.swing.JPanel();
        totalEscaneadosLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEscaneados = new javax.swing.JTable();
        ticketVirtualPanel = new javax.swing.JPanel();
        bottomTickVirt = new javax.swing.JPanel();
        totalTicketVirtualLabel = new javax.swing.JLabel();
        atendidoLabel = new javax.swing.JLabel();
        metodoPagoLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTicket = new javax.swing.JTable();

        setBackground(new java.awt.Color(233, 253, 253));
        setLayout(new java.awt.BorderLayout());

        cabecera.setOpaque(false);
        cabecera.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("VENTAS");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        cabecera.add(jLabel2, gridBagConstraints);

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
        cabecera.add(relleno, gridBagConstraints);

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
        cabecera.add(trabajadorPanel, gridBagConstraints);

        add(cabecera, java.awt.BorderLayout.PAGE_START);

        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(new java.awt.BorderLayout());

        panelCodigoProd.setMaximumSize(new java.awt.Dimension(654165165, 40));
        panelCodigoProd.setMinimumSize(new java.awt.Dimension(400, 40));
        panelCodigoProd.setOpaque(false);
        panelCodigoProd.setPreferredSize(new java.awt.Dimension(400, 40));
        panelCodigoProd.setLayout(new java.awt.GridBagLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_codigo_barras.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        panelCodigoProd.add(jLabel1, gridBagConstraints);

        codigoProductoTxt.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        codigoProductoTxt.setForeground(new java.awt.Color(150, 150, 150));
        codigoProductoTxt.setText("Código o nombre del producto");
        codigoProductoTxt.setMaximumSize(new java.awt.Dimension(300, 40));
        codigoProductoTxt.setMinimumSize(new java.awt.Dimension(300, 40));
        codigoProductoTxt.setPreferredSize(new java.awt.Dimension(300, 40));
        codigoProductoTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                codigoProductoTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                codigoProductoTxtFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panelCodigoProd.add(codigoProductoTxt, gridBagConstraints);

        anadirProductoBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        anadirProductoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_proximo.png"))); // NOI18N
        anadirProductoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        anadirProductoBtn.setMaximumSize(new java.awt.Dimension(40, 40));
        anadirProductoBtn.setMinimumSize(new java.awt.Dimension(40, 40));
        anadirProductoBtn.setPreferredSize(new java.awt.Dimension(40, 40));
        anadirProductoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anadirProductoAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panelCodigoProd.add(anadirProductoBtn, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCodigoProd.add(filler1, gridBagConstraints);

        bodyPanel.add(panelCodigoProd, java.awt.BorderLayout.PAGE_START);

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.GridBagLayout());

        listaLbl.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        listaLbl.setText("Lista de productos escaneados");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 10, 10);
        mainPanel.add(listaLbl, gridBagConstraints);

        usuarioLbl.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        usuarioLbl.setText("Ingresar Cliente (opcional)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 10);
        mainPanel.add(usuarioLbl, gridBagConstraints);

        usuarioPanel.setBackground(new java.awt.Color(255, 255, 255));
        usuarioPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        usuarioPanel.setMaximumSize(new java.awt.Dimension(300, 200));
        usuarioPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        usuarioPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        usuarioPanel.setLayout(new java.awt.GridBagLayout());

        clienteFoto.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        clienteFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_user.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        usuarioPanel.add(clienteFoto, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("Nombre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        usuarioPanel.add(jLabel3, gridBagConstraints);

        nombreClienteTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        nombreClienteTxt.setForeground(new java.awt.Color(150, 150, 150));
        nombreClienteTxt.setText("Nombre de cliente");
        nombreClienteTxt.setEnabled(false);
        nombreClienteTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nombreClienteTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nombreClienteTxtFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        usuarioPanel.add(nombreClienteTxt, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Teléfono");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        usuarioPanel.add(jLabel4, gridBagConstraints);

        telefonoTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        telefonoTxt.setForeground(new java.awt.Color(150, 150, 150));
        telefonoTxt.setText("612345678");
        telefonoTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                telefonoTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                telefonoTxtFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        usuarioPanel.add(telefonoTxt, gridBagConstraints);

        enviarClienteBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        enviarClienteBtn.setText("LogIn");
        enviarClienteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        enviarClienteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarClienteAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        usuarioPanel.add(enviarClienteBtn, gridBagConstraints);

        puntosLbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        puntosLbl.setText("Puntos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        usuarioPanel.add(puntosLbl, gridBagConstraints);

        prefijoTlfTxt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("+"))));
        prefijoTlfTxt.setText("+34");
        prefijoTlfTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        usuarioPanel.add(prefijoTlfTxt, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Código de Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        usuarioPanel.add(jLabel8, gridBagConstraints);

        codigoClienteTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        codigoClienteTxt.setForeground(new java.awt.Color(150, 150, 150));
        codigoClienteTxt.setText("Código de Cliente");
        codigoClienteTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                codigoClienteTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                codigoClienteTxtFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        usuarioPanel.add(codigoClienteTxt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        mainPanel.add(usuarioPanel, gridBagConstraints);

        metodoPagoLbl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        metodoPagoLbl.setText("Método de pago");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        mainPanel.add(metodoPagoLbl, gridBagConstraints);

        metodoPagoPanel.setBackground(new java.awt.Color(255, 255, 255));
        metodoPagoPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        metodoPagoPanel.setMaximumSize(new java.awt.Dimension(300, 200));
        metodoPagoPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        metodoPagoPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        metodoPagoPanel.setLayout(new java.awt.GridBagLayout());

        metodosDePagoGroup.add(efectivoOpt);
        efectivoOpt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        efectivoOpt.setSelected(true);
        efectivoOpt.setText("Efectivo");
        efectivoOpt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        metodoPagoPanel.add(efectivoOpt, gridBagConstraints);

        metodosDePagoGroup.add(tarjetaOpt);
        tarjetaOpt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tarjetaOpt.setText("Tarjeta de Crédito");
        tarjetaOpt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        metodoPagoPanel.add(tarjetaOpt, gridBagConstraints);

        metodosDePagoGroup.add(bizumOpt);
        bizumOpt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bizumOpt.setText("Bizum");
        bizumOpt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        metodoPagoPanel.add(bizumOpt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 10);
        mainPanel.add(metodoPagoPanel, gridBagConstraints);

        ticketVirtualLbl.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        ticketVirtualLbl.setText("Ticket virtual con promociones aplicadas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 20);
        mainPanel.add(ticketVirtualLbl, gridBagConstraints);

        finalizarBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        finalizarBtn.setText("Finalizar Compra");
        finalizarBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        finalizarBtn.setMaximumSize(new java.awt.Dimension(200, 40));
        finalizarBtn.setMinimumSize(new java.awt.Dimension(200, 40));
        finalizarBtn.setPreferredSize(new java.awt.Dimension(200, 40));
        finalizarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalizarCompraAction(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 20);
        mainPanel.add(finalizarBtn, gridBagConstraints);

        escaneadosPanel.setBackground(new java.awt.Color(255, 255, 255));
        escaneadosPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        escaneadosPanel.setPreferredSize(new java.awt.Dimension(598, 683));
        escaneadosPanel.setLayout(new java.awt.BorderLayout());

        bottomEscaneados.setOpaque(false);
        bottomEscaneados.setPreferredSize(new java.awt.Dimension(526, 100));
        bottomEscaneados.setLayout(new java.awt.GridBagLayout());

        totalEscaneadosLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        totalEscaneadosLabel.setText("Total de la compra = {total}");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        bottomEscaneados.add(totalEscaneadosLabel, gridBagConstraints);

        escaneadosPanel.add(bottomEscaneados, java.awt.BorderLayout.PAGE_END);

        tablaEscaneados.setBackground(new java.awt.Color(255, 255, 255));
        tablaEscaneados.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaEscaneados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Producto", "Categoria", "Cantidad", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaEscaneados.setToolTipText("");
        jScrollPane1.setViewportView(tablaEscaneados);

        escaneadosPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 10);
        mainPanel.add(escaneadosPanel, gridBagConstraints);

        ticketVirtualPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        ticketVirtualPanel.setLayout(new java.awt.BorderLayout());

        bottomTickVirt.setBackground(new java.awt.Color(255, 255, 255));
        bottomTickVirt.setPreferredSize(new java.awt.Dimension(526, 100));
        bottomTickVirt.setLayout(new java.awt.GridBagLayout());

        totalTicketVirtualLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        totalTicketVirtualLabel.setText("Total de la compra = {total}");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 5, 20);
        bottomTickVirt.add(totalTicketVirtualLabel, gridBagConstraints);

        atendidoLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        atendidoLabel.setText("Atendido por: {emp}");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 20, 10);
        bottomTickVirt.add(atendidoLabel, gridBagConstraints);

        metodoPagoLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        metodoPagoLabel.setText("Método de pago: {met}");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 20, 20);
        bottomTickVirt.add(metodoPagoLabel, gridBagConstraints);

        ticketVirtualPanel.add(bottomTickVirt, java.awt.BorderLayout.PAGE_END);

        tablaTicket.setBackground(new java.awt.Color(255, 255, 255));
        tablaTicket.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaTicket.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Categoría", "Cantidad", "Precio unitario", "Precio total", "Precio con promociones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaTicket);

        ticketVirtualPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 20, 20);
        mainPanel.add(ticketVirtualPanel, gridBagConstraints);

        bodyPanel.add(mainPanel, java.awt.BorderLayout.CENTER);

        add(bodyPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private void codigoProductoTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codigoProductoTxtFocusGained
        if (codigoProductoTxt.getText().equals("Código o nombre del producto")) {
            codigoProductoTxt.setText("");
            codigoProductoTxt.setForeground(new java.awt.Color(0, 0, 0)); // texto normal
        }
    }//GEN-LAST:event_codigoProductoTxtFocusGained

    private void codigoProductoTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codigoProductoTxtFocusLost
        if (codigoProductoTxt.getText().trim().isEmpty()) {
            codigoProductoTxt.setText("Código o nombre del producto");
            codigoProductoTxt.setForeground(new java.awt.Color(150, 150, 150)); // texto placeholder gris
        }
    }//GEN-LAST:event_codigoProductoTxtFocusLost

    private void nombreClienteTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nombreClienteTxtFocusGained
        if (nombreClienteTxt.getText().equals("Nombre de cliente")) {
            nombreClienteTxt.setText("");
            nombreClienteTxt.setForeground(new java.awt.Color(0, 0, 0)); // texto normal
        }
    }//GEN-LAST:event_nombreClienteTxtFocusGained

    private void nombreClienteTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nombreClienteTxtFocusLost
        if (nombreClienteTxt.getText().trim().isEmpty()) {
            nombreClienteTxt.setText("Nombre de cliente");
            nombreClienteTxt.setForeground(new java.awt.Color(150, 150, 150)); // texto placeholder gris
        }
    }//GEN-LAST:event_nombreClienteTxtFocusLost

    private void telefonoTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_telefonoTxtFocusGained
        if (telefonoTxt.getText().equals("612345678")) {
            telefonoTxt.setText("");
            telefonoTxt.setForeground(new java.awt.Color(0, 0, 0)); // texto normal
        }
    }//GEN-LAST:event_telefonoTxtFocusGained

    private void telefonoTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_telefonoTxtFocusLost
        if (telefonoTxt.getText().trim().isEmpty()) {
            telefonoTxt.setText("612345678");
            telefonoTxt.setForeground(new java.awt.Color(150, 150, 150)); // texto placeholder gris
        }
    }//GEN-LAST:event_telefonoTxtFocusLost

    private void codigoClienteTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codigoClienteTxtFocusGained
        if (codigoClienteTxt.getText().equals("Código de Cliente")) {
            codigoClienteTxt.setText("");
            codigoClienteTxt.setForeground(new java.awt.Color(0, 0, 0)); // texto normal
        }
    }//GEN-LAST:event_codigoClienteTxtFocusGained

    private void codigoClienteTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codigoClienteTxtFocusLost
        if (codigoClienteTxt.getText().trim().isEmpty()) {
            codigoClienteTxt.setText("Código de Cliente");
            codigoClienteTxt.setForeground(new java.awt.Color(150, 150, 150)); // texto placeholder gris
        }
    }//GEN-LAST:event_codigoClienteTxtFocusLost

    private void enviarClienteAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarClienteAction
        String codigoCliente = codigoClienteTxt.getText().trim();
        String telefonoCliente = telefonoTxt.getText().trim();
        
        if (codigoCliente != null && !codigoCliente.isEmpty() && !codigoCliente.equals("Código de Cliente")) {
            Cliente cliente = ClienteDAO.getClienteByCodigoCliente(codigoCliente);
            checkCliente(cliente, "código de cliente");
            currentClient = cliente;
            
        } else if (telefonoCliente != null && !telefonoCliente.isEmpty() && !codigoCliente.equals("612345678")) {
            Cliente cliente = ClienteDAO.getClienteByTelefono(
                prefijoTlfTxt.getText().trim() + " " + telefonoCliente
            );
            checkCliente(cliente, "número de teléfono");
            currentClient = cliente;
        }
    }//GEN-LAST:event_enviarClienteAction

    private void checkCliente(Cliente cliente, String msg) {
        if (cliente != null) {
            puntosLbl.setText("Puntos: " + cliente.getPuntosCliente());
            
            nombreClienteTxt.setText(cliente.getNombre() + " " + cliente.getApellidos());
            
            codigoClienteTxt.setText(cliente.getCodigoCliente());
            codigoClienteTxt.setForeground(new java.awt.Color(0, 0, 0));
            
            String[] telefono = cliente.getTelefono().split(" ");
            String prefijo = telefono[0];
            String numero = telefono[1];
            prefijoTlfTxt.setText(prefijo);
            telefonoTxt.setText(numero);
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se ha encontrado un cliente con dicho " + msg + ".",
                "Error de cliente",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void anadirProductoAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anadirProductoAction
        String prodBuscar = codigoProductoTxt.getText().trim();
        
        if (prodBuscar != null && !prodBuscar.isEmpty() && !prodBuscar.equals("Código o nombre del producto")) {
            Producto[] productos = getProductosCodigoNombre(prodBuscar);
            if (productos == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "No se encontró ningún producto relativo a \"" + prodBuscar + "\".",
                    "No se encontró",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            Object seleccionado = JOptionPane.showInputDialog(
                this,
                "Selecciona un producto.",
                "Elección de producto",
                JOptionPane.QUESTION_MESSAGE,
                null,
                productos,
                productos[0]
            );
            
            if (seleccionado != null) {
                //ventaActual.add((Producto) seleccionado);
                carrito.merge((Producto) seleccionado, 1, Integer::sum);
                repintarVenta();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No ha seleccionado ningún producto.",
                    "No se seleccionó producto",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }//GEN-LAST:event_anadirProductoAction

    private Producto[] getProductosCodigoNombre(String datoProd) {
        if (datoProd != null && !datoProd.isEmpty()) {
            int aux = 0;
            Producto[] prods = new Producto[ProductoDAO.contarProductosTotales()];
            String dato = datoProd.toLowerCase();
            for (Producto p : productosActivosLista) {
                if (p.getNombre().toLowerCase().contains(dato) || p.getCodigoProducto().toLowerCase().contains(dato)) {
                    prods[aux] = p;
                    aux++;
                }
            }
            if (prods != null) return prods;
            else return null;
        } else return null;
    }
    
    private void finalizarCompraAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalizarCompraAction
        // 1. Validaciones
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "El carrito está vacío.",
                "Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (currentClient == null) {
            JOptionPane.showMessageDialog(
                this,
                "Debe seleccionar un cliente.",
                "Cliente requerido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Calcular Total Final (Con descuentos aplicados)
        Double totalVenta = calcularPrecioTotalTrasPromociones();

        // 3. Confirmación
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Total a pagar: " + totalVenta + "\n¿Finalizar venta?", 
            "Confirmar Venta",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // 4. Preparar datos
        String metodoPago = "Efectivo";
        if (tarjetaOpt.isSelected()) metodoPago = "Tarjeta";
        else if (bizumOpt.isSelected()) metodoPago = "Bizum";

        // TODO: Usar el usuario logueado real
        int idEmpleado = 1; 

        Venta nuevaVenta = Venta.builder()
            .fecha(java.time.LocalDate.now())
            .metodoPago(metodoPago)
            .idEmpleado(idEmpleado)
            .idCliente(currentClient.getIdCliente())
            .build();

        // 5. Llamar al DAO pasando también el total para los puntos
        boolean exito = VentaDAO.registrarVentaCompleta(nuevaVenta, carrito, totalVenta);

        if (exito) {
            int puntosGanados = (int) (totalVenta * 100);
            String mensaje = String.format("¡Venta registrada!\n\nCliente: %s\nPuntos ganados: %d", 
                    currentClient.getNombre(), puntosGanados);
            
            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Venta Finalizada",
                JOptionPane.INFORMATION_MESSAGE
            );
            resetearVenta();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Error crítico al registrar la venta.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_finalizarCompraAction

    private void resetearVenta() {
        // 1. Limpiar datos lógicos
        carrito.clear();
        currentClient = null;
        
        // 2. Limpiar UI de Cliente
        nombreClienteTxt.setText("Nombre de cliente");
        nombreClienteTxt.setForeground(new java.awt.Color(150, 150, 150));
        
        codigoClienteTxt.setText("Código de Cliente");
        codigoClienteTxt.setForeground(new java.awt.Color(150, 150, 150));
        
        prefijoTlfTxt.setText("+34");
        telefonoTxt.setText("612345678");
        telefonoTxt.setForeground(new java.awt.Color(150, 150, 150));
        
        puntosLbl.setText("Puntos:");
        
        // 3. Limpiar UI de Producto
        codigoProductoTxt.setText("Código o nombre del producto");
        codigoProductoTxt.setForeground(new java.awt.Color(150, 150, 150));
        
        // 4. Resetear Opciones de Pago
        efectivoOpt.setSelected(true);
        metodoPagoLabel.setText("Método de pago: Efectivo");
        
        // 5. Repintar tablas (se quedarán vacías porque el carrito está empty)
        repintarVenta();
        
        // 6. Focus al inicio
        codigoProductoTxt.requestFocus();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton anadirProductoBtn;
    private javax.swing.JLabel atendidoLabel;
    private javax.swing.JRadioButton bizumOpt;
    private javax.swing.JPanel bodyPanel;
    private javax.swing.JPanel bottomEscaneados;
    private javax.swing.JPanel bottomTickVirt;
    private javax.swing.JPanel cabecera;
    private javax.swing.JLabel clienteFoto;
    private javax.swing.JTextField codigoClienteTxt;
    private javax.swing.JTextField codigoProductoTxt;
    private javax.swing.JRadioButton efectivoOpt;
    private javax.swing.JButton enviarClienteBtn;
    private javax.swing.JPanel escaneadosPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton finalizarBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel listaLbl;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel metodoPagoLabel;
    private javax.swing.JLabel metodoPagoLbl;
    private javax.swing.JPanel metodoPagoPanel;
    private javax.swing.ButtonGroup metodosDePagoGroup;
    private javax.swing.JTextField nombreClienteTxt;
    private javax.swing.JPanel panelCodigoProd;
    private javax.swing.JPanel pnlUser;
    private javax.swing.JFormattedTextField prefijoTlfTxt;
    private javax.swing.JLabel puntosLbl;
    private javax.swing.JPanel relleno;
    private javax.swing.JTable tablaEscaneados;
    private javax.swing.JTable tablaTicket;
    private javax.swing.JRadioButton tarjetaOpt;
    private javax.swing.JTextField telefonoTxt;
    private javax.swing.JLabel ticketVirtualLbl;
    private javax.swing.JPanel ticketVirtualPanel;
    private javax.swing.JLabel totalEscaneadosLabel;
    private javax.swing.JLabel totalTicketVirtualLabel;
    private javax.swing.JPanel trabajadorPanel;
    private javax.swing.JLabel usuarioLbl;
    private javax.swing.JPanel usuarioPanel;
    // End of variables declaration//GEN-END:variables
}
