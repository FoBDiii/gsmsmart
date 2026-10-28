# GSMsmart Reparaciones

App para dar de alta y seguir las reparaciones de **GSMsmart** (C/ Escorredor, 66 Bajo · 12530 Burriana).
La misma app funciona en **Android** (APK) y en **Windows** (instalador .exe).

## Descargar

Ve a **[Releases](../../releases/latest)** y descarga:

| Dispositivo | Archivo |
|---|---|
| Móvil o tablet Android | `GSMsmart-x.y.z.apk` |
| PC con Windows (instalar) | `GSMsmart-Instalador-x.y.z.exe` |
| PC con Windows (sin instalar) | `GSMsmart-Portable-x.y.z.exe` |

### Instalar en Android
1. Descarga el `.apk` desde el móvil.
2. Ábrelo. La primera vez, Android pedirá permitir instalar apps de esta fuente: acéptalo.
3. Para actualizar, instala el APK nuevo encima. Las reparaciones se conservan.

### Instalar en Windows
1. Ejecuta el instalador. Windows puede mostrar “Windows protegió su PC” porque el programa no tiene firma de pago: pulsa **Más información → Ejecutar de todas formas**.
2. Se crea un acceso directo **GSMsmart** en el escritorio.

## Dónde se guardan los datos
Las reparaciones se guardan dentro de la app, en cada dispositivo. No se pierden al borrar el historial del navegador, pero sí si se desinstala la app o se borran sus datos.
Haz una copia desde **Ajustes → Copia de seguridad** de vez en cuando:
- En Android se guarda en `Descargas/GSMsmart`.
- En Windows eliges dónde guardarla.

## Para desarrollo
- La app está en `www/index.html` (un solo archivo).
- Cada vez que se sube un cambio a `main`, GitHub Actions compila el APK y el .exe y crea una nueva versión en Releases.
- Probar en el PC: `npm install` y `npm start`.
- La clave de firma del APK está en `android/app/gsmsmart.keystore`. **No la pierdas**: sin ella no se pueden instalar actualizaciones encima de la app.
