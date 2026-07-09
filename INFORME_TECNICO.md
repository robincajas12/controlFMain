# Auditoría Técnica del Proyecto

## 1. Resumen

- Objetivo del proyecto: construir una plataforma de auditoría ciudadana para visualizar y comparar leyes, políticos y su coherencia legislativa, con un panel administrativo y una capa de importación de datos desde la Asamblea Nacional. La intención es clara en los controladores, servicios y páginas del frontend.
- Estado actual y avance aproximado: el proyecto tiene una base funcional bastante avanzada, especialmente en el backend y en las pantallas principales. La estimación, basada en lo implementado y en lo que falta para producción, es de aproximadamente 65–75% de avance funcional. El backend compila correctamente; el frontend no compila completamente por errores de TypeScript.
- Fortalezas:
  - Arquitectura en capas relativamente clara en backend.
  - Modelo de dominio bien identificado para leyes, políticos, promesas, votos y coherencia.
  - Frontend con rutas y pantallas principales definidas.
  - Integración con una API externa para importación de votaciones.
- Problemas y riesgos:
  - Falta de autenticación/autorización real.
  - Credenciales de base de datos hardcodeadas.
  - CORS muy permisivo.
  - El frontend presenta errores de compilación.
  - Se usan IDs de usuario hardcodeados en acciones de comentario/calificación.
  - El código contiene lógica de negocio mezclada con mapeo DTO y manejo de errores simplificado.

> Verificación realizada: el backend compila con éxito mediante `./gradlew build`. El frontend falla al compilar con `npm run build` por errores de TypeScript en varios archivos.

## 2. Arquitectura

- Arquitectura implementada: es una arquitectura en capas inspirada en MVC/Layered Architecture, con Spring Boot como base. No es una Clean Architecture ni Hexagonal Architecture estricta: hay controladores, servicios, repositorios y entidades JPA, pero no se observan puertos/adaptadores explícitos ni casos de uso aislados.
- Justificación:
  - El código separa claramente controladores, servicios y persistencia.
  - Los DTOs encapsulan la respuesta de la API.
  - El modelo de negocio está representado en entidades JPA y repositorios.
- Organización de capas:
  - Presentación: frontend React + React Router.
  - API: controladores Spring en [controlF/src/main/java/com/controlf/controller](controlF/src/main/java/com/controlf/controller).
  - Aplicación: servicios en [controlF/src/main/java/com/controlf/service](controlF/src/main/java/com/controlf/service).
  - Persistencia: repositorios en [controlF/src/main/java/com/controlf/db/repository](controlF/src/main/java/com/controlf/db/repository).
  - Dominio: entidades en [controlF/src/main/java/com/controlf/db/schema](controlF/src/main/java/com/controlf/db/schema).

Diagrama textual simple:

```text
React/Vite Frontend
  -> Rutas y páginas
  -> Fetch a /api/...
  -> Spring Boot Controllers
  -> Services
  -> Repositories
  -> PostgreSQL
  -> (opcional) API externa de la Asamblea
```

## 3. Tecnologías

- Backend:
  - Java 21.
  - Spring Boot 4.0.6.
  - Spring Web.
  - Spring Data JPA / Hibernate.
  - PostgreSQL.
  - Lombok.
  - JUnit 5.
- Frontend:
  - React 19.
  - TypeScript.
  - Vite 8.
  - React Router DOM 7.
  - Tailwind CSS via Vite plugin.
- Configuración:
  - [controlF/build.gradle.kts](controlF/build.gradle.kts)
  - [controlf_fronted/package.json](controlf_fronted/package.json)
  - [controlF/src/main/resources/application.properties](controlF/src/main/resources/application.properties)

## 4. Estructura del Proyecto

- Backend:
  - [controlF/src/main/java/com/controlf/controller](controlF/src/main/java/com/controlf/controller): endpoints HTTP.
  - [controlF/src/main/java/com/controlf/service](controlF/src/main/java/com/controlf/service): lógica de negocio.
  - [controlF/src/main/java/com/controlf/db](controlF/src/main/java/com/controlf/db): repositorios y entidades.
  - [controlF/src/main/java/com/controlf/dto](controlF/src/main/java/com/controlf/dto): objetos de transferencia.
  - [controlF/src/main/java/com/controlf/config](controlF/src/main/java/com/controlf/config): configuración de beans.
