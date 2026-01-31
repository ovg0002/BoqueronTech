package com.boquerontech.supermercadoboqueron.informes.items;

import com.boquerontech.supermercadoboqueron.informes.modelo.VentaRow;
import com.boquerontech.supermercadoboqueron.informes.modelo.VentaRow;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;

public class ItemVenta extends JPanel {

    private JLabel lblProductos;
    private JLabel lblEstado;
    private JLabel lblFecha;
    private JLabel lblEmpleado;
    private JLabel lblPago;

    public ItemVenta() {
        // Inicializamos la interfaz manualmente
        initCustomComponents();
    }
    
    private void initCustomComponents() {
        // 1. Configuración del Panel
        this.setBackground(new Color(255, 255, 255));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(204, 204, 204)));
        this.setLayout(new GridLayout(1, 5, 10, 0)); // 1 fila, 5 columnas, espacio horizontal 10

        // 2. Inicializar Etiquetas
        lblProductos = new JLabel("Productos...");
        lblEstado = new JLabel("Realizado");
        lblFecha = new JLabel("00/00/0000");
        lblEmpleado = new JLabel("Empleado");
        lblPago = new JLabel("Pago");

        // 3. Añadir al panel en orden
        this.add(lblProductos);
        this.add(lblEstado);
        this.add(lblFecha);
        this.add(lblEmpleado);
        this.add(lblPago);
    }
    
    // Método para recibir los datos
    public void setDatos(VentaRow v) {
        if (v != null) {
            lblProductos.setText(v.getProductos());
            lblEstado.setText(v.getEstado());
            lblFecha.setText(v.getFecha());
            lblEmpleado.setText(v.getEmpleado());
            lblPago.setText(v.getMetodoPago());
        }
    }
}