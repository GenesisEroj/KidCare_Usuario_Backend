# KidCare — Microservicio de Usuario

Microservicio encargado de la autenticación, gestión de usuarios, perfiles de menores y auditoría del sistema KidCare.

---

## Tecnologías

- Java 21
- Spring Boot 3.5.14
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA
- MySQL
- Lombok
- Maven

---

## Puerto

```
8081
```

---

## Estructura del proyecto

```
src/main/java/com/kidcare/usuario_service/
│
├── model/
│   ├── Rol.java               → Entidad que representa los roles del sistema (ADMIN, TUTOR, DELEGADO)
│   ├── Permiso.java           → Entidad que representa las acciones permitidas en el sistema
│   ├── RolPermiso.java        → Tabla pivot N:M entre Rol y Permiso
│   ├── RolPermisoId.java      → Clave primaria compuesta de RolPermiso
│   ├── Usuario.java           → Entidad principal de usuarios del sistema
│   ├── Menor.java             → Entidad que representa el perfil de un menor
│   ├── UsuarioMenor.java      → Tabla pivot N:M entre Usuario y Menor
│   ├── UsuarioMenorId.java    → Clave primaria compuesta de UsuarioMenor
│   └── Auditoria.java         → Entidad que registra acciones del administrador
│
├── repository/
│   ├── RolRepository.java           → Acceso a datos de Rol (búsqueda por nombre)
│   ├── UsuarioRepository.java       → Acceso a datos de Usuario (búsqueda por email y token)
│   ├── MenorRepository.java         → Acceso a datos de Menor
│   ├── UsuarioMenorRepository.java  → Acceso a datos de UsuarioMenor (menores por tutor)
│   └── AuditoriaRepository.java     → Acceso a datos de Auditoria (registros por usuario)
│
├── dto/
│   ├── RegistroRequestDTO.java           → Datos para registrar un nuevo usuario
│   ├── LoginRequestDTO.java              → Datos para iniciar sesión
│   ├── AuthResponseDTO.java              → Token JWT y datos del usuario autenticado
│   ├── MenorRequestDTO.java              → Datos para crear o editar un menor
│   ├── MenorResponseDTO.java             → Datos de respuesta de un menor
│   ├── RecuperarPasswordRequestDTO.java  → Correo para solicitar recuperación de contraseña
│   └── NuevaPasswordRequestDTO.java      → Token y nueva contraseña para restablecerla
│
├── security/
│   ├── JwtUtil.java       → Genera, valida y extrae datos de tokens JWT
│   ├── JwtFilter.java     → Intercepta cada request y valida el token JWT del header
│   └── SecurityConfig.java → Configura rutas públicas, protegidas y política de sesión
│
├── service/
│   ├── AuthService.java    → Lógica de registro, login y recuperación de contraseña
│   └── MenorService.java   → Lógica de creación, edición, listado y eliminación de menores
│
├── controller/
│   ├── AuthController.java     → Endpoints POST /api/auth/registro y POST /api/auth/login
│   ├── PasswordController.java → Endpoints POST /api/password/recuperar y /restablecer
│   └── MenorController.java    → Endpoints CRUD de /api/menores
│
└── exception/
    └── GlobalExceptionHandler.java → Maneja errores de validación y excepciones de negocio
```

---

## Endpoints

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| POST | `/api/auth/registro` | Público | Registra un nuevo tutor |
| POST | `/api/auth/login` | Público | Inicia sesión y retorna token JWT |
| POST | `/api/password/recuperar` | Público | Envía token de recuperación al correo |
| POST | `/api/password/restablecer` | Público | Restablece la contraseña con el token |
| GET | `/api/menores` | Autenticado | Lista los menores del tutor |
| POST | `/api/menores` | Autenticado | Crea un perfil de menor |
| PUT | `/api/menores/{id}` | Autenticado | Edita el perfil de un menor |
| DELETE | `/api/menores/{id}` | Autenticado | Elimina el perfil de un menor |

---

## Requisitos previos

- Java 21 instalado
- Maven instalado
- MySQL corriendo (cuando se conecte la BD)
- VS Code con Extension Pack for Java y Spring Boot Extension Pack

---

## Cómo iniciar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/vareeth227/KidCare_Usuario_Backend.git
cd KidCare_Usuario_Backend
```

### 2. Configurar variables de entorno

Edita el archivo `src/main/resources/application.properties` con tus datos de MySQL cuando tengas la base de datos lista:

```properties
server.port=8081
spring.application.name=usuario-service
spring.datasource.url=jdbc:mysql://localhost:3306/db_users
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=kidcare-secret-key-2024-segura-32chars
jwt.expiration=86400000
```

### 3. Compilar el proyecto

```bash
mvn clean install -DskipTests
```

### 4. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

El microservicio estará disponible en `http://localhost:8081`

---

## Notas importantes

- El token JWT debe enviarse en el header `Authorization: Bearer <token>` en todas las rutas protegidas.
- La clave `jwt.secret` debe ser la misma en todos los microservicios de KidCare.
- Por ahora la base de datos está desactivada en `application.properties`. Cuando se conecte Docker hay que eliminar la línea `spring.autoconfigure.exclude`.

---

## Integrantes

| Nombre | Rol |
|--------|-----|
| Benjamín Peña | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Génesis Rojas | Backend / Integración IA / DevOps |
