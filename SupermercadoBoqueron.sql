-- MySQL Workbench Forward Engineering
-- Contraseña root: superboqueron1234

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema superboqueron
-- -----------------------------------------------------
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
-- (MODIFICADA: Ahora incluye Categoria_idCategoria)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Producto` (
  `idProducto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(512) NOT NULL,
  `precio` DECIMAL(8,2) NOT NULL,
  `stock` INT NULL DEFAULT 0,
  `minStock` INT NULL DEFAULT 0,
  `Categoria_idCategoria` INT NOT NULL,
  `activo` TINYINT NOT NULL DEFAULT 1,
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
-- (MODIFICADA: Incluye metodoPago)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Venta` (
  `idVenta` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `metodoPago` VARCHAR(45) NULL DEFAULT 'Efectivo', -- Nueva columna
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
-- (MODIFICADA: Incluye aplicableA y reglas)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superboqueron`.`Promociones` (
  `idPromociones` INT NOT NULL AUTO_INCREMENT,
  `nombrePromocion` VARCHAR(256) NOT NULL,
  `unidadesAfectadas` INT NOT NULL,
  `precioPorUnidad` DECIMAL(8,2) NOT NULL,
  `fechaInicio` DATE NOT NULL,
  `fechaFin` DATE NULL,
  `aplicableA` VARCHAR(255) NULL, -- Nueva
  `reglas` VARCHAR(512) NULL,     -- Nueva
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
-- Table `superboqueron`.`CierreCaja` (NUEVA)
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