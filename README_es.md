README de uso (español)
=========================

Requisitos
---------
- JDK 11+ instalado en Windows (asegúrate de que `javac` y `java` estén en el PATH).
- MySQL (o MariaDB) disponible localmente o en una máquina accesible.
- PowerShell para ejecutar los scripts incluidos.

Archivos importantes
--------------------
- `build.ps1` / `run.ps1`: scripts para compilar y ejecutar sin Maven (usa `javac` y `jar`).
-- `src/main/resources/db.properties`: archivo de configuración para la conexión a la base de datos.
-- `sql/init_db.sql`: script para crear la base de datos y tablas mínimas necesarias, y un usuario administrador de ejemplo.
-- `scripts/setup_db.ps1`: script interactivo para crear la base y usuario de aplicación (usa el cliente `mysql`).
-- `scripts/helpers/`: scripts SQL auxiliares (creación de usuario, ajuste de plugin, inspección).

Pasos rápidos
-------------
1) Configura MySQL y crea la base de datos y el usuario de aplicación (script interactivo):

  Abre PowerShell en la raíz del proyecto y ejecuta:

```powershell
# posicionarse en la carpeta del proyecto
cd .\for_github\AbarreyApp

# ejecutar el script interactivo que crea la DB y el usuario de aplicación
.\scripts\setup_db.ps1
```

2) Ajusta `src/main/resources/db.properties` si tus credenciales/host son diferentes.

3) Compila y empaqueta con el script incluido:

```powershell
cd .\for_github\AbarreyApp
.\build.ps1
```

4) Ejecuta la aplicación (para ver salida en la consola):

```powershell
.\scripts\run_app.ps1
```

Pruebas rápidas (credenciales de ejemplo)
----------------------------------------
- Usuario administrador de ejemplo creado por `init_db.sql`:
  - usuario: admin
  - contraseña: 1234

Nota sobre contraseñas
----------------------
El sistema migrará automáticamente usuarios "legado" que no tengan hash PBKDF2: al iniciar sesión con la contraseña legacy `1234`, la aplicación calculará y guardará el hash PBKDF2 en la base de datos.

Si necesitas ayuda para ejecutar los scripts o adaptar el `db.properties`, dime qué host/usuario/contraseña quieres usar y lo preparo.
