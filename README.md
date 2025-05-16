# App Móvil - Gestión de Cursos y Estudiantes

## Descripción
Esta es una aplicación móvil desarrollada en Android con Jetpack Compose para gestionar cursos y estudiantes. Cada estudiante puede estar inscrito en un único curso.

## Tecnologías
- Android Studio Giraffe o superior
- Kotlin
- Jetpack Compose
- MVVM (Model-View-ViewModel)
- Retrofit + OkHttp
- Room Database
- Firebase Cloud Messaging (FCM)

## Requisitos
- Android Studio
- Dispositivo físico o emulador Android
- API REST corriendo localmente o en servidor
- Sql server corriendo localmente

## Configuración del proyecto

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/KevinCastillo839/AppExamen.git
   ```

2. **Configuración previa**:
   - Asegúrate de que el API está corriendo localmente o accesible públicamente.
   

3. **Compilar y ejecutar**:
   - Abrir el proyecto en Android Studio.
   - Sincronizar dependencias.
   - Presionar el boton Sync Project with gradle files
   - Ejecutar la app en un emulador.

## Características
- CRUD de Cursos (Crear, Leer, Actualizar, Eliminar)
- CRUD de Estudiantes (Crear, Leer, Actualizar, Eliminar)
- Visualización offline de datos usando Room
- Sincronización con el API cuando hay conexión
- Notificaciones push al registrar nuevos estudiantes



## Rama Principal
- `master`
