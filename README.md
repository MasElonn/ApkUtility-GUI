# ApktoolGui

JavaFX GUI for Apktool.

## Features
- Decompile and Recompile APKs
- Sign APKs (custom or default keys)
- Zipalign support
- ADB integration (Install APK, Uninstall App)
- Dark/Light theme

## Requirements
- Java 21+

## Build

Build the standalone jar:

```bash
./mvnw clean package
```

Output: `target/ApktoolGui-1.0-SNAPSHOT-shaded.jar`

## Run

```bash
java -jar target/ApktoolGui-1.0-SNAPSHOT-shaded.jar
```
