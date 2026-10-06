-- Carga de usuarios iniciales (ADMIN, REPARTIDOR, CLIENTE) - Pass: password123
INSERT INTO usuarios (nombre, direccion, telefono, email, password, rol)
VALUES
('Administrador Principal', 'Ciudad de Guatemala', '55510001', 'admin@delivery.com', '$2a$10$f3Fh2dOvhxL16g8R35v5E.WJv0qQ5wA1RzL6h4R02hEmsvNlmkGte', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios (nombre, direccion, telefono, email, password, rol)
VALUES
('Repartidor Oficial', 'Zona 10, Ciudad de Guatemala', '55520002', 'repartidor@delivery.com', '$2a$10$f3Fh2dOvhxL16g8R35v5E.WJv0qQ5wA1RzL6h4R02hEmsvNlmkGte', 'REPARTIDOR')
ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios (nombre, direccion, telefono, email, password, rol)
VALUES
('Cliente Test', 'Zona 1, Ciudad de Guatemala', '55530003', 'cliente@delivery.com', '$2a$10$f3Fh2dOvhxL16g8R35v5E.WJv0qQ5wA1RzL6h4R02hEmsvNlmkGte', 'CLIENTE')
ON CONFLICT (email) DO NOTHING;

-- Carga inicial de comercios
INSERT INTO comercios (nombre, categoria, direccion, abierto)
VALUES
('Hamburguesas Express', 'RESTAURANTE', 'Avenida Reforma 12-01, Zona 10', true)
ON CONFLICT DO NOTHING;

-- Carga inicial de productos
INSERT INTO productos (comercio_id, nombre, precio, stock, disponible)
VALUES
(1, 'Hamburguesa Doble con Queso', 45.00, 20, true),
(1, 'Papas Fritas Medianas', 18.00, 30, true),
(1, 'Gaseosa en Lata', 12.00, 50, true)
ON CONFLICT DO NOTHING;