- Frontend:
  - [controlf_fronted/src/componentes](controlf_fronted/src/componentes): páginas y componentes por módulo.
  - [controlf_fronted/src/App.tsx](controlf_fronted/src/App.tsx): rutas.
  - [controlf_fronted/src/main.tsx](controlf_fronted/src/main.tsx): bootstrap de la app.

## 5. Módulos y Funcionalidades

| Módulo | Propósito | Funcionalidades | Estado |
|---|---|---|---|
| Directorio de políticos | Explorar políticos | Listado, filtros, paginación, perfil | Parcial |
| Perfil político | Detalle del político | Información básica, coherencia, historial, comentarios, calificaciones | Parcial |
| Directorio de leyes | Explorar leyes | Listado, filtros, paginación, perfil | Parcial |
| Perfil de ley | Detalle de ley | Contenido, votación, auditoría, debate ciudadano | Parcial |
| Dashboard | Resumen general | Estadísticas y actividad reciente | Parcial |
| Panel administrativo | Operaciones de administración | Panel, motor de coherencia, mantenimiento, seed, importación de votaciones | Parcial |
| Importación externa | Traer datos desde Asamblea | Obtener asambleístas, votaciones e importarlas | Parcial |

Hecho verificable: los módulos existen y están conectados a endpoints. La implementación es funcional en lo básico, pero no está completa ni refinada para producción.

## 6. Frontend

- Páginas:
  - [controlf_fronted/src/componentes/DashboardPage.tsx](controlf_fronted/src/componentes/DashboardPage.tsx)
  - [controlf_fronted/src/componentes/directorio_politicos/DirectorioPoliticosPage.tsx](controlf_fronted/src/componentes/directorio_politicos/DirectorioPoliticosPage.tsx)
  - [controlf_fronted/src/componentes/directorio_leyes/DirectorioLeyesPage.tsx](controlf_fronted/src/componentes/directorio_leyes/DirectorioLeyesPage.tsx)
  - [controlf_fronted/src/componentes/perfil_politico_screen/PerfilPoliticoPage.tsx](controlf_fronted/src/componentes/perfil_politico_screen/PerfilPoliticoPage.tsx)
  - [controlf_fronted/src/componentes/perfil_ley/PerfilLeyPage.tsx](controlf_fronted/src/componentes/perfil_ley/PerfilLeyPage.tsx)
  - [controlf_fronted/src/componentes/panel_admin/AdminPage.tsx](controlf_fronted/src/componentes/panel_admin/AdminPage.tsx)
- Componentes:
  - El frontend está construido con componentes por sección y subcomponentes reutilizables.
  - Se observa una estructura por dominio: directorio de leyes, directorio de políticos, perfil político, perfil ley, panel admin.
- Hooks, Context, Store:
  - No se encontró un store global ni Context API.
  - El estado se administra con `useState` y `useEffect` en cada página.
- Manejo de estado:
  - Local y component-level.
  - No se detecta persistencia de estado ni sincronización global.
- Navegación y rutas:
  - Definidas en [controlf_fronted/src/App.tsx](controlf_fronted/src/App.tsx):
    - `/`
    - `/dashboard`
    - `/politico/:id`
    - `/leyes`
    - `/ley/:id`
    - `/admin`
- Consumo de APIs:
  - Se usa `fetch` directo en los componentes, por ejemplo en [controlf_fronted/src/componentes/directorio_politicos/DirectorioPoliticosPage.tsx](controlf_fronted/src/componentes/directorio_politicos/DirectorioPoliticosPage.tsx) y [controlf_fronted/src/componentes/perfil_politico_screen/PerfilPoliticoPage.tsx](controlf_fronted/src/componentes/perfil_politico_screen/PerfilPoliticoPage.tsx).

## 7. Backend

