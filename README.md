AbarreyApp — ready-to-run package for classmates

This folder contains a self-contained copy of the AbarreyApp Java Swing application prepared for sharing on GitHub.

AbarreyApp — paquete listo para compartir con tus compañeros

Este directorio contiene una copia auto-contenida de la aplicación Java Swing AbarreyApp preparada para compartir en GitHub.

Contenido
- Código fuente: `src/main/java/**`
- Recursos: `src/main/resources/**` (imágenes usadas por la app)
- FlatLaf (look-and-feel): `lib/flatlaf-3.4.1.jar`
- Script de build (no requiere Maven): `build.ps1`
- Helpers de ejecución: `run.ps1` (PowerShell) y `start.bat` (doble clic en Windows)
- El script de build crea un JAR listo para ejecutar en `target/`.

Requisitos
- JDK 11+ instalado (o que `javac`, `jar` y `javaw` estén en el PATH). Es recomendable tener `JAVA_HOME` apuntando al JDK.
- Windows (el `start.bat` incluido es para Windows); los scripts PowerShell también funcionan en PowerShell Core en otros sistemas.

Cómo compilar
Abre PowerShell en esta carpeta y ejecuta:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1
```

Esto compilará las fuentes, copiará los recursos y generará el JAR ejecutable en:

```
C:\ruta\a\for_github\AbarreyApp\target\AbarreyApp-1.0-SNAPSHOT.jar
```

Cómo ejecutar
- Haz doble clic en `start.bat` desde el Explorador (Windows). Esto lanzará la interfaz gráfica sin abrir una consola.
- O desde PowerShell:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

Notas para tus compañeros
- Si PowerShell bloquea la ejecución de scripts, usa la opción `-ExecutionPolicy Bypass` como en los ejemplos anteriores, o cambia la política local para el usuario actual:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

- Si no quieres incluir `flatlaf-3.4.1.jar` en el repositorio, se puede eliminar y modificar `build.ps1` para descargar la dependencia en el paso de build (se explica en el script).

Licencia
- Este paquete incluye los archivos fuente del proyecto. Añade o reemplaza con la licencia que prefieras si es necesario.

Contacto
- Si tienes problemas, abre un issue en el repositorio de GitHub describiendo el error y tu entorno (SO, versión de Java, pasos para reproducir).