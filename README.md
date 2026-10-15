# NADOTA AI — versión inicial

Interfaz Android gratuita en cemento mate y azul eléctrico. Incluye dos perfiles de voz TTS (las voces concretas dependen de las instaladas en el teléfono). **No clona voces de artistas.**

**Estado:** prototipo funcional de interfaz y lectura en voz alta. **Todavía no incluye un modelo de IA**, generación de imágenes ni edición PDF. Esas funciones necesitan integrarse después.

## Compilar gratis desde GitHub
1. Crear repositorio `NADOTA` y subir el contenido de esta carpeta a la raíz (incluida `.github`).
2. Abrir **Actions → Compilar APK NADOTA → Run workflow**.
3. Al terminar, abrir el resultado de la ejecución y descargar el artefacto `NADOTA-debug-APK`.
4. Extraer el ZIP del artefacto e instalar `app-debug.apk` en Android, si confías en el archivo.

El APK se compila en GitHub Actions; este ZIP contiene el código fuente, no el APK ya compilado.