- Endpoints:
  - Leyes: [controlF/src/main/java/com/controlf/controller/LeyController.java](controlF/src/main/java/com/controlf/controller/LeyController.java)
  - Políticos: [controlF/src/main/java/com/controlf/controller/PoliticoController.java](controlF/src/main/java/com/controlf/controller/PoliticoController.java)
  - Dashboard: [controlF/src/main/java/com/controlf/controller/DashboardController.java](controlF/src/main/java/com/controlf/controller/DashboardController.java)
  - Admin: [controlF/src/main/java/com/controlf/controller/AdminController.java](controlF/src/main/java/com/controlf/controller/AdminController.java)
  - Importación de votaciones: [controlF/src/main/java/com/controlf/controller/AssemblyImportController.java](controlF/src/main/java/com/controlf/controller/AssemblyImportController.java)
- Controladores:
  - Exponen endpoints REST con `@RestController`.
  - Se usan `@CrossOrigin("*")` y `@RequestMapping`.
- Servicios:
  - [controlF/src/main/java/com/controlf/service/LeyService.java](controlF/src/main/java/com/controlf/service/LeyService.java)
  - [controlF/src/main/java/com/controlf/service/PoliticoService.java](controlF/src/main/java/com/controlf/service/PoliticoService.java)
  - [controlF/src/main/java/com/controlf/service/AdminService.java](controlF/src/main/java/com/controlf/service/AdminService.java)
  - [controlF/src/main/java/com/controlf/service/AssemblyImportService.java](controlF/src/main/java/com/controlf/service/AssemblyImportService.java)
- Casos de uso:
  - Consulta de leyes y políticos.
  - Comentarios y calificaciones.
  - Cálculo de coherencia.
  - Importación masiva de datos.
  - Panel administrativo.
- Repositorios:
  - Se implementa el patrón Repository con interfaces Spring Data JPA, por ejemplo [controlF/src/main/java/com/controlf/db/repository/LeyRepository.java](controlF/src/main/java/com/controlf/db/repository/LeyRepository.java) y [controlF/src/main/java/com/controlf/db/repository/PoliticoRepository.java](controlF/src/main/java/com/controlf/db/repository/PoliticoRepository.java).
- Entidades:
  - [controlF/src/main/java/com/controlf/db/schema/Ley.java](controlF/src/main/java/com/controlf/db/schema/Ley.java)
  - [controlF/src/main/java/com/controlf/db/schema/Politico.java](controlF/src/main/java/com/controlf/db/schema/Politico.java)
  - [controlF/src/main/java/com/controlf/db/schema/Promesa.java](controlF/src/main/java/com/controlf/db/schema/Promesa.java)
  - [controlF/src/main/java/com/controlf/db/schema/VinculoPromesaLey.java](controlF/src/main/java/com/controlf/db/schema/VinculoPromesaLey.java)
  - [controlF/src/main/java/com/controlf/db/schema/Usuario.java](controlF/src/main/java/com/controlf/db/schema/Usuario.java)
- DTOs:
  - Hay una amplia colección en [controlF/src/main/java/com/controlf/dto](controlF/src/main/java/com/controlf/dto).
- Middleware:
  - No se detecta un stack de middleware propio de seguridad.
  - Sí existe CORS en [controlF/src/main/java/com/controlf/WebConfig.java](controlF/src/main/java/com/controlf/WebConfig.java).
  - Hay un `@ExceptionHandler` en [controlF/src/main/java/com/controlf/controller/AssemblyImportController.java](controlF/src/main/java/com/controlf/controller/AssemblyImportController.java).
- Autenticación y autorización:
  - No se encontró implementación real de JWT, filtros, roles aplicados a endpoints o sesiones.
  - En [controlF/src/main/resources/application.properties](controlF/src/main/resources/application.properties) se deshabilita explícitamente la seguridad de Spring.

## 8. Base de Datos y APIs

- Modelos/tablas inferidos:
  - `leyes`
  - `politicos`
  - `promesas`
  - `votos`
  - `usuarios`
  - `comentarios`
  - `calificaciones`
  - `vinculos_promesa_ley`
  - `configuraciones`
  - `reportes`
  - `logs_sistema`
