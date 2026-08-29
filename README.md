# Localts Refresh Token App

Desktop app (Java Swing) for managing Localts Minecraft refresh-token products: import orders, auto-refresh Xbox/Minecraft tokens, keep accounts alive, and run a local proxy (`127.0.0.1:25565`) for servers that need a static token.

Cross-platform: works on **Windows**, **macOS**, and **Linux**.

## Requirements

- **Java 21** or newer (run with any JVM; building also needs JDK 21+)

## Run the app (GUI)

**From the source tree:**
```
./gradlew run
```
(Windows: `gradlew.bat run`)

**From a built jar (any OS, no Gradle needed):**
```
java -jar localts-refresh-token-app-1.0.0-all.jar
```

## Console debug mode

Exchanges a single Microsoft refresh token for a Minecraft token and prints the result. Pass your token as an argument:

```
./gradlew runConsole --args="M.C..."
```

## Build

- `./gradlew build` – compiles, tests nothing, and produces a plain (non-fat) jar.
- `./gradlew shadowJar` – produces the **fat jar** with all dependencies bundled:

  ```
  build/libs/localts-refresh-token-app-1.0.0-all.jar
  ```

One jar runs on all three platforms — just copy it and `java -jar`.

## Platform notes

- **Certificate roots:** on Windows the app also trusts the Windows certificate store; on Linux/macOS it loads the system CA bundle (`/etc/ssl/certs/ca-certificates.crt`, macOS `/etc/ssl/cert.pem`). Corporate/intercepted HTTPS works the same everywhere.
- **macOS:** the local proxy binds to port 25565 — allow the one-time "incoming connections" firewall prompt. The app uses the macOS menu bar.
- **Linux:** the UI falls back to a portable font (Noto Sans / DejaVu / Liberation) if Arial/Segoe UI are unavailable.

## Notes

- Refresh tokens expire after ~90 days — a token embedded in old builds is long dead (`invalid_grant`). Real accounts are managed in the GUI.