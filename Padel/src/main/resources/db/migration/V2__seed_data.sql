-- V2__seed_data.sql: datos de prueba
INSERT INTO users (email, password, nombre, role, puntos)
VALUES
  ('joaquin@test.local', 'password123', 'Joaquín', 'ADMIN', 100),
  ('maxi@test.local', 'password123', 'Maxi', 'USER', 10);

INSERT INTO courts (owner_id, nombre, direccion, lat, lng, price)
VALUES
  (1, 'Club Central - Cancha 1', 'Av. Falsa 123', -34.6, -58.4, 1200.00);

INSERT INTO bookings (court_id, created_by, start_time, end_time, status)
VALUES
  (1, 2, '2025-10-15 18:00:00', '2025-10-15 19:00:00', 'BOOKED');
