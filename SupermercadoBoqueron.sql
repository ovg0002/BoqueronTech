-- MySQL Workbench Forward Engineering
-- Contraseña root: superboqueron1234

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- IMPORTANTE: Los números de teléfono deben seguir esta estructura: "<prefijo> <número>", por ejemplo: "+34 612345678". Es decir, el prefijo debe
	-- empezar por '+' y entre el prefijo y el número de teléfono debe haber un espacio

-- IMPORTANTE: los código de cliente/empleado/lo que sea son tal que asi: "XXXXAAA",
	-- donde las X son los 4 primeros números del dni y las A son las 3 primeras letras del nombre

-- -----------------------------------------------------
-- Schema superboqueron
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `superboqueron`;
CREATE SCHEMA IF NOT EXISTS `superboqueron` DEFAULT CHARACTER SET utf8 ;
USE `superboqueron` ;

-- -----------------------------------------------------
-- Table `superboqueron`.`Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Cliente` (
  `idCliente` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(255) NOT NULL,
  `fechaNacimiento` DATE NULL,
  `telefono` VARCHAR(20) NOT NULL,
  `dni` VARCHAR(9) NOT NULL,
  `puntosCliente` INT NULL DEFAULT 0,
  `codigoCliente` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`idCliente`),
  UNIQUE INDEX `telefono_UNIQUE` (`telefono` ASC) VISIBLE,
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  UNIQUE INDEX `codigoCliente_UNIQUE` (`codigoCliente` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Empleado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Empleado` (
  `idEmpleado` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(255) NOT NULL,
  `fechaNacimiento` DATE NOT NULL,
  `fechaIncorporacion` DATE NOT NULL,
  `telefono` VARCHAR(20) NOT NULL,
  `dni` VARCHAR(9) NOT NULL,
  `sueldo` DECIMAL(8,2) NOT NULL,
  `codigoEmpleado` VARCHAR(64) NOT NULL,
  `contrasenia` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`idEmpleado`),
  UNIQUE INDEX `telefono_UNIQUE` (`telefono` ASC) VISIBLE,
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  UNIQUE INDEX `codigoEmpleado_UNIQUE` (`codigoEmpleado` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Proveedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Proveedor` (
  `idProveedor` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(295) NOT NULL,
  `email` VARCHAR(128) NOT NULL,
  `telefono` VARCHAR(20) NOT NULL,
  `direccion` VARCHAR(1024) NULL,
  `codigoProveedor` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`idProveedor`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `telefono_UNIQUE` (`telefono` ASC) VISIBLE,
  UNIQUE INDEX `codigoProveedor_UNIQUE` (`codigoProveedor` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Categoria` (
  `idCategoria` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`idCategoria`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Producto` (
  `idProducto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(512) NOT NULL,
  `precio` DECIMAL(8,2) NOT NULL,
  `stock` INT NULL DEFAULT 0,
  `minStock` INT NULL DEFAULT 0,
  `Categoria_idCategoria` INT NOT NULL,
  `activo` TINYINT NOT NULL DEFAULT 1,
  `codigoProducto` VARCHAR(64) NULL, 
  PRIMARY KEY (`idProducto`),
  INDEX `fk_Producto_Categoria1_idx` (`Categoria_idCategoria` ASC) VISIBLE,
  CONSTRAINT `fk_Producto_Categoria1`
    FOREIGN KEY (`Categoria_idCategoria`)
    REFERENCES `superboqueron`.`Categoria` (`idCategoria`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Pedidos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Pedidos` (
  `idPedidos` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `Proveedor_idProveedor` INT NOT NULL,
  PRIMARY KEY (`idPedidos`),
  INDEX `fk_Pedidos_Proveedor1_idx` (`Proveedor_idProveedor` ASC) VISIBLE,
  CONSTRAINT `fk_Pedidos_Proveedor1`
    FOREIGN KEY (`Proveedor_idProveedor`)
    REFERENCES `superboqueron`.`Proveedor` (`idProveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Venta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Venta` (
  `idVenta` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `metodoPago` VARCHAR(45) NULL DEFAULT 'Efectivo',
  `Empleado_idEmpleado` INT NOT NULL,
  `Cliente_idCliente` INT NOT NULL,
  PRIMARY KEY (`idVenta`),
  INDEX `fk_Venta_Empleado1_idx` (`Empleado_idEmpleado` ASC) VISIBLE,
  INDEX `fk_Venta_Cliente1_idx` (`Cliente_idCliente` ASC) VISIBLE,
  CONSTRAINT `fk_Venta_Empleado1`
    FOREIGN KEY (`Empleado_idEmpleado`)
    REFERENCES `superboqueron`.`Empleado` (`idEmpleado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Venta_Cliente1`
    FOREIGN KEY (`Cliente_idCliente`)
    REFERENCES `superboqueron`.`Cliente` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Promociones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Promociones` (
  `idPromociones` INT NOT NULL AUTO_INCREMENT,
  `nombrePromocion` VARCHAR(256) NOT NULL,
  `unidadesAfectadas` INT NOT NULL,
  `precioPorUnidad` DECIMAL(8,2) NOT NULL,
  `fechaInicio` DATE NOT NULL,
  `fechaFin` DATE NULL,
  `aplicableA` VARCHAR(255) NULL,
  `reglas` VARCHAR(512) NULL,
  `Categoria_idCategoria` INT NOT NULL,
  PRIMARY KEY (`idPromociones`),
  INDEX `fk_Promociones_Categoria1_idx` (`Categoria_idCategoria` ASC) VISIBLE,
  CONSTRAINT `fk_Promociones_Categoria1`
    FOREIGN KEY (`Categoria_idCategoria`)
    REFERENCES `superboqueron`.`Categoria` (`idCategoria`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Proveedor_has_Producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Proveedor_has_Producto` (
  `Proveedor_idProveedor` INT NOT NULL,
  `Producto_idProducto` INT NOT NULL,
  PRIMARY KEY (`Proveedor_idProveedor`, `Producto_idProducto`),
  INDEX `fk_Proveedor_has_Producto_Producto1_idx` (`Producto_idProducto` ASC) VISIBLE,
  INDEX `fk_Proveedor_has_Producto_Proveedor1_idx` (`Proveedor_idProveedor` ASC) VISIBLE,
  CONSTRAINT `fk_Proveedor_has_Producto_Proveedor1`
    FOREIGN KEY (`Proveedor_idProveedor`)
    REFERENCES `superboqueron`.`Proveedor` (`idProveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Proveedor_has_Producto_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `superboqueron`.`Producto` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`DetallePedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`DetallePedido` (
  `Producto_idProducto` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `precioCompraUnitario` DECIMAL(8,2) NOT NULL,
  `Pedidos_idPedidos` INT NOT NULL,
  PRIMARY KEY (`Producto_idProducto`, `Pedidos_idPedidos`),
  INDEX `fk_DetallePedido_Producto1_idx` (`Producto_idProducto` ASC) VISIBLE,
  INDEX `fk_DetallePedido_Pedidos1_idx` (`Pedidos_idPedidos` ASC) VISIBLE,
  CONSTRAINT `fk_DetallePedido_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `superboqueron`.`Producto` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_DetallePedido_Pedidos1`
    FOREIGN KEY (`Pedidos_idPedidos`)
    REFERENCES `superboqueron`.`Pedidos` (`idPedidos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`Promociones_has_Producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Promociones_has_Producto` (
  `Promociones_idPromociones` INT NOT NULL,
  `Producto_idProducto` INT NOT NULL,
  PRIMARY KEY (`Promociones_idPromociones`, `Producto_idProducto`),
  INDEX `fk_Promociones_has_Producto_Producto1_idx` (`Producto_idProducto` ASC) VISIBLE,
  INDEX `fk_Promociones_has_Producto_Promociones1_idx` (`Promociones_idPromociones` ASC) VISIBLE,
  CONSTRAINT `fk_Promociones_has_Producto_Promociones1`
    FOREIGN KEY (`Promociones_idPromociones`)
    REFERENCES `superboqueron`.`Promociones` (`idPromociones`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Promociones_has_Producto_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `superboqueron`.`Producto` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`DetalleVenta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`DetalleVenta` (
  `Producto_idProducto` INT NOT NULL,
  `Venta_idVenta` INT NOT NULL,
  `precioUnitarioHistorico` DECIMAL(8,2) NOT NULL,
  `cantidad` INT NOT NULL,
  PRIMARY KEY (`Producto_idProducto`, `Venta_idVenta`),
  INDEX `fk_Producto_has_Venta_Venta1_idx` (`Venta_idVenta` ASC) VISIBLE,
  INDEX `fk_Producto_has_Venta_Producto1_idx` (`Producto_idProducto` ASC) VISIBLE,
  CONSTRAINT `fk_Producto_has_Venta_Producto1`
    FOREIGN KEY (`Producto_idProducto`)
    REFERENCES `superboqueron`.`Producto` (`idProducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Producto_has_Venta_Venta1`
    FOREIGN KEY (`Venta_idVenta`)
    REFERENCES `superboqueron`.`Venta` (`idVenta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `superboqueron`.`CierreCaja`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`CierreCaja` (
  `idCierre` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `validacion` VARCHAR(45) NULL,
  `incidencia` VARCHAR(512) NULL,
  `Empleado_idEmpleado` INT NOT NULL,
  PRIMARY KEY (`idCierre`),
  INDEX `fk_CierreCaja_Empleado1_idx` (`Empleado_idEmpleado` ASC) VISIBLE,
  CONSTRAINT `fk_CierreCaja_Empleado1`
    FOREIGN KEY (`Empleado_idEmpleado`)
    REFERENCES `superboqueron`.`Empleado` (`idEmpleado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- ========================================================
-- INSERCIONES DE DATOS
-- ========================================================

-- 1. CATEGORIAS
INSERT INTO Categoria (idCategoria, nombre) VALUES
(1, 'Frutas y Verduras'), (2, 'Carnicería'), (3, 'Pescadería'),
(4, 'Lácteos y Huevos'), (5, 'Panadería y Pastelería'), (6, 'Bebidas'),
(7, 'Limpieza del Hogar'), (8, 'Higiene Personal'), (9, 'Despensa y Ultramarinos'),
(10, 'Congelados'), (11, 'Snacks y Dulces'), (12, 'Productos Bio/Vegano');

-- 2. PRODUCTOS
INSERT INTO Producto (idProducto, nombre, precio, stock, minStock, Categoria_idCategoria) VALUES
(1, 'Manzana Golden (Kg)', 2.50, 100, 20, 1),
(2, 'Plátano de Canarias (Kg)', 1.95, 150, 30, 1),
(3, 'Lechuga Iceberg', 0.99, 50, 10, 1),
(4, 'Tomate Pera (Kg)', 1.80, 80, 15, 1),
(5, 'Patata Malla 5Kg', 4.50, 40, 5, 1),
(6, 'Pechuga de Pollo Fileteada (Kg)', 7.95, 30, 5, 2),
(7, 'Carne Picada Ternera (500g)', 4.20, 25, 5, 2),
(8, 'Chuletas de Cerdo (Kg)', 5.50, 20, 5, 2),
(9, 'Jamón Serrano Reserva (Sobres)', 3.50, 100, 20, 2),
(10, 'Boquerones Frescos (Kg)', 5.90, 40, 10, 3),
(11, 'Salmón Noruego (Kg)', 14.50, 15, 2, 3),
(12, 'Gambón Argentino (Caja 2Kg)', 18.00, 10, 2, 3),
(13, 'Merluza en Rodajas (Kg)', 9.90, 20, 5, 3),
(14, 'Leche Entera 1L', 0.95, 500, 50, 4),
(15, 'Leche Semidesnatada 1L', 0.95, 500, 50, 4),
(16, 'Yogur Natural Pack x4', 1.20, 80, 10, 4),
(17, 'Queso Curado Cuña (250g)', 4.50, 40, 5, 4),
(18, 'Mantequilla con Sal (250g)', 2.10, 30, 5, 4),
(19, 'Huevos Camperos Docena', 2.30, 60, 10, 4),
(20, 'Barra de Pan Rústica', 0.80, 100, 10, 5),
(21, 'Croissant de Mantequilla', 1.10, 40, 5, 5),
(22, 'Pan de Molde Integral', 1.50, 60, 10, 5),
(23, 'Agua Mineral 1.5L', 0.60, 200, 40, 6),
(24, 'Coca Cola 2L', 1.85, 150, 20, 6),
(25, 'Cerveza Victoria (Lata)', 0.75, 300, 50, 6),
(26, 'Vino Tinto Rioja Crianza', 8.50, 40, 5, 6),
(27, 'Zumo de Naranja Exprimidor 1L', 1.99, 50, 10, 6),
(28, 'Detergente Líquido 3L', 7.95, 30, 5, 7),
(29, 'Lejía con Detergente 2L', 1.50, 50, 10, 7),
(30, 'Papel Higiénico 12 Rollos', 4.95, 40, 5, 8),
(31, 'Gel de Ducha Familiar', 2.50, 35, 5, 8),
(32, 'Pasta de Dientes', 1.90, 45, 5, 8),
(33, 'Aceite de Oliva Virgen Extra 1L', 8.90, 100, 20, 9),
(34, 'Arroz Redondo 1Kg', 1.30, 80, 15, 9),
(35, 'Macarrones 500g', 0.90, 90, 20, 9),
(36, 'Tomate Frito Brik', 0.70, 100, 20, 9),
(37, 'Atún en Aceite Pack x3', 3.20, 60, 10, 9),
(38, 'Pizza 4 Quesos', 3.50, 40, 5, 10),
(39, 'Helado de Vainilla 1L', 4.50, 25, 5, 10),
(40, 'Guisantes Congelados 1Kg', 1.80, 30, 5, 10),
(41, 'Patatas Fritas Bolsa Grande', 1.80, 50, 10, 11),
(42, 'Chocolate con Leche', 1.20, 60, 10, 11),
(43, 'Galletas María Paquete', 1.00, 80, 15, 11),
(44, 'Leche de Almendras (Vegana)', 1.95, 40, 5, 12),
(45, 'Hamburguesa Vegana x2', 4.95, 20, 2, 12);

-- 3. EMPLEADOS
INSERT INTO Empleado (nombre, apellidos, fechaNacimiento, fechaIncorporacion, telefono, dni, sueldo, codigoEmpleado, contrasenia) VALUES 
('Oscar', 'Velázquez', '1995-05-15', '2022-01-10', '+34 600111001', '12345678A', 1500.00, '1234OSC', 'oscar1234'),
('David', 'García', '1998-11-20', '2023-03-15', '+34 600222002', '87654321B', 1450.50, '8765DAV', 'david1234'),
('Adrian', 'López', '1992-07-08', '2021-06-01', '+34 600333003', '11223344C', 1600.00, '1122ADR', 'adrian1234'),
('Jaime', 'Pérez', '1990-02-28', '2020-09-20', '+34 600444004', '44332211D', 1550.75, '4433JAI', 'jaime1234'),
('José Luis', 'Torrente', '1965-03-13', '1998-03-13', '+34 666999666', '99999999T', 2500.00, '9999JOS', 'admin');

-- 4. CLIENTES
INSERT INTO Cliente (nombre, apellidos, fechaNacimiento, telefono, dni, puntosCliente, codigoCliente) VALUES
('María', 'González Pérez', '1985-04-12', '+34 611222333', '11112222X', 150, '1111MAR'),
('Juan', 'Martínez Soria', '1970-08-23', '+34 622333444', '22223333Y', 500, '2222JUA'),
('Laura', 'Ruiz Jiménez', '1999-12-01', '+34 633444555', '33334444Z', 20, '3333LAU'),
('Carlos', 'Fernández Mota', '1960-02-14', '+34 644555666', '44445555H', 0, '4444CAR'),
('Ana', 'Belén Torres', '1992-06-30', '+34 655666777', '55556666J', 1200, '5555ANA'),
('Pedro', 'Almodóvar', '1955-09-25', '+34 666777888', '66667777K', 50, '6666PED'),
('Sofía', 'Vergara', '1980-01-10', '+34 677888999', '77778888L', 300, '7777SOF'),
('Antonio', 'Banderas', '1960-08-10', '+34 688999000', '88889999M', 10, '8888ANT'),
('Penélope', 'Cruz', '1974-04-28', '+34 699000111', '99990000N', 750, '9999PEN'),
('Javier', 'Bardem', '1969-03-01', '+34 600123456', '00001111P', 45, '0000JAV'),
('Elsa', 'Pataky', '1976-07-18', '+34 611234567', '12121212Q', 90, '1212ELS'),
('Mario', 'Casas', '1986-06-12', '+34 622345678', '13131313R', 100, '1313MAR'),
('Blanca', 'Suárez', '1988-10-21', '+34 633456789', '14141414S', 5, '1414BLA'),
('Úrsula', 'Corberó', '1989-08-11', '+34 644567890', '15151515T', 66, '1515URS'),
('Miguel', 'Ángel Silvestre', '1982-04-06', '+34 655678901', '16161616V', 200, '1616MIG');

-- 5. PROVEEDORES
INSERT INTO Proveedor (nombre, email, telefono, direccion, codigoProveedor) VALUES
('Huerta Fresca S.L.', 'pedidos@huertafresca.com', '+34 911222333', 'Polígono Mercamalaga', 'PROV_FRE'),
('Bebidas del Sur', 'contacto@bebidassur.es', '+34 952111222', 'Calle Larios 15', 'PROV_BEB'),
('Limpiezas Paco', 'paco@limpiezas.com', '+34 956333444', 'Avda. Velázquez 89', 'PROV_LIM'),
('Panadería El Espigo', 'info@elespigo.com', '+34 954000111', 'Calle Harina 2', 'PROV_PAN'),
('Carnes y Embutidos Manolo', 'manolo@carnes.com', '+34 912333444', 'Polígono Industrial Norte', 'PROV_CAR'),
('Congelados del Ártico', 'frio@artico.com', '+34 981222999', 'Puerto de la Coruña', 'PROV_CON');

-- 6. RELACIÓN PROVEEDOR-PRODUCTO
INSERT INTO Proveedor_has_Producto (Proveedor_idProveedor, Producto_idProducto) VALUES
(1, 1), (1, 3), (1, 19), (1, 4), (1, 2), 
(2, 23), (2, 24), (2, 25), (2, 27), (2, 26), 
(3, 28), (3, 29), (3, 30), (3, 31), (3, 32), 
(4, 20), (4, 21), (4, 22), (4, 43), (4, 41), 
(5, 6), (5, 7), (5, 8), (5, 9), (5, 38), 
(6, 10), (6, 11), (6, 12), (6, 13), (6, 40);

-- 7. PEDIDOS Y DETALLE PEDIDO
INSERT INTO Pedidos (fecha, Proveedor_idProveedor) VALUES
('2023-10-01', 1), ('2023-10-05', 2), ('2023-10-10', 3), ('2023-10-12', 4), ('2023-10-15', 5), ('2023-10-20', 6);

INSERT INTO DetallePedido (Producto_idProducto, cantidad, precioCompraUnitario, Pedidos_idPedidos) VALUES
(1, 100, 1.20, 1), (3, 50, 0.50, 1),
(24, 200, 1.10, 2), (25, 300, 0.40, 2),
(30, 100, 2.50, 3),
(20, 500, 0.30, 4), 
(6, 50, 4.00, 5), 
(11, 20, 9.00, 6); 

-- 8. PROMOCIONES
INSERT INTO Promociones (nombrePromocion, unidadesAfectadas, precioPorUnidad, fechaInicio, fechaFin, aplicableA, reglas, Categoria_idCategoria) VALUES
('3x2 en Lácteos', 3, 0.63, '2023-11-01', '2023-11-30', 'Leche Entera', 'Lleva 3 y paga 2', 4),
('Semana Fantástica Bebidas', 1, 0.60, '2023-10-15', '2023-10-25', 'Cerveza Victoria', 'Descuento directo', 6),
('Pack Desayuno', 2, 0.90, '2023-01-01', NULL, 'Bollería', 'Descuento por par', 5);

INSERT INTO Promociones_has_Producto (Promociones_idPromociones, Producto_idProducto) VALUES
(1, 14), (2, 25), (3, 21);

-- 9. VENTAS Y DETALLES
INSERT INTO Venta (fecha, metodoPago, Empleado_idEmpleado, Cliente_idCliente) VALUES 
('2023-11-01', 'Tarjeta', 1, 1),
('2023-11-01', 'Efectivo', 5, 2),
('2023-11-02', 'Tarjeta', 4, 3),
('2023-11-02', 'Tarjeta', 2, 4),
('2023-11-03', 'Tarjeta', 3, 5),
('2023-11-03', 'Efectivo', 1, 6),
('2023-11-04', 'Efectivo', 5, 7),
('2023-11-04', 'Tarjeta', 4, 8),
('2023-11-05', 'Tarjeta', 2, 9),
('2023-11-05', 'Efectivo', 3, 10),
('2023-11-06', 'Tarjeta', 1, 11),
('2023-11-06', 'Efectivo', 5, 12),
('2023-11-07', 'Tarjeta', 4, 13),
('2023-11-07', 'Tarjeta', 2, 14),
('2023-11-08', 'Efectivo', 3, 15);

INSERT INTO DetalleVenta VALUES 
(1, 1, 2.50, 2), (14, 1, 0.95, 6), (30, 1, 4.95, 1),
(9, 2, 3.50, 5), (26, 2, 8.50, 2),
(42, 3, 1.20, 1), (23, 3, 0.60, 1),
(6, 4, 7.95, 2), (5, 4, 4.50, 1), (33, 4, 8.90, 3), (28, 4, 7.95, 1),
(44, 5, 1.95, 4), (45, 5, 4.95, 2), (3, 5, 0.99, 1),
(20, 6, 0.80, 2), (18, 6, 2.10, 1), (27, 6, 1.99, 1),
(38, 7, 3.50, 2), (24, 7, 1.85, 1),
(25, 8, 0.75, 24), (41, 8, 1.80, 5),
(11, 9, 14.50, 1), (12, 9, 18.00, 1), (26, 9, 8.50, 1),
(14, 10, 0.95, 6), (19, 10, 2.30, 2), (34, 10, 1.30, 2),
(31, 11, 2.50, 2), (32, 11, 1.90, 2), (8, 11, 5.50, 1),
(21, 12, 1.10, 4), (22, 12, 1.50, 1),
(2, 13, 1.95, 2), (4, 13, 1.80, 1), (16, 13, 1.20, 2),
(40, 14, 1.80, 2), (7, 14, 4.20, 2), (36, 14, 0.70, 3),
(37, 15, 3.20, 4), (20, 15, 0.80, 2);

-- 10. CIERRE CAJA
INSERT INTO CierreCaja (fecha, validacion, incidencia, Empleado_idEmpleado) VALUES
('2023-11-05', 'Correcto', 'Sin novedad', 1),
('2023-11-05', 'Descuadre', 'Faltan 50 eurillos. Se los ha llevado el viento.', 5),
('2023-11-06', 'Correcto', 'Todo OK', 2),
('2023-11-07', 'Correcto', 'Caja perfecta', 3);

-- ========================================================
-- 11. ACTUALIZACIÓN CÓDIGOS DE PRODUCTO (Cálculo Automático)
-- ========================================================
-- Esta sección genera los códigos "XXXXAAA" automáticamente
-- ID: Relleno con ceros a la izquierda (4 dígitos)
-- NOMBRE: 3 primeras letras en mayúscula
-- Ejemplo: ID 1, Manzana -> 0001MAN
SET SQL_SAFE_UPDATES = 0;
UPDATE Producto 
SET codigoProducto = CONCAT(
    LPAD(idProducto, 4, '0'), 
    UPPER(LEFT(nombre, 3))
);
SET SQL_SAFE_UPDATES = 1;

-- Una vez generados, aplicamos las restricciones finales
ALTER TABLE Producto MODIFY codigoProducto VARCHAR(64) NOT NULL;
CREATE UNIQUE INDEX `codigoProducto_UNIQUE` ON `Producto` (`codigoProducto`);