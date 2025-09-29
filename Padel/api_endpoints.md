# 📌 API Endpoints - Padel App

## Health Check
- **GET** `/api/health` → Devuelve `"OK"` para verificar que la API está activa.

---

## Users
- **GET** `/api/users` → Lista todos los usuarios.
- **GET** `/api/users/{id}` → Obtiene un usuario por ID.
- **POST** `/api/users` → Crea un nuevo usuario.
    - Body JSON:
      ```json
      {
        "nombre": "Juan Pérez",
        "email": "juan@example.com",
        "password": "123456",
        "fotoUrl": "https://example.com/fotos/juan.jpg"
      }
      ```
- **DELETE** `/api/users/{id}` → Elimina un usuario.

---

## Courts
- **GET** `/api/courts` → Lista todas las canchas.
- **GET** `/api/courts/{id}` → Obtiene una cancha por ID.
- **POST** `/api/courts` → Crea una nueva cancha.
    - Body JSON:
      ```json
      {
        "nombre": "Cancha Central",
        "direccion": "Av. Padel 123",
        "lat": -34.6037,
        "lng": -58.3816,
        "price": 2500,
        "ownerId": 1
      }
      ```
- **DELETE** `/api/courts/{id}` → Elimina una cancha.

---

## Bookings
- **GET** `/api/bookings` → Lista todas las reservas.
- **GET** `/api/bookings/{id}` → Obtiene una reserva por ID.
- **POST** `/api/bookings` → Crea una nueva reserva.
    - Body JSON:
      ```json
      {
        "courtId": 1,
        "userId": 1,
        "startTime": "2025-09-26T18:00:00",
        "endTime": "2025-09-26T19:30:00"
      }
      ```
- **DELETE** `/api/bookings/{id}` → Elimina una reserva.
