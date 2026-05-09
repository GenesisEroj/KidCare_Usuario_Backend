# KidCare — Usuario Service

Microservicio de gestión de usuarios y autenticación del sistema KidCare.
Se encarga del registro, login, recuperación de contraseña y administración de perfiles de menores y apoderados.

## Equipo

| Nombre | Rol |
|---|---|
| Génesis Rojas | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Benjamín Peña | Backend / Integración IA / DevOps |

## Stack técnico

- Java 21 + Spring Boot 3.x
- Spring Security + JWT (HMAC-SHA256)
- MySQL 8.0 (Docker) — base de datos `db_usuario`
- Spring Data JPA / Hibernate (`ddl-auto=update`)
- Spring Boot Mail — Gmail SMTP para recuperación de contraseña
- Lombok

**Puerto:** `8081`

## Prerrequisitos

- Docker Desktop corriendo con `docker-compose up -d` (MySQL + MongoDB)
- JDK 21
- Maven 3.9+

## Configuración

Edita `src/main/resources/application.properties` y reemplaza las credenciales de Gmail:

```properties
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_APP_PASSWORD_GOOGLE
```

Para obtener una contraseña de aplicación de Google:
1. Activa verificación en 2 pasos en tu cuenta Gmail
2. Ve a **Cuenta de Google → Seguridad → Contraseñas de aplicaciones**
3. Genera una nueva contraseña para "Correo / Windows"

## Cómo ejecutar

```bash
mvn spring-boot:run
```

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/registro` | Registra nuevo usuario (TUTOR o DELEGADO) |
| POST | `/api/auth/login` | Autentica y retorna JWT |
| POST | `/api/auth/recuperar` | Envía token de recuperación al correo |
| POST | `/api/auth/restablecer` | Establece nueva contraseña con el token |

### Menores (requieren JWT)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/menores` | Crea perfil de menor (TUTOR/ADMIN) |
| GET | `/api/menores` | Lista menores del usuario autenticado |
| PUT | `/api/menores/{id}` | Edita perfil de menor (TUTOR/ADMIN) |
| DELETE | `/api/menores/{id}` | Elimina perfil de menor (TUTOR/ADMIN) |

### Delegados (requieren JWT — solo TUTOR/ADMIN)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/delegados/vincular` | Vincula un apoderado a un menor por email |

## Modelos de datos

```
USUARIO (id_usuario, id_rol, nombre_completo, email, password_hash, activo, token_recuperacion, fecha_expiracion_token)
ROL (id_rol, nombre, descripcion)  →  ADMIN | TUTOR | DELEGADO
MENOR (id_menor, nombre, fecha_nacimiento, sexo)
USUARIO_MENOR (id_usuario, id_menor)  →  tabla pivot N:M
```

## Seguridad

- Contraseñas encriptadas con BCrypt
- JWT firmado con `kidcare-secret-key-2024-segura-32chars` (expiración 24h)
- Los roles se evalúan con prefijo `ROLE_` (Spring Security estándar)
- El token de recuperación de contraseña expira en 24 horas y se invalida tras un uso
