Workpoint snapshot — 2025-10-08

Branch: work/from-current-2025-10-08
Commit: 2953f4e (local)

Objetivo
- A partir de esta versión se continuará el trabajo (esta es la copia compilada y arrancada con éxito desde `src`).

Arrancar la app (rápido)
1) Compilar y ejecutar (PowerShell):
   .\scripts\compile_and_run.ps1

Notas importantes
- Asegúrate de que `lib/` contiene `flatlaf-*.jar` y `mysql-connector-j-*.jar` antes de ejecutar.
- La app lee la configuración de la base de datos desde `src/main/resources/db.properties` o desde el `DBConfig` en tiempo de ejecución; confirma que apunta a la base de datos donde se aplicó la migración de passwords (hash PBKDF2).
- Si el login falla, comprueba la sucursal (branch_id) que se selecciona en la pantalla de login: las consultas están limitadas por branch.

Próximos pasos sugeridos
- Alternativa A: Añadir logging temporal en `LoginForm` para ver el hash almacenado y el resultado de verificación.
- Alternativa B: Crear un usuario de prueba con branch_id conocido para confirmar flujo de login.

Creado por: agent
Fecha: 2025-10-08
