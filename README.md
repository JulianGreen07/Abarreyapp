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
