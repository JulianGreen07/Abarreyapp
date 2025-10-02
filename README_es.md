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
- `src/main/resources/db.properties`: archivo de configuración para la conexión a la base de datos.
- `sql/init_db.sql`: script para crear la base de datos y tablas mínimas necesarias, y un usuario administrador de ejemplo.

Pasos rápidos
-------------
1) Configura MySQL y crea la base de datos ejecutando el script SQL:

   Abre PowerShell y ejecuta:

```powershell
# posicionarse en la carpeta del proyecto
cd .\for_github\AbarreyApp

# ejecutar el script SQL (se pedirá la contraseña de MySQL)
mysql -u root -p < .\sql\init_db.sql
```

2) Ajusta `src/main/resources/db.properties` si tus credenciales/host son diferentes.

3) Compila y empaqueta con el script incluido:

```powershell
cd .\for_github\AbarreyApp
.\build.ps1
```

4) Ejecuta la aplicación:

```powershell
.\run.ps1
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
