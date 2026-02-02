-- --------------------------------------------------------
-- 1. INSERTAR CATEGORÍAS
-- --------------------------------------------------------
INSERT INTO Categoria (idCategoria, nombre) VALUES
(1, 'Frutas y Verduras'),
(2, 'Carnicería'),
(3, 'Pescadería'),
(4, 'Lácteos y Huevos'),
(5, 'Panadería y Pastelería'),
(6, 'Bebidas'),
(7, 'Limpieza del Hogar'),
(8, 'Higiene Personal'),
(9, 'Despensa y Ultramarinos'),
(10, 'Congelados'),
(11, 'Snacks y Dulces'),
(12, 'Productos Bio/Vegano');

-- --------------------------------------------------------
-- 2. INSERTAR PRODUCTOS
-- --------------------------------------------------------
INSERT INTO Producto (idProducto, nombre, precio, stock, minStock, Categoria_idCategoria) VALUES
-- Frutas y Verduras (ID 1)
(1, 'Manzana Golden (Kg)', 2.50, 100, 20, 1),
(2, 'Plátano de Canarias (Kg)', 1.95, 150, 30, 1),
(3, 'Lechuga Iceberg', 0.99, 50, 10, 1),
(4, 'Tomate Pera (Kg)', 1.80, 80, 15, 1),
(5, 'Patata Malla 5Kg', 4.50, 40, 5, 1),
-- Carnicería (ID 2)
(6, 'Pechuga de Pollo Fileteada (Kg)', 7.95, 30, 5, 2),
(7, 'Carne Picada Ternera (500g)', 4.20, 25, 5, 2),
(8, 'Chuletas de Cerdo (Kg)', 5.50, 20, 5, 2),
(9, 'Jamón Serrano Reserva (Sobres)', 3.50, 100, 20, 2),
-- Pescadería (ID 3)
(10, 'Boquerones Frescos (Kg)', 5.90, 40, 10, 3),
(11, 'Salmón Noruego (Kg)', 14.50, 15, 2, 3),
(12, 'Gambón Argentino (Caja 2Kg)', 18.00, 10, 2, 3),
(13, 'Merluza en Rodajas (Kg)', 9.90, 20, 5, 3),
-- Lácteos (ID 4)
(14, 'Leche Entera 1L', 0.95, 500, 50, 4),
(15, 'Leche Semidesnatada 1L', 0.95, 500, 50, 4),
(16, 'Yogur Natural Pack x4', 1.20, 80, 10, 4),
(17, 'Queso Curado Cuña (250g)', 4.50, 40, 5, 4),
(18, 'Mantequilla con Sal (250g)', 2.10, 30, 5, 4),
(19, 'Huevos Camperos Docena', 2.30, 60, 10, 4),
-- Panadería (ID 5)
(20, 'Barra de Pan Rústica', 0.80, 100, 10, 5),
(21, 'Croissant de Mantequilla', 1.10, 40, 5, 5),
(22, 'Pan de Molde Integral', 1.50, 60, 10, 5),
-- Bebidas (ID 6)
(23, 'Agua Mineral 1.5L', 0.60, 200, 40, 6),
(24, 'Coca Cola 2L', 1.85, 150, 20, 6),
(25, 'Cerveza Victoria (Lata)', 0.75, 300, 50, 6),
(26, 'Vino Tinto Rioja Crianza', 8.50, 40, 5, 6),
(27, 'Zumo de Naranja Exprimidor 1L', 1.99, 50, 10, 6),
-- Limpieza (ID 7)
(28, 'Detergente Líquido 3L', 7.95, 30, 5, 7),
(29, 'Lejía con Detergente 2L', 1.50, 50, 10, 7),
-- Higiene (ID 8)
(30, 'Papel Higiénico 12 Rollos', 4.95, 40, 5, 8),
(31, 'Gel de Ducha Familiar', 2.50, 35, 5, 8),
(32, 'Pasta de Dientes', 1.90, 45, 5, 8),
-- Despensa (ID 9)
(33, 'Aceite de Oliva Virgen Extra 1L', 8.90, 100, 20, 9),
(34, 'Arroz Redondo 1Kg', 1.30, 80, 15, 9),
(35, 'Macarrones 500g', 0.90, 90, 20, 9),
(36, 'Tomate Frito Brik', 0.70, 100, 20, 9),
(37, 'Atún en Aceite Pack x3', 3.20, 60, 10, 9),
-- Congelados (ID 10)
(38, 'Pizza 4 Quesos', 3.50, 40, 5, 10),
(39, 'Helado de Vainilla 1L', 4.50, 25, 5, 10),
(40, 'Guisantes Congelados 1Kg', 1.80, 30, 5, 10),
-- Snacks (ID 11)
(41, 'Patatas Fritas Bolsa Grande', 1.80, 50, 10, 11),
(42, 'Chocolate con Leche', 1.20, 60, 10, 11),
(43, 'Galletas María Paquete', 1.00, 80, 15, 11),
-- Bio/Vegano (ID 12)
(44, 'Leche de Almendras (Vegana)', 1.95, 40, 5, 12),
(45, 'Hamburguesa Vegana x2', 4.95, 20, 2, 12);