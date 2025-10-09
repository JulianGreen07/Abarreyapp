AbarreyApp - Instrucciones y recomendaciones
============================================

Resumen
------
Aplicación de escritorio Java Swing para gestionar inventario, usuarios y merma.
El proyecto está preparado para usar una base de datos MySQL centralizada y soporta "sucursales" (branches) que comparten la misma base de datos pero están aisladas por `branch_id`.

Qué hice aquí (resumen técnico en breve)
---------------------------------------
- Añadido selector de sucursal en la cabecera de la ventana principal (`MainFrame`).
- Implementado `Branch` (modelo) y `BranchDAO` para cargar sucursales desde la BD.
- Las DAO (`UserDAO`, `ProductDAO`, `MermaDAO`, `ReportDAO`) ya filtran por `branch_id`.
- Añadidos métodos `reload()` en los paneles principales (`UsuariosPanel`, `FrutasVerdurasPanel`, `MermaPanel`) para recargar los datos al cambiar de sucursal.

Requisitos previos
------------------
- Java 11+ (o la versión que uses para compilar el proyecto)
- MySQL 8.x accesible desde la máquina
- Conector JDBC en `lib/mysql-connector-j-*.jar` (ya incluido en `lib/` del proyecto)

Preparar la base de datos
-------------------------
1. Edita `src/main/resources/db.properties` con tus credenciales y host/puerto.
2. Ejecuta el script SQL `db/init_extended.sql` en tu servidor MySQL para crear tablas y la tabla `branches`.
   - Puedes usar `mysql.exe` o `mysqlsh` en modo SQL. Ejemplos:

```powershell
# Importar usando mysql.exe (Windows)
mysql -u tu_usuario -p tu_base_de_datos < db/init_extended.sql

# O abrir mysqlsh en modo SQL y ejecutar:
# \\source C:/ruta/a/API2/AbarreyApp/db/init_extended.sql
```

Nota: Algunas sentencias ALTER/ADD CONSTRAINT en el script pueden necesitar versión específica de MySQL; si recibes errores, dímelo y te doy una versión segura del script paso a paso.

Ejecutar la aplicación localmente
--------------------------------
He incluido un script de ayuda `scripts/run_local.ps1` que compila y ejecuta la aplicación sin Maven. Desde PowerShell en la raíz `AbarreyApp`:

```powershell
# Ejecutar el script (asegúrate de permitir ejecución si es necesario)
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run_local.ps1
```

Si prefieres compilar a mano:
```powershell
# Compilar
mkdir -Force target\classes
Get-ChildItem -Recurse -Filter *.java -Path src\main\java | ForEach-Object { $_.FullName } > sources.txt
javac -cp "lib/*;target/classes" -d target\classes (Get-Content sources.txt)
# Ejecutar
java -cp "target/classes;lib/*" com.abarreyapp.Main
```

Recomendaciones y pasos siguientes
---------------------------------
- Ejecuta primero el script SQL `db/init_extended.sql` para crear `branches` y añadir `branch_id` a las tablas existentes.
- Tras importar la BD, abre la aplicación y selecciona la sucursal en la esquina superior derecha. Los paneles recargarán datos automáticamente.
- Sugerencia: añadir una UI para administrar sucursales (crear/editar) desde la app. Puedo implementarla si quieres.
- Opcional: agregar pruebas unitarias pequeñas para DAOs que usen una base de datos en memoria (H2) para verificar la lógica de `branch_id`.

Si quieres que yo continúe ahora, elige una de estas opciones:
- Ejecutar el script SQL en tu equipo (necesito confirmación y/o credenciales si quieres que lo intente desde aquí).
- Añadir una pantalla de administración de sucursales.
- Limpiar y mejorar la forma en que `MainFrame` almacena referencias a los paneles (más robusto).

Si prefieres que te guíe paso a paso para ejecutar todo en tu máquina, dime y te doy los comandos exactos en PowerShell.
