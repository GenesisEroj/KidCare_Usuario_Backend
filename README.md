# KidCare — Usuario Service

Microservicio de gestión de usuarios y autenticación del sistema KidCare.
Se encarga del registro, login, recuperación de contraseña y administración de perfiles de menores y apoderados.

---

## Equipo

| Nombre | Rol |
|---|---|
| Génesis Rojas | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Benjamín Peña | Backend / Integración IA / DevOps |

---

## Stack técnico

- Java 21 + Spring Boot 3.x
- Spring Security + JWT (HMAC-SHA256, jjwt 0.12.6)
- MySQL 8.0 (Docker) — base de datos `db_usuario`
- Spring Data JPA / Hibernate (`ddl-auto=update`)
- Spring Boot Mail — Gmail SMTP (modo dev: imprime token en consola)
- Lombok

**Puerto:** `8081`

---

## Estructura del proyecto

```
src/main/java/com/kidcare/usuario_service/
│
├── model/
│   ├── Usuario.java        → Entidad principal con email, password (BCrypt) y token de recuperación
│   ├── Rol.java            → Entidad de rol: ADMIN, TUTOR o DELEGADO
│   ├── Menor.java          → Entidad de perfil de menor
│   └── UsuarioMenor.java   → Tabla pivot que vincula usuarios con menores (N:M)
│
├── repository/
│   ├── UsuarioRepository.java → Búsqueda por email y token de recuperación
│   ├── RolRepository.java     → Búsqueda por nombre de rol
│   └── MenorRepository.java   → Búsqueda de menores por usuario
│
├── dto/
│   ├── RegistroRequestDTO.java         → Datos para registro (nombreCompleto, email, password, rolNombre, aceptaTerminos)
│   ├── LoginRequestDTO.java            → Credenciales de login
│   ├── AuthResponseDTO.java            → Respuesta con token JWT, email y rol
│   ├── RecuperarPasswordRequestDTO.java → Email para solicitar recuperación
│   ├── NuevaPasswordRequestDTO.java    → Token UUID + nueva contraseña
│   ├── MenorRequestDTO.java            → Datos para crear/editar un menor
│   ├── MenorResponseDTO.java           → Datos de respuesta de un menor
│   └── VincularDelegadoRequestDTO.java → Email del delegado + idMenor
│
├── security/
│   ├── JwtUtil.java        → Genera y valida tokens JWT (claims: sub=email, rol, idUsuario)
│   ├── JwtFilter.java      → Intercepta requests y extrae el JWT del header Authorization
│   └── SecurityConfig.java → Rutas públicas: /api/auth/**, rutas protegidas: todo lo demás
│
├── service/
│   ├── AuthService.java    → Registro, login, recuperación y restablecimiento de contraseña
│   ├── MenorService.java   → CRUD de perfiles de menores
│   ├── DelegadoService.java → Vinculación de apoderados a menores
│   └── EmailService.java   → Envío de correo o impresión en consola (modo dev)
│
├── controller/
│   ├── AuthController.java      → POST /api/auth/{registro,login,recuperar,restablecer}
│   ├── MenorController.java     → CRUD /api/menores
│   └── DelegadoController.java  → POST /api/delegados/vincular
│
└── exception/
    └── GlobalExceptionHandler.java → Errores de validación → 400 Bad Request
```

---

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Body | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | `{nombreCompleto, email, password, rolNombre, aceptaTerminos}` | Registra nuevo usuario |
| POST | `/api/auth/login` | `{email, password}` | Retorna JWT |
| POST | `/api/auth/recuperar` | `{email}` | Genera token de recuperación |
| POST | `/api/auth/restablecer` | `{token, nuevaPassword}` | Establece nueva contraseña |

### Menores (requieren JWT)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/menores` | Crea perfil de menor (TUTOR/ADMIN) |
| GET | `/api/menores` | Lista menores del usuario autenticado |
| PUT | `/api/menores/{id}` | Edita perfil de menor |
| DELETE | `/api/menores/{id}` | Elimina perfil de menor |

### Delegados (requieren JWT — solo TUTOR/ADMIN)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/delegados/vincular` | Vincula un apoderado a un menor por email |

---

## Modelos de datos

