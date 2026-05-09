# ARRANQUE — Usuario Service (puerto 8081)

Guía paso a paso para iniciar el microservicio de usuarios en un equipo nuevo.
Sigue los pasos en orden, sin saltarte ninguno.

---

## Antes de empezar — verifica que tienes todo instalado

Abre una terminal y ejecuta cada comando. Si alguno falla, instala la herramienta antes de continuar.

```bash
java -version
```
Debe decir `openjdk 21`. Si no lo tienes: https://adoptium.net → descarga **Temurin 21 LTS**.

```bash
mvn -version
```
Debe decir `Apache Maven 3.9.x`. Si no lo tienes: https://maven.apache.org/download.cgi

```bash
docker --version
```
Debe decir `Docker version 24.x` o superior. Si no lo tienes: https://www.docker.com/products/docker-desktop → instala Docker Desktop y ábrelo antes de continuar.

```bash
git --version
```
Cualquier versión sirve. Si no lo tienes: https://git-scm.com

---

## Paso 1 — Obtener el código

Si ya tienes el repositorio clonado:

```bash
cd KidCare_Usuario_Backend
git fetch origin
git checkout benja
git pull origin benja
```

Si es la primera vez:

```bash
git clone https://github.com/vareeth227/KidCare_Usuario_Backend.git
cd KidCare_Usuario_Backend
git checkout benja
```

---

## Paso 2 — Iniciar Docker Desktop

Abre Docker Desktop desde el menú de inicio y espera a que el ícono de la ballena deje de animarse (puede tardar 30–60 segundos).

Verifica que Docker esté corriendo:

```bash
docker ps
```

Si no da error, Docker está listo.

---

## Paso 3 — Iniciar MySQL con Docker

Crea un archivo llamado `docker-compose.yml` en la carpeta raíz del proyecto (donde está el `pom.xml`) con este contenido exacto:

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

Luego ejecuta:

```bash
docker compose up -d
```

Espera 20 segundos y verifica que el contenedor esté corriendo:

```bash
docker ps
```

Debes ver una línea con `kidcare-mysql` y estado `Up`.

---

## Paso 4 — Crear la base de datos

**Windows PowerShell:**
```powershell
docker exec kidcare-mysql mysql -u root -pkidcare123 -e "CREATE DATABASE IF NOT EXISTS db_usuario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

**Mac / Linux (Terminal):**
```bash
docker exec kidcare-mysql mysql -u root -pkidcare123 -e "CREATE DATABASE IF NOT EXISTS db_usuario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Verifica que se creó:

```bash
docker exec kidcare-mysql mysql -u root -pkidcare123 -e "SHOW DATABASES;"
```

Debes ver `db_usuario` en la lista.

---

## Paso 5 — Revisar application.properties

Abre el archivo `src/main/resources/application.properties`. Debe verse así:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/db_usuario?...
spring.datasource.username=root
spring.datasource.password=kidcare123
```

Si las credenciales de MySQL coinciden con las del Paso 3 (`root` / `kidcare123`), no necesitas cambiar nada.

> **Nota sobre email:** el sistema está en modo desarrollo (`mail.dev-mode=true` por defecto). El token de recuperación de contraseña se imprime en la consola del servidor — no necesitas configurar Gmail.

---

## Paso 6 — Compilar el proyecto

```bash
mvn clean install -DskipTests
```

Espera a que aparezca:

```
BUILD SUCCESS
```

Si falla, verifica que Java 21 y Maven están correctamente instalados (`java -version`, `mvn -version`).

---

## Paso 7 — Iniciar el servicio

```bash
mvn spring-boot:run
```

Espera a que aparezca esta línea (puede tardar 10–20 segundos):

```
Started UsuarioServiceApplication in X.XXX seconds (process running for X.XXX)
```

El servicio queda disponible en `http://localhost:8081`. **No cierres esta terminal.**

---

## Paso 8 — Verificar que funciona

Abre una terminal nueva y ejecuta:

**Windows PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/auth/registro" `
  -Method POST -ContentType "application/json" `
  -Body '{"nombreCompleto":"Test","email":"test@kidcare.com","password":"Password123","rolNombre":"TUTOR","aceptaTerminos":true}'
```

**Mac / Linux:**
```bash
curl -s -X POST http://localhost:8081/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"nombreCompleto":"Test","email":"test@kidcare.com","password":"Password123","rolNombre":"TUTOR","aceptaTerminos":true}'
```

Respuesta esperada: un objeto JSON con `token`, `email` y `rol`. Si ves eso, el servicio está funcionando correctamente.

---

## Solución de problemas frecuentes

### Error: "Communications link failure" o "Connection refused"
MySQL no está corriendo. Ejecuta `docker ps` y verifica que `kidcare-mysql` aparece con estado `Up`. Si no aparece, repite el Paso 3.

### Error: "Query did not return a unique result: N results were returned"
Hay roles duplicados en la base de datos. Ejecuta esto y reinicia el servicio:

```bash
docker exec kidcare-mysql mysql -u root -pkidcare123 db_usuario -e "SET FOREIGN_KEY_CHECKS=0; DELETE FROM usuario; DELETE FROM rol; SET FOREIGN_KEY_CHECKS=1;"
```

### Error: "Port 8081 already in use"
Otro proceso está usando el puerto. En Windows:
```powershell
netstat -ano | findstr :8081
taskkill /PID <el_numero_que_aparece> /F
```

### Error: "BUILD FAILURE" en mvn spring-boot:run
Haz scroll hacia arriba en la terminal para ver el error real. Los errores más comunes son Java no instalado o MySQL no corriendo.
