USE `superboqueron`;

-- Limpiamos tabla por si acaso (Opcional)
-- DELETE FROM Empleado; 
-- ALTER TABLE Empleado AUTO_INCREMENT = 1;

INSERT INTO Empleado (nombre, apellidos, fechaNacimiento, fechaIncorporacion, telefono, dni, sueldo, codigoEmpleado, contrasenia) VALUES 
-- 1. Oscar
('Oscar', 'Velázquez', '1995-05-15', '2022-01-10', '600111001', '12345678A', 1500.00, 'oscar1234', 'oscar1234'),
-- 2. David
('David', 'García', '1998-11-20', '2023-03-15', '600222002', '87654321B', 1450.50, 'david1234', 'david1234'),
-- 3. Adrian
('Adrian', 'López', '1992-07-08', '2021-06-01', '600333003', '11223344C', 1600.00, 'adrian1234', 'adrian1234'),
-- 4. Jaime
('Jaime', 'Pérez', '1990-02-28', '2020-09-20', '600444004', '44332211D', 1550.75, 'jaime1234', 'jaime1234'),
-- 5. Torrente (El Admin)
('José Luis', 'Torrente', '1965-03-13', '1998-03-13', '666999666', '99999999T', 2500.00, 'admin', 'admin');