```
ROL          (id_rol, nombre UNIQUE, descripcion)
USUARIO      (id_usuario, id_rol, nombre_completo, email UNIQUE, password_hash,
              activo, token_recuperacion, fecha_expiracion_token)
MENOR        (id_menor, nombre, fecha_nacimiento, sexo)
USUARIO_MENOR (id_usuario, id_menor)  →  tabla pivot N:M
```

---

## Seguridad

- Contraseñas encriptadas con BCrypt
- JWT firmado con `kidcare-secret-key-2024-segura-32chars` (expiración 24h)
- Claims del JWT: `sub` (email), `rol`, `idUsuario`
- Token de recuperación: UUID válido 24h, se invalida tras el primer uso

---

## Cómo iniciar en otro equipo

### Prerrequisitos

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| Java JDK | 21 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org/download.cgi |
| Docker Desktop | 4.x | https://www.docker.com/products/docker-desktop |
| Git | cualquiera | https://git-scm.com |

Verifica la instalación:
```bash
java -version    # debe decir openjdk 21
mvn -version     # debe decir Apache Maven 3.9.x
docker --version # debe decir Docker version 24.x o superior
```

---

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/vareeth227/KidCare_Usuario_Backend.git
cd KidCare_Usuario_Backend
```

---

### Paso 2 — Iniciar MySQL con Docker

Crea el archivo `docker-compose.yml` en la carpeta raíz del proyecto con este contenido:

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: kidcare-mysql
    environment:
      MYSQL_ROOT_PASSWORD: kidcare123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: >
      --default-authentication-plugin=mysql_native_password
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_unicode_ci

volumes:
  mysql_data:
```

Luego inicia el contenedor:

```bash
docker compose up -d
```

Espera 15–20 segundos hasta que MySQL esté listo. Verifica con:

```bash
docker ps
```

Debes ver `kidcare-mysql` con estado `Up`.

---

### Paso 3 — Crear la base de datos

```bash
docker exec -it kidcare-mysql mysql -u root -pkidcare123 -e "CREATE DATABASE IF NOT EXISTS db_usuario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Verifica:

```bash
docker exec -it kidcare-mysql mysql -u root -pkidcare123 -e "SHOW DATABASES;"
```

Debes ver `db_usuario` en la lista.

---

### Paso 4 — Revisar application.properties

El archivo `src/main/resources/application.properties` ya está configurado para conectarse a MySQL en localhost con las credenciales del Paso 2. No necesitas cambiar nada para desarrollo local.

Para correo en desarrollo, el modo dev está activo por defecto (`mail.dev-mode=true`): el token de recuperación se imprime en la consola del servidor en lugar de enviarse por email. No se requiere configuración de Gmail.

---

### Paso 5 — Compilar

```bash
mvn clean install -DskipTests
```

Espera a que aparezca `BUILD SUCCESS`.

---

### Paso 6 — Ejecutar

```bash
mvn spring-boot:run
```

Espera a que aparezca:

```
Started UsuarioServiceApplication in X.XXX seconds
```

El servicio queda disponible en `http://localhost:8081`.

---

### Paso 7 — Verificar

**Registro de usuario de prueba** (PowerShell):

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/auth/registro" `
  -Method POST -ContentType "application/json" `
  -Body '{"nombreCompleto":"Test","email":"test@kidcare.com","password":"Password123","rolNombre":"TUTOR","aceptaTerminos":true}'
```

Respuesta esperada: objeto JSON con `token`, `email` y `rol`.

**Login:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
  -Method POST -ContentType "application/json" `
  -Body '{"email":"test@kidcare.com","password":"Password123"}'
```

**Recuperar contraseña:**

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/auth/recuperar" `
  -Method POST -ContentType "application/json" `
  -Body '{"email":"test@kidcare.com"}'
```

El token aparecerá en la consola del servidor (modo dev).

---

## Notas importantes

- La clave `jwt.secret` debe ser idéntica en los 4 microservicios de KidCare.
- El campo `nombre` de la tabla `ROL` tiene restricción UNIQUE — si la base de datos ya tenía roles duplicados de pruebas anteriores, ejecuta: `docker exec kidcare-mysql mysql -u root -pkidcare123 db_usuario -e "SET FOREIGN_KEY_CHECKS=0; DELETE FROM usuario; DELETE FROM rol; SET FOREIGN_KEY_CHECKS=1;"`
- El token JWT debe enviarse en el header `Authorization: Bearer <token>` en todas las rutas protegidas.
