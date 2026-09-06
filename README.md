# Periferia API

Backend Spring Boot para registro/login de usuarios (JWT) y CRUD de tareas.

## Requisitos

- **Java 21**
- **MySQL 8+**
- **Gradle** (incluido con el wrapper `gradlew` / `gradlew.bat`)

## Configuración de la base de datos

1. Crea la base de datos en MySQL:

```sql
CREATE DATABASE periferiadb;
```

2. Revisa las credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/periferiadb
spring.datasource.username=root
spring.datasource.password=admin123
```

Ajusta `username` y `password` según tu instalación de MySQL.

Las tablas (`users`, `tasks`) se crean automáticamente al iniciar la aplicación (`ddl-auto=update`).

## Cómo ejecutar

Desde la raíz del proyecto:

**Windows**

```bash
.\gradlew.bat bootRun
```

**Linux / macOS**

```bash
./gradlew bootRun
```

También puedes ejecutar la clase `com.prueba.periferia.PeriferiaApplication` desde el IDE.

La API queda disponible en:

- **Base URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## Endpoints principales

### Usuarios (públicos)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/users/register` | Registro de usuario |
| `POST` | `/api/users/login` | Login (devuelve JWT) |

**Registro**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login**

```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

### Tareas (requieren JWT)

Incluye el header:

```http
Authorization: Bearer <token>
```

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/tasks` | Crear tarea |
| `GET` | `/api/tasks?userId=1&page=0&size=10` | Listar tareas por usuario (paginado) |
| `GET` | `/api/tasks?userId=1&status=PENDING&page=0&size=10` | Filtrar por estado (`PENDING` / `COMPLETED`) |
| `PUT` | `/api/tasks/{id}` | Actualizar tarea |
| `DELETE` | `/api/tasks/{id}` | Eliminar tarea |

Si omites `status`, se devuelven todas las tareas del usuario.

En Swagger UI usa el botón **Authorize** y pega el token obtenido en el login.

## Consumo desde Flutter

En el backend ya está habilitado **CORS** y el servidor escucha en `0.0.0.0:8080` para que el emulador/dispositivo pueda conectarse.

### Base URL según dónde corras Flutter

| Entorno | Base URL |
|---------|----------|
| Emulador Android | `http://10.0.2.2:8080` |
| Simulador iOS | `http://localhost:8080` |
| Dispositivo físico | `http://<IP-de-tu-PC>:8080` (ej. `http://192.168.1.10:8080`) |
| Flutter Web | `http://localhost:8080` |

### Endpoints User

```text
POST /api/users/register
POST /api/users/login
```

Headers en ambos:

```text
Content-Type: application/json
```

**Register** → body: `{ "name", "email", "password" }` → respuesta `201`: `{ "id", "name", "email" }`

**Login** → body: `{ "email", "password" }` → respuesta `200`:

```json
{
  "token": "...",
  "type": "Bearer",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

Guarda `token` y `userId` (SharedPreferences / secure storage). Para **Tasks** envía:

```text
Authorization: Bearer <token>
```

### Ejemplo mínimo en Flutter (`http`)

```dart
final baseUrl = 'http://10.0.2.2:8080'; // Android emulator

Future<Map<String, dynamic>> login(String email, String password) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/users/login'),
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({'email': email, 'password': password}),
  );
  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  }
  throw Exception('Login failed: ${response.statusCode}');
}
```

En Android (API 28+), si usas `http` (no https), permite cleartext en `AndroidManifest.xml` o en `network_security_config` para desarrollo.

## Estructura del proyecto

```
controller  → endpoints REST
service     → interfaces de negocio
service/impl → implementaciones
repository  → acceso a datos (JPA)
model       → entidades
dto         → request/response
security    → JWT
config      → Security y OpenAPI
```

## Notas

- El password se almacena hasheado con BCrypt.
- El JWT expira en 24 horas (`app.jwt.expiration-ms`).
- Cambia `app.jwt.secret` en producción.
