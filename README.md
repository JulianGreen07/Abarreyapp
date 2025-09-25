AbarreyApp — ready-to-run package for classmates

This folder contains a self-contained copy of the AbarreyApp Java Swing application prepared for sharing on GitHub.

What is included
- Source code: src/main/java/**
- Resources: src/main/resources/** (images used by the app)
- FlatLaf look-and-feel: lib/flatlaf-3.4.1.jar
- Build script (no Maven required): build.ps1
- Run helper: run.ps1 (PowerShell) and start.bat (Windows double-click)
- A pre-packaged JAR will be created under target/ by the build script.

Prerequisites
- JDK 11+ installed (JAVA_HOME set to JDK) or javac, jar, javaw on PATH.
- Windows (the included start.bat is for Windows; PowerShell scripts work on any platform with PowerShell Core)

How to build
Open PowerShell in this folder and run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1
```

This will compile sources, copy resources, and create the runnable JAR at:

```
C:\path\to\for_github\AbarreyApp\target\AbarreyApp-1.0-SNAPSHOT.jar
```

How to run
- Double-click `start.bat` in File Explorer (Windows). This will launch the GUI without a console window.
- Or from PowerShell:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

Notes for collaborators
- If PowerShell blocks script execution, use the command above with `-ExecutionPolicy Bypass` or set a scoped policy: `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`.
- If you prefer not to include the FlatLaf JAR in the repo, remove `lib/flatlaf-3.4.1.jar` and update `build.ps1` to instruct how to download it.

License
- This package includes the project's source files. Add or replace with your preferred license if needed.

Contact
- If you have issues, open a GitHub issue in the repo with the error output and environment details (OS, Java version).