- Relaciones:
  - Un político tiene muchas promesas.
  - Una promesa pertenece a un político.
  - Un vínculo une una promesa con una ley.
  - Una ley tiene votos, comentarios y calificaciones.
  - Un político tiene votos, comentarios y calificaciones.
  - Un usuario participa en comentarios y calificaciones.
- ORM:
  - Hibernate/JPA mediante Spring Data JPA.
- Resumen de endpoints disponibles:
  - `GET /api/leyes`
  - `GET /api/leyes/filtros`
  - `GET /api/leyes/{id}/perfil`
  - `POST /api/leyes/{id}/comentarios`
  - `POST /api/leyes/{id}/calificaciones`
  - `GET /api/politicos`
  - `GET /api/politicos/filtros`
  - `GET /api/politicos/{id}`
  - `POST /api/politicos/{id}/comentarios`
  - `POST /api/politicos/{id}/calificaciones`
  - `GET /api/dashboard/stats`
  - `GET /api/admin/...`
  - `GET /admin/assembly-members/...`

## 9. Patrones y Calidad del Código

- Patrones observados:
  - Repository Pattern.
  - Service Layer.
  - DTOs para transporte.
  - Dependency Injection con Lombok + Spring.
- Uso de SOLID:
  - Hay cierta separación de responsabilidades, pero hay mezcla de lógica de negocio y mapeo en los servicios.
  - No se observa una separación estricta entre dominio, aplicación e infraestructura.
- Organización:
  - Buena para un prototipo o MVP.
  - Menos madura para una aplicación empresarial.
- Acoplamiento/cohesión:
  - Cohesión razonable por módulos.
  - Acoplamiento algo alto entre servicios y entidades JPA.
  - El frontend está muy acoplado a los endpoints y a los datos esperados.

## 10. Seguridad y Configuración

- Variables de entorno:
  - No se detectan variables de entorno para base de datos ni secretos. La configuración está hardcodeada en [controlF/src/main/resources/application.properties](controlF/src/main/resources/application.properties).
- Gestión de secretos:
  - No se encontró un gestor de secretos ni placeholders seguros.
- JWT/Auth:
  - No se encontró implementación de JWT ni autenticación real.
- Validaciones:
  - Existen algunos `orElseThrow()` y conversiones de enum, pero no hay un sistema de validación robusto visible.
- Riesgos detectados:
  - Seguridad desactivada explícitamente.
  - CORS abierto a `*`.
  - Contraseñas y credenciales en texto plano.
  - IDs de usuario hardcodeados.
  - No hay protección de endpoints sensibles.

## 11. Deuda Técnica

Prioridad alta:
- Corregir errores de TypeScript del frontend.
- Implementar autenticación/autorización real.
- Eliminar credenciales hardcodeadas y usar variables de entorno.
- Reemplazar `usuarioId: 1` por identidad real.

Prioridad media:
- Mejorar validaciones de entrada y manejo de excepciones.
- Separar más claramente mapeo DTO y lógica de negocio.
- Revisar el flujo de filtros y búsqueda en el frontend para evitar comportamientos inconsistentes.

Prioridad baja:
- Limpiar interfaces/DTOs no usados o mal nombrados.
- Revisar comentarios temporales y código de ejemplo.
- Mejorar la calidad de los mensajes de error.

## 12. Conclusión

| Área | Estado |
|---|---|
| Frontend | Parcial |
| Backend | Parcial |
| Base de datos | Parcial |
| APIs | Parcial |
| Seguridad | Incompleto |
| Arquitectura | Parcialmente sólida |
| Calidad del código | Media |

- ¿Está listo para producción? No.
- ¿Qué falta para considerarlo completo? Autenticación, seguridad real, validaciones robustas, despliegue y configuración segura, corrección de errores del frontend, tests más amplios y mayor estabilidad de integración.
- Prioridades técnicas inmediatas:
  1. Corregir el build del frontend.
  2. Implementar seguridad y auth.
  3. Mover secretos a variables de entorno.
  4. Refinar los flujos de negocio y el manejo de errores.
