# Documento de Reutilización de Software — ControlF

## Control de versiones

| Versión | Fecha | Autor | Descripción |
|---|---|---|---|
| 1.0 | 2026-07-15 | Equipo de Ingeniería / Documentación asistida | Versión inicial del documento de reutilización |

---

## 1. Introducción

El presente documento describe los elementos de software del proyecto **ControlF** —una plataforma de auditoría ciudadana para el seguimiento de leyes, políticos y su coherencia legislativa, compuesta por un backend en Spring Boot (`controlF/`) y un frontend en React/TypeScript (`controlf_fronted/`)— que pueden ser reutilizados, total o parcialmente, en el desarrollo de nuevos proyectos o en la evolución del propio sistema.

Siguiendo el enfoque de Ian Sommerville en *Ingeniería de Software* sobre reutilización de software, este documento no se limita a listar bibliotecas de terceros, sino que identifica y documenta **activos reutilizables propios**: componentes de aplicación, marcos de trabajo internos (frameworks propios), patrones de diseño aplicados de forma consistente y elementos de dominio genéricos que pueden desprenderse del contexto específico de auditoría legislativa.

El análisis se basa exclusivamente en la evidencia disponible en el código fuente del repositorio (`controlF/src/main/java/com/controlf`, `controlf_fronted/src`), en la documentación técnica existente (`INFORME_TECNICO.md`, `README.md`, `casos_de_uso_checklist.md`) y en la configuración de build (`build.gradle.kts`, `package.json`). No se incorpora información no verificable en dichas fuentes.

## 2. Objetivo

Elaborar un inventario técnico y una descripción normalizada de los componentes de software de ControlF que cumplen criterios de reutilización, con el fin de:

- Facilitar su identificación y localización por parte de futuros equipos de desarrollo.
- Reducir el esfuerzo de reimplementación de funcionalidades transversales (autenticación, integración HTTP, componentes de interfaz genéricos) en nuevos módulos o proyectos.
- Establecer criterios objetivos que permitan distinguir entre software reutilizable y software específico del dominio.
- Dejar constancia explícita de los riesgos y limitaciones técnicas asociados a la reutilización de cada componente.

## 3. Alcance

Este documento cubre:

- El código fuente del backend ubicado en `controlF/src/main/java/com/controlf` (paquetes `auth`, `config`, `controller`, `service`, `db.repository`, `db.schema`, `dto`, y clases raíz `WebConfig`, `ControlFApplication`).
- El código fuente del frontend ubicado en `controlf_fronted/src` (`context`, `componentes`, `types`).
- La configuración de dependencias y build (`build.gradle.kts`, `package.json`).
- La documentación técnica y de casos de uso existente en la raíz del repositorio.

Quedan fuera del alcance: las librerías de terceros de código abierto utilizadas (Spring Boot, React, jjwt, etc.), que se documentan únicamente como **dependencias** de los componentes propios que las emplean, no como activos reutilizables propios del proyecto; y los archivos de infraestructura (`Dockerfile`, `docker-compose.yml`), que no constituyen software reutilizable en el sentido de Sommerville sino configuración de despliegue.

## 4. Criterios de reutilización adoptados (según Sommerville)

De acuerdo con Sommerville, un componente es candidato a reutilización cuando su **interfaz** puede describirse con independencia de su implementación y cuando su lógica no está irremediablemente acoplada a un contexto de negocio único. Para clasificar cada elemento del sistema se aplicaron los siguientes criterios, adaptados de los factores de reutilización que propone (madurez, granularidad, adaptabilidad y documentación disponible):

1. **Independencia de dominio**: el componente no requiere conocimiento de las entidades de negocio de ControlF (`Ley`, `Politico`, `Promesa`, `Voto`) para funcionar.
2. **Interfaz bien definida**: expone una API pública (métodos, props, endpoints) clara y estable, separada de su implementación interna.
3. **Bajo acoplamiento**: sus dependencias externas son librerías de propósito general (Spring Security, `jjwt`, React Router) y no otros componentes específicos del dominio.
4. **Configurabilidad**: los aspectos variables (URLs, claves, rutas protegidas, roles) están parametrizados mediante propiedades, variables de entorno o props, en lugar de estar embebidos en el código.
5. **Evidencia de generalidad**: el componente resuelve un problema transversal reconocible en otros sistemas (autenticación, paginación, logging, integración HTTP), no un problema exclusivo de auditoría legislativa.

Un componente que cumple los cinco criterios se clasifica como **Reutilizable directo** (puede trasladarse a otro proyecto con cambios mínimos de configuración). Un componente que cumple parcialmente se clasifica como **Reutilizable como patrón** (su diseño o estructura es replicable, pero el código concreto requiere adaptación). Un componente que incumple los criterios 1 y 3 se clasifica como **No reutilizable** (ver sección 7).

## 5. Inventario de componentes reutilizables

| Nº | Componente | Ubicación | Categoría | Clasificación |
|---|---|---|---|---|
| 1 | `JwtService` | `controlF/.../auth/JwtService.java` | Backend — Seguridad | Reutilizable directo |
| 2 | `AuthenticatedUser` | `controlF/.../auth/AuthenticatedUser.java` | Backend — Seguridad | Reutilizable directo |
| 3 | `JwtAuthenticationFilter` | `controlF/.../auth/JwtAuthenticationFilter.java` | Backend — Seguridad | Reutilizable como patrón |
| 4 | Beans de `SecurityConfig` (`passwordEncoder`, `authenticationManager`) | `controlF/.../auth/SecurityConfig.java` | Backend — Seguridad | Reutilizable directo |
| 5 | `CustomUserDetailsService` | `controlF/.../auth/CustomUserDetailsService.java` | Backend — Seguridad | Reutilizable como patrón |
| 6 | `AppConfig` (bean `RestTemplate`) | `controlF/.../config/AppConfig.java` | Backend — Configuración | Reutilizable directo |
| 7 | `WebConfig` (CORS) | `controlF/.../WebConfig.java` | Backend — Configuración | Reutilizable directo |
| 8 | `HolaController` (fallback SPA) | `controlF/.../controller/HolaController.java` | Backend — Infraestructura web | Reutilizable directo |
| 9 | `GeminiService` (motor de integración) | `controlF/.../service/GeminiService.java` | Backend — Integración externa | Reutilizable como patrón |
| 10 | `AssemblyImportService` (motor de integración HTTP) | `controlF/.../service/AssemblyImportService.java` | Backend — Integración externa | Reutilizable como patrón |
| 11 | Interfaces `JpaRepository` (patrón Repository) | `controlF/.../db/repository/*.java` | Backend — Persistencia | Reutilizable como patrón |
| 12 | Entidad `LogSistema` | `controlF/.../db/schema/LogSistema.java` | Backend — Dominio genérico | Reutilizable como patrón |
| 13 | Entidad `AccionSistema` | `controlF/.../db/schema/AccionSistema.java` | Backend — Dominio genérico | Reutilizable como patrón |
| 14 | Entidad `Configuracion` (clave-valor) | `controlF/.../db/schema/Configuracion.java` | Backend — Dominio genérico | Reutilizable como patrón |
| 15 | Patrón DTO (capa `dto/`) | `controlF/.../dto/*.java` | Backend — Patrón arquitectónico | Reutilizable como patrón |
| 16 | `AuthContext` / hook `useAuth` | `controlf_fronted/src/context/AuthContext.tsx` | Frontend — Autenticación | Reutilizable como patrón |
| 17 | `ProtectedRoute` | `controlf_fronted/src/componentes/auth/ProtectedRoute.tsx` | Frontend — Autenticación | Reutilizable directo |
| 18 | `authValidation.ts` | `controlf_fronted/src/componentes/auth/authValidation.ts` | Frontend — Utilidades | Reutilizable directo |
| 19 | `SearchableSelect` | `controlf_fronted/src/componentes/panel_admin/components/SearchableSelect.tsx` | Frontend — UI genérica | Reutilizable directo |
| 20 | `Paginacion` | `controlf_fronted/src/componentes/directorio_politicos/Componente pie pagina/Paginacion.tsx` | Frontend — UI genérica | Reutilizable directo |
| 21 | `MainLayout` | `controlf_fronted/src/componentes/layout/MainLayout.tsx` | Frontend — Layout | Reutilizable como patrón |
| 22 | Barras de búsqueda con filtros configurables (`BarraBusqueda.tsx`, `BarraBusquedaLeyes.tsx`) | `controlf_fronted/src/componentes/directorio_*/` | Frontend — UI genérica | Reutilizable como patrón |

## 6. Descripción detallada de los componentes reutilizables

### 6.1 `JwtService`

- **Nombre**: `JwtService`
- **Ubicación**: `controlF/src/main/java/com/controlf/auth/JwtService.java`
- **Descripción y propósito**: encapsula la emisión y validación de tokens JWT mediante la librería `jjwt` (`io.jsonwebtoken:jjwt-api:0.12.6`). Normaliza el secreto configurado mediante un hash SHA-256 antes de derivar la clave HMAC de firma.
- **Responsabilidades**: generar tokens firmados a partir de un correo y un rol; parsear y validar tokens recibidos, exponiendo sus *claims*.
- **Funcionalidades que ofrece**: emisión de token con expiración configurable; validación de firma e integridad; extracción de *claims* (`email`, `role`).
- **Interfaz pública**:
  - `String generateToken(String email, String role)`
  - `Claims parseToken(String token)` — lanza `io.jsonwebtoken.JwtException` si el token es inválido o expiró.
- **Entradas y salidas**: entrada — correo electrónico y rol (`String`) para generación; token (`String`) para validación. Salida — token JWT firmado (`String`) o un objeto `Claims` con la información decodificada.
- **Dependencias y requisitos para su reutilización**: `io.jsonwebtoken:jjwt-api`, `jjwt-impl` y `jjwt-jackson` (versión 0.12.6 o compatible); propiedades de configuración `app.jwt.secret` y `app.jwt.expiration-hours` (esta última con valor por defecto de 24 horas si no se define).
- **Restricciones o limitaciones**: el código contiene un valor de secreto embebido como *fallback* si no se define `app.jwt.secret`; este *fallback* **no debe usarse en producción** y debe sustituirse por una variable de entorno obligatoria antes de reutilizar el componente fuera de un entorno de desarrollo.
- **Escenarios de reutilización**: cualquier backend Spring Boot que requiera autenticación *stateless* basada en tokens, con claims mínimos de identidad (correo) y autorización (rol).
- **Beneficios de reutilizar el componente**: evita reimplementar la lógica de firma/verificación JWT y la derivación segura de claves; su interfaz de dos métodos es mínima y fácil de integrar.
- **Riesgos o consideraciones técnicas**: la presencia de un secreto por defecto en el código fuente constituye un riesgo si se reutiliza sin revisión; se recomienda eliminar el *fallback* y exigir la propiedad `app.jwt.secret` como obligatoria en el nuevo proyecto.
- **Recomendaciones para su integración**: copiar la clase junto con `JwtAuthenticationFilter` y `AuthenticatedUser`; parametrizar el nombre de los claims si el nuevo dominio requiere más de `email`/`role`.

### 6.2 `AuthenticatedUser`

- **Nombre**: `AuthenticatedUser`
- **Ubicación**: `controlF/src/main/java/com/controlf/auth/AuthenticatedUser.java`
- **Descripción y propósito**: adaptador que implementa la interfaz `org.springframework.security.core.userdetails.UserDetails`, envolviendo los campos mínimos de identidad (`id`, `email`, `nombre`, `role`, `passwordHash`, `activo`) requeridos por Spring Security.
- **Responsabilidades**: representar el principal autenticado dentro del contexto de seguridad de Spring; traducir el rol de negocio a una autoridad de Spring Security (`ROLE_<rol>`).
- **Funcionalidades que ofrece**: `getAuthorities()`, `getPassword()`, `getUsername()`, indicadores de estado de cuenta (`isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`) fijos en `true`, `isEnabled()` ligado al campo `activo`.
- **Interfaz pública**: constructor con `id`, `email`, `nombre`, `role`, `passwordHash`, `activo`; getters `getId()`, `getNombre()`, `getRole()`.
- **Entradas y salidas**: entrada — datos primitivos de un usuario. Salida — objeto conforme al contrato `UserDetails` de Spring Security.
- **Dependencias y requisitos para su reutilización**: `spring-security-core`. No depende de la entidad `Usuario` del dominio ni de ningún repositorio.
- **Restricciones o limitaciones**: asume un modelo de rol único por usuario (no roles múltiples).
- **Escenarios de reutilización**: cualquier proyecto Spring Security que module identidad de usuario con un rol simple y no requiera lógica de expiración de cuenta o credenciales.
- **Beneficios de reutilizar el componente**: elimina la necesidad de escribir un adaptador `UserDetails` desde cero.
- **Riesgos o consideraciones técnicas**: si el nuevo dominio requiere múltiples roles por usuario, la clase debe extenderse antes de reutilizarse.
- **Recomendaciones para su integración**: usar junto con `CustomUserDetailsService` como referencia de patrón, sustituyendo la fuente de datos por el repositorio del nuevo dominio.

### 6.3 `JwtAuthenticationFilter`

- **Nombre**: `JwtAuthenticationFilter`
- **Ubicación**: `controlF/src/main/java/com/controlf/auth/JwtAuthenticationFilter.java`
- **Descripción y propósito**: filtro `OncePerRequestFilter` que intercepta cada solicitud HTTP, extrae el token del encabezado `Authorization: Bearer`, lo valida mediante `JwtService` y reconstruye el principal de seguridad.
- **Responsabilidades**: aplicar autenticación *stateless* en cada solicitud; delegar la reconstrucción del usuario autenticado; limpiar el contexto de seguridad ante tokens inválidos.
- **Funcionalidades que ofrece**: filtrado transparente compatible con la cadena de filtros de Spring Security; manejo defensivo de errores de parseo (los trata como solicitud no autenticada, sin interrumpir el flujo).
- **Interfaz pública**: se integra como filtro mediante `HttpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)` en `SecurityConfig`.
- **Entradas y salidas**: entrada — `HttpServletRequest` con encabezado `Authorization`. Salida — efecto lateral sobre `SecurityContextHolder` (autenticación establecida o contexto limpio).
- **Dependencias y requisitos para su reutilización**: `JwtService` y `CustomUserDetailsService` (o equivalentes del nuevo dominio); `spring-security-web`.
- **Restricciones o limitaciones**: la reconstrucción del principal está acoplada a `CustomUserDetailsService`; para reutilizar el filtro tal cual, el nuevo proyecto debe implementar un servicio de carga de usuario compatible con la misma firma.
- **Escenarios de reutilización**: cualquier API REST Spring Boot que requiera autenticación JWT sin sesiones de servidor.
- **Beneficios de reutilizar el componente**: patrón de filtro JWT ya probado y libre de errores comunes (p. ej., no capturar excepciones de parseo).
- **Riesgos o consideraciones técnicas**: el manejo de excepciones es genérico (`catch (Exception e)`); en un contexto que requiera distinguir tipos de error de autenticación (token expirado vs. malformado) debe extenderse.
- **Recomendaciones para su integración**: mantener el orden de registro antes de `UsernamePasswordAuthenticationFilter`; sustituir `CustomUserDetailsService` por la implementación del nuevo dominio.

### 6.4 Beans de configuración de seguridad (`SecurityConfig`)

- **Nombre**: `passwordEncoder()` y `authenticationManager(PasswordEncoder)`
- **Ubicación**: `controlF/src/main/java/com/controlf/auth/SecurityConfig.java`
- **Descripción y propósito**: `passwordEncoder()` expone un `BCryptPasswordEncoder`; `authenticationManager(...)` construye un `ProviderManager` con `DaoAuthenticationProvider`.
- **Responsabilidades**: proveer los mecanismos estándar de codificación de contraseñas y autenticación por credenciales de Spring Security.
- **Interfaz pública**: beans de tipo `PasswordEncoder` y `AuthenticationManager`, inyectables en cualquier servicio del contexto Spring.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-security`.
- **Restricciones o limitaciones**: ninguna específica de dominio; son configuración estándar de Spring Security.
- **Escenarios de reutilización**: cualquier proyecto Spring Boot que autentique usuarios por correo/contraseña.
- **Beneficios de reutilizar el componente**: configuración correcta y probada, evita errores comunes de instanciación manual de `AuthenticationManager` en versiones recientes de Spring Security.
- **Riesgos o consideraciones técnicas**: la matriz de autorización de rutas (`securityFilterChain`) definida en la misma clase **no** es reutilizable directamente, ya que hardcodea rutas del dominio ControlF (`/api/leyes/**`, `/api/admin/**`); debe documentarse por separado como componente específico (ver sección 7).
- **Recomendaciones para su integración**: extraer únicamente los métodos `passwordEncoder()` y `authenticationManager(...)` a una clase de configuración base; redefinir `securityFilterChain` para las rutas del nuevo proyecto.

### 6.5 `CustomUserDetailsService`

- **Nombre**: `CustomUserDetailsService`
- **Ubicación**: `controlF/src/main/java/com/controlf/auth/CustomUserDetailsService.java`
- **Descripción y propósito**: implementa `UserDetailsService.loadUserByUsername`, delegando en `UsuarioRepository.findByEmail` y mapeando el resultado a `AuthenticatedUser`.
- **Responsabilidades**: puente entre la capa de persistencia de usuarios y el contrato de Spring Security.
- **Interfaz pública**: `UserDetails loadUserByUsername(String username)`.
- **Dependencias y requisitos para su reutilización**: `UsuarioRepository` (o repositorio equivalente) y la entidad `Usuario` del dominio.
- **Restricciones o limitaciones**: acoplado a la entidad `Usuario` de ControlF; **no es reutilizable directo**, sino como patrón de implementación (cargar por email vía repositorio Spring Data JPA y mapear a un adaptador `UserDetails`).
- **Escenarios de reutilización**: proyectos Spring Data JPA que necesiten integrar su propia tabla de usuarios con Spring Security.
- **Recomendaciones para su integración**: replicar la estructura (una consulta + un mapeo) sustituyendo el repositorio y la entidad de origen.

### 6.6 `AppConfig` (bean `RestTemplate`)

- **Nombre**: `AppConfig`
- **Ubicación**: `controlF/src/main/java/com/controlf/config/AppConfig.java`
- **Descripción y propósito**: expone un bean `RestTemplate` sin configuración adicional (sin *interceptors*, *timeouts* personalizados ni *error handlers*).
- **Responsabilidades**: centralizar la instancia de cliente HTTP síncrono usada por los servicios de integración (`AssemblyImportService`, `GeminiService`).
- **Interfaz pública**: bean `RestTemplate restTemplate()`.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-web`.
- **Restricciones o limitaciones**: no incluye configuración de *timeouts*; en un proyecto que consuma servicios externos con SLA estrictos debería complementarse con un `ClientHttpRequestFactory` configurado.
- **Escenarios de reutilización**: cualquier proyecto Spring Boot que necesite un cliente HTTP centralizado inyectable.
- **Beneficios de reutilizar el componente**: evita instanciar `RestTemplate` de forma dispersa en distintos servicios.
- **Recomendaciones para su integración**: al reutilizar, se recomienda añadir configuración explícita de *timeouts* de conexión y lectura, ausente en la versión actual.

### 6.7 `WebConfig` (configuración CORS)

- **Nombre**: `WebConfig`
- **Ubicación**: `controlF/src/main/java/com/controlf/WebConfig.java`
- **Descripción y propósito**: implementa `WebMvcConfigurer.addCorsMappings` para habilitar CORS sobre `/**` desde un origen configurable.
- **Responsabilidades**: permitir que el frontend SPA, servido desde un origen distinto en desarrollo, consuma la API sin bloqueo del navegador.
- **Interfaz pública**: método `addCorsMappings(CorsRegistry registry)`, controlado por la propiedad `app.cors.allowed-origin` (valor por defecto `http://localhost:5173`).
- **Entradas y salidas**: entrada — propiedad de configuración de origen permitido. Salida — cabeceras CORS en las respuestas HTTP.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-web`.
- **Restricciones o limitaciones**: solo admite un único origen permitido a través de una propiedad simple; para múltiples orígenes debe adaptarse a una lista.
- **Escenarios de reutilización**: cualquier backend Spring MVC/Boot que sirva un SPA (React, Vue, Angular) desde un origen o puerto distinto.
- **Beneficios de reutilizar el componente**: configuración CORS mínima y correcta, parametrizable sin recompilar.
- **Recomendaciones para su integración**: copiar la clase y ajustar el valor por defecto de `app.cors.allowed-origin`; si se requieren múltiples orígenes, extender la propiedad a una lista separada por comas.

### 6.8 `HolaController` (fallback de enrutamiento SPA)

- **Nombre**: `HolaController`
- **Ubicación**: `controlF/src/main/java/com/controlf/controller/HolaController.java`
- **Descripción y propósito**: reenvía (`forward:/index.html`) cualquier ruta sin extensión no reconocida por el backend hacia el `index.html` estático, permitiendo que el enrutador del lado del cliente (React Router) resuelva la navegación de rutas profundas.
- **Responsabilidades**: soportar recarga de página en rutas de una SPA embebida dentro del mismo *jar* de Spring Boot (`src/main/resources/static`).
- **Interfaz pública**: mapeo de rutas comodín gestionado internamente; no requiere invocación directa.
- **Dependencias y requisitos para su reutilización**: que el frontend compilado (`dist/`) se copie a `src/main/resources/static`, tal como se documenta en `README.md`.
- **Restricciones o limitaciones**: asume que las rutas de la API están bajo un prefijo reservado (`/api/**`, `/admin/**`) para no colisionar con el *forward*.
- **Escenarios de reutilización**: cualquier proyecto Spring Boot que embeba y sirva una SPA construida con un enrutador del lado del cliente.
- **Beneficios de reutilizar el componente**: resuelve un problema común (error 404 al recargar rutas profundas de una SPA) con una solución mínima.
- **Recomendaciones para su integración**: verificar que el patrón de exclusión de rutas de API no colisione con los prefijos usados en el nuevo proyecto.

### 6.9 `GeminiService` (motor de integración con IA generativa)

- **Nombre**: `GeminiService`
- **Ubicación**: `controlF/src/main/java/com/controlf/service/GeminiService.java`
- **Descripción y propósito**: cliente REST hacia la API de Google Gemini (`generativelanguage.googleapis.com`), que construye la solicitud, adjunta la clave de API y modelo por configuración, y parsea la respuesta `generateContent`.
- **Responsabilidades**: abstraer la comunicación HTTP con el servicio de IA generativa; traducir errores de red o de la API en `ResponseStatusException` (503).
- **Funcionalidades que ofrece**: construcción de solicitud (`GeminiRequest`), envío vía `RestTemplate`, deserialización tolerante a campos desconocidos (`@JsonIgnoreProperties(ignoreUnknown = true)`).
- **Interfaz pública**: método de negocio `generarExplicacion(String titulo, String descripcionOriginal)` (específico de dominio) que internamente usa un mecanismo de solicitud/respuesta genérico.
- **Entradas y salidas**: entrada — `app.gemini.api-key`, `app.gemini.model` (propiedades); texto de *prompt*. Salida — texto generado por el modelo o excepción HTTP 503 ante fallo.
- **Dependencias y requisitos para su reutilización**: `RestTemplate` (bean de `AppConfig`), clave de API de Google Gemini válida, `jackson-databind`.
- **Restricciones o limitaciones**: el método público expuesto (`generarExplicacion`) está acoplado al dominio (arma un *prompt* específico sobre leyes); para reutilizar el **mecanismo** de llamada a Gemini en otro proyecto es necesario extraer la construcción de la solicitud/respuesta (`GeminiRequest`/`GeminiResponse`) a un método genérico que reciba el *prompt* como parámetro.
- **Escenarios de reutilización**: cualquier backend Spring Boot que necesite integrar generación de texto con la API de Gemini.
- **Beneficios de reutilizar el componente**: evita reimplementar el mapeo del formato de solicitud/respuesta de la API de Gemini, incluyendo manejo de campos desconocidos.
- **Riesgos o consideraciones técnicas**: la clave de API es un dato sensible; debe gestionarse mediante variables de entorno o un gestor de secretos, no en texto plano en `application.properties`.
- **Recomendaciones para su integración**: refactorizar antes de reutilizar, separando un método genérico `generarTexto(String prompt)` del método de dominio `generarExplicacion(...)`.

### 6.10 `AssemblyImportService` (motor de integración con API externa)

- **Nombre**: `AssemblyImportService`
- **Ubicación**: `controlF/src/main/java/com/controlf/service/AssemblyImportService.java`
- **Descripción y propósito**: cliente de integración contra la API pública de la Asamblea Nacional, que obtiene asambleístas y votaciones, y los mapea a entidades del dominio mediante DTOs intermedios (`AssemblyMemberDTO`, `VotingDTO`).
- **Responsabilidades**: encapsular las llamadas HTTP, el mapeo de payloads externos y la construcción de resultados tipados de importación.
- **Funcionalidades que ofrece**: obtención de listado de asambleístas, obtención de detalle de votaciones, resultado de importación estructurado (`ImportResultDTO`).
- **Interfaz pública**: métodos de servicio invocados desde `AssemblyImportController` y `PoliticoImportController`.
- **Entradas y salidas**: entrada — parámetros de consulta hacia la API externa. Salida — `ImportResultDTO` con conteos de éxito/error y detalle de incidencias.
- **Dependencias y requisitos para su reutilización**: `RestTemplate`, `ObjectMapper`, disponibilidad y estabilidad de la API externa de la Asamblea Nacional.
- **Restricciones o limitaciones**: la URL base y la estructura del payload están acopladas a esa API concreta; **no reutilizable directo**, pero el patrón arquitectónico —cliente HTTP + mapeo a DTO + resultado tipado de importación— es replicable para construir otros conectores de importación masiva.
- **Escenarios de reutilización**: como plantilla para nuevos servicios de importación desde fuentes de datos externas (otras APIs gubernamentales o de terceros).
- **Beneficios de reutilizar el componente**: reduce el tiempo de diseño de nuevos conectores al proveer una estructura de resultado (`ImportResultDTO`) ya validada en producción.
- **Riesgos o consideraciones técnicas**: no se observó manejo de reintentos, *circuit breaker* ni *timeouts* explícitos; se recomienda añadirlos si el nuevo conector opera contra una fuente menos confiable.
- **Recomendaciones para su integración**: usar como referencia de diseño, no como código copiable tal cual.

### 6.11 Patrón Repository (`db/repository/*.java`)

- **Nombre**: interfaces `JpaRepository`/`JpaSpecificationExecutor` (p. ej. `LeyRepository`, `PoliticoRepository`, `UsuarioRepository`, `VotoRepository`)
- **Ubicación**: `controlF/src/main/java/com/controlf/db/repository/`
- **Descripción y propósito**: interfaces de acceso a datos basadas en Spring Data JPA, sin implementación propia, que exponen métodos derivados por convención de nombres (p. ej. `findByEmail`, `findByCodigo`, `existsByExternalId`, `findDistinctCategorias`).
- **Responsabilidades**: abstraer el acceso a la base de datos relacional para cada entidad de dominio.
- **Interfaz pública**: métodos CRUD heredados de `JpaRepository`, más métodos derivados específicos de cada entidad.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-data-jpa`, un motor de base de datos compatible con JPA (PostgreSQL en producción, H2 en pruebas).
- **Restricciones o limitaciones**: cada interfaz concreta está acoplada a su entidad de dominio; lo reutilizable es el **patrón** (declarar una interfaz que extiende `JpaRepository<Entidad, ID>` y añadir métodos derivados), no las interfaces en sí.
- **Escenarios de reutilización**: cualquier proyecto Spring Data JPA se beneficia de seguir esta misma convención de nomenclatura y de uso de métodos derivados en lugar de consultas manuales cuando sea posible.
- **Beneficios de reutilizar el componente**: reduce código repetitivo de acceso a datos y mantiene consistencia entre repositorios.

### 6.12 Entidad `LogSistema`

- **Nombre**: `LogSistema`
- **Ubicación**: `controlF/src/main/java/com/controlf/db/schema/LogSistema.java`
- **Descripción y propósito**: entidad JPA que registra acciones administrativas (tabla `logs_sistema`), con campos genéricos: identificador, acción, detalles en texto, fecha y usuario.
- **Responsabilidades**: soporte de auditoría/bitácora de acciones del sistema.
- **Funcionalidades que ofrece**: persistencia de eventos administrativos, consultable vía `LogSistemaRepository`.
- **Interfaz pública**: entidad JPA estándar, sin lógica de negocio embebida.
- **Entradas y salidas**: entrada — datos del evento a registrar. Salida — registro persistido, consultable por fecha/usuario/acción.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-data-jpa`.
- **Restricciones o limitaciones**: no contiene campos específicos del dominio legislativo; es genérica de por sí.
- **Escenarios de reutilización**: cualquier sistema administrativo que requiera una bitácora simple de acciones, independientemente del dominio de negocio.
- **Beneficios de reutilizar el componente**: esquema mínimo ya definido para trazabilidad administrativa.
- **Recomendaciones para su integración**: puede copiarse tal cual como punto de partida de un módulo de auditoría en otro proyecto Spring Data JPA.

### 6.13 Entidad `AccionSistema`

- **Nombre**: `AccionSistema`
- **Ubicación**: `controlF/src/main/java/com/controlf/db/schema/AccionSistema.java`
- **Descripción y propósito**: entidad JPA que representa una acción del sistema activable/desactivable (tabla `acciones_disponibles`), con un nombre único y un indicador booleano `estaHabilitada`.
- **Responsabilidades**: soportar un mecanismo simple de activación/desactivación de funcionalidades (patrón *feature flag* rudimentario) desde base de datos.
- **Interfaz pública**: entidad JPA estándar, consultable vía `AccionSistemaRepository`.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-data-jpa`.
- **Restricciones o limitaciones**: no implementa evaluación de condiciones complejas (porcentaje de usuarios, segmentación); es un interruptor binario simple.
- **Escenarios de reutilización**: proyectos que requieran un mecanismo básico de *feature flags* administrables sin recurrir a una plataforma externa dedicada.
- **Beneficios de reutilizar el componente**: esquema mínimo reutilizable como base de un sistema de banderas de funcionalidad.

### 6.14 Entidad `Configuracion` (clave-valor)

- **Nombre**: `Configuracion`
- **Ubicación**: `controlF/src/main/java/com/controlf/db/schema/Configuracion.java`
- **Descripción y propósito**: entidad JPA de tipo clave-valor (`clave` como clave primaria, `valor` en texto, `descripcion` opcional) para parámetros configurables desde base de datos.
- **Responsabilidades**: permitir la persistencia de parámetros de sistema sin requerir redeploy.
- **Interfaz pública**: entidad JPA estándar, consultable vía `ConfiguracionRepository`.
- **Dependencias y requisitos para su reutilización**: `spring-boot-starter-data-jpa`.
- **Restricciones o limitaciones**: el campo `valor` es texto plano; no admite tipado fuerte (numérico, booleano) sin conversión manual en la capa de servicio.
- **Escenarios de reutilización**: cualquier proyecto que necesite un almacén simple de configuración editable en tiempo de ejecución.
- **Beneficios de reutilizar el componente**: patrón clave-valor ya modelado, evita definir una tabla de configuración desde cero.

### 6.15 Patrón DTO (capa `dto/`)

- **Nombre**: patrón *Data Transfer Object*
- **Ubicación**: `controlF/src/main/java/com/controlf/dto/` (aproximadamente 50 clases)
- **Descripción y propósito**: separación sistemática entre las entidades JPA de persistencia y los objetos expuestos por la API REST, agrupables por propósito: DTOs de solicitud de creación/actualización (`Crear*RequestDTO`, `Actualizar*RequestDTO`), de respuesta de perfil/detalle (`Perfil*DTO`, `Contenido*DTO`), de listado/grilla con filtros (`Grilla*DTO`, `Filtros*DTO`), de autenticación (`Auth*DTO`), de resultados de importación (`*ImportResultDTO`) y de agregados administrativos/dashboard (`DashboardStatsDTO`, `Panel*DTO`).
- **Responsabilidades**: desacoplar el contrato de la API del modelo de persistencia; evitar la exposición directa de entidades JPA (y de sus relaciones *lazy*) en las respuestas HTTP.
- **Interfaz pública**: no aplica como componente único; el patrón se manifiesta en la convención de nombres y la agrupación por tipo de operación.
- **Dependencias y requisitos para su reutilización**: ninguna específica; es una práctica arquitectónica aplicable con Bean Validation (`spring-boot-starter-validation`) para las validaciones de entrada.
- **Restricciones o limitaciones**: los DTOs concretos son específicos del dominio y no reutilizables individualmente en otro proyecto; lo reutilizable es la **convención de organización y nomenclatura**.
- **Escenarios de reutilización**: cualquier API REST construida sobre Spring Boot y JPA que requiera evitar el acoplamiento entre entidad y contrato público.
- **Beneficios de reutilizar el componente**: consistencia y previsibilidad en la nomenclatura de contratos de API a través de todo el backend.
- **Recomendaciones para su integración**: adoptar la misma convención de sufijos (`RequestDTO`, `DTO` para respuestas) en nuevos módulos para mantener coherencia.

### 6.16 `AuthContext` y hook `useAuth`

- **Nombre**: `AuthContext` / `AuthProvider` / `useAuth`
- **Ubicación**: `controlf_fronted/src/context/AuthContext.tsx`
- **Descripción y propósito**: contexto de React que centraliza el estado de autenticación del cliente, persistiendo el token en `localStorage` y exponiendo un *wrapper* de `fetch` (`apiFetch`) que adjunta automáticamente el encabezado `Authorization: Bearer`.
- **Responsabilidades**: gestionar el ciclo de vida de la sesión del usuario en el cliente; centralizar el envío del token en cada solicitud a la API.
- **Funcionalidades que ofrece**: `login(token, user)`, `logout()`, `apiFetch(...)`, y los valores derivados `user`, `role`, `isAuthenticated`.
- **Interfaz pública**: hook `useAuth()` consumible desde cualquier componente descendiente de `AuthProvider`.
- **Entradas y salidas**: entrada — token y datos de usuario recibidos del backend tras el *login*. Salida — estado reactivo de autenticación disponible en toda la aplicación.
- **Dependencias y requisitos para su reutilización**: React 19 (Context API + Hooks), `localStorage` del navegador.
- **Restricciones o limitaciones**: el tipo `AuthUser` (`id/email/nombre/rol`) es específico de ControlF, pero de estructura mínima y fácilmente adaptable; no implementa renovación automática de token (*refresh token*) ni expiración proactiva en el cliente.
- **Escenarios de reutilización**: cualquier SPA en React que consuma una API protegida por JWT y requiera compartir el estado de sesión entre componentes sin una librería de gestión de estado global.
- **Beneficios de reutilizar el componente**: evita reimplementar el patrón Context + `localStorage` + *fetch wrapper*, una de las combinaciones más comunes en aplicaciones React con autenticación por token.
- **Riesgos o consideraciones técnicas**: al no manejar expiración proactiva, un token vencido solo se detecta cuando el backend responde con error; se recomienda añadir dicha lógica antes de reutilizar en un contexto con sesiones largas.
- **Recomendaciones para su integración**: adaptar el tipo `AuthUser` a los campos de identidad del nuevo dominio; conservar la estructura de `apiFetch` como envoltorio único de llamadas autenticadas.

### 6.17 `ProtectedRoute`

- **Nombre**: `ProtectedRoute`
- **Ubicación**: `controlf_fronted/src/componentes/auth/ProtectedRoute.tsx`
- **Descripción y propósito**: componente de orden superior para `react-router-dom` que restringe el acceso a rutas según autenticación y, opcionalmente, rol (props `adminOnly`, `roles`), redirigiendo a `/login` y preservando la ruta de origen (`location.pathname`) para retorno tras el inicio de sesión.
- **Responsabilidades**: control de acceso declarativo a nivel de ruta.
- **Interfaz pública**: props `adminOnly?: boolean`, `roles?: string[]`, hijos (`children`) o `<Outlet />` según patrón de uso.
- **Entradas y salidas**: entrada — estado de `useAuth()` (contexto). Salida — renderizado condicional del contenido protegido o redirección.
- **Dependencias y requisitos para su reutilización**: `react-router-dom` (v6 o v7), el hook `useAuth` de la sección 6.16.
- **Restricciones o limitaciones**: ninguna específica de dominio.
- **Escenarios de reutilización**: cualquier SPA en React con rutas que requieran autenticación o autorización por rol.
- **Beneficios de reutilizar el componente**: totalmente genérico; integrable sin modificaciones si el proyecto usa la misma forma de `useAuth`.
- **Recomendaciones para su integración**: copiar tal cual, siempre que el hook de autenticación del nuevo proyecto exponga `isAuthenticated` y `role` con la misma semántica.

### 6.18 `authValidation.ts`

- **Nombre**: `authValidation.ts`
- **Ubicación**: `controlf_fronted/src/componentes/auth/authValidation.ts`
- **Descripción y propósito**: módulo de utilidades de validación de formularios de autenticación: `isValidEmail` (validación por expresión regular), `PASSWORD_REQUIREMENTS` (longitud mínima de 10 caracteres, mayúscula, minúscula, número y carácter especial), `evaluatePassword` e `isPasswordStrong`.
- **Responsabilidades**: centralizar reglas de validación de correo y fortaleza de contraseña, evitando duplicarlas entre `LoginPage` y `RegisterPage`.
- **Interfaz pública**: funciones puras exportadas, sin efectos secundarios ni dependencia de estado de React.
- **Entradas y salidas**: entrada — cadenas de texto (correo o contraseña). Salida — booleanos o estructuras de evaluación de reglas cumplidas/incumplidas.
- **Dependencias y requisitos para su reutilización**: ninguna (TypeScript puro).
- **Restricciones o limitaciones**: las reglas de fortaleza de contraseña están fijas en el módulo (no parametrizables desde fuera) si se requiere una política distinta.
- **Escenarios de reutilización**: cualquier formulario de registro/autenticación en React o en otro framework de JavaScript/TypeScript, dada su independencia de UI.
- **Beneficios de reutilizar el componente**: funciones puras, fácilmente testeables y trasladables sin adaptación.
- **Recomendaciones para su integración**: si la política de contraseñas del nuevo proyecto difiere, parametrizar `PASSWORD_REQUIREMENTS` en lugar de modificar la lógica de evaluación.

### 6.19 `SearchableSelect`

- **Nombre**: `SearchableSelect`
- **Ubicación**: `controlf_fronted/src/componentes/panel_admin/components/SearchableSelect.tsx`
- **Descripción y propósito**: componente de selección tipo *combobox* con búsqueda incremental, cierre automático al hacer clic fuera del componente (mediante `useRef` y un *listener* de `mousedown`), y filtrado por etiqueta o, opcionalmente, por identificador (`searchById`).
- **Responsabilidades**: proveer una alternativa de selección más usable que un `<select>` nativo cuando la lista de opciones es extensa.
- **Interfaz pública**: props tipadas mediante `SearchableSelectOption { id, label }`; no depende de ninguna entidad del dominio de negocio.
- **Entradas y salidas**: entrada — arreglo de opciones y valor seleccionado. Salida — evento de selección hacia el componente padre.
- **Dependencias y requisitos para su reutilización**: React (hooks `useState`, `useRef`, `useEffect`); Tailwind CSS para el estilo (sustituible por otra solución de estilos si se elimina la dependencia de clases utilitarias).
- **Restricciones o limitaciones**: no implementa virtualización de listas; en catálogos muy extensos (miles de opciones) podría requerir optimización adicional.
- **Escenarios de reutilización**: cualquier formulario React que necesite un selector con búsqueda (selección de categorías, entidades relacionadas, etc.).
- **Beneficios de reutilizar el componente**: componente de interfaz completamente desacoplado del dominio, lista para usarse en cualquier formulario nuevo.
- **Recomendaciones para su integración**: copiar el componente junto con su archivo de tipos; ajustar las clases de Tailwind si el proyecto destino usa otro sistema de diseño.

### 6.20 `Paginacion`

- **Nombre**: `Paginacion`
- **Ubicación**: `controlf_fronted/src/componentes/directorio_politicos/Componente pie pagina/Paginacion.tsx`
- **Descripción y propósito**: componente de paginación controlado por props (`paginaActual`, `totalPaginas`, `onPageChange`), sin estado interno propio ni acoplamiento a datos del dominio.
- **Responsabilidades**: renderizar controles de navegación entre páginas de un listado.
- **Interfaz pública**: props `paginaActual: number`, `totalPaginas: number`, `onPageChange: (pagina: number) => void`.
- **Entradas y salidas**: entrada — estado de paginación gestionado por el componente padre. Salida — callback de cambio de página.
- **Dependencias y requisitos para su reutilización**: React; ninguna dependencia de dominio.
- **Restricciones o limitaciones**: las etiquetas de texto ("Anterior", "Siguiente", "Página") están en español y embebidas en el componente; para reutilización en un proyecto multi-idioma requeriría externalizar los textos.
- **Escenarios de reutilización**: cualquier listado paginado en React (directorios, tablas, resultados de búsqueda).
- **Beneficios de reutilizar el componente**: componente 100% controlado por props, sin efectos colaterales, de integración inmediata.
- **Recomendaciones para su integración**: extraer las cadenas de texto a un mecanismo de internacionalización si el proyecto destino lo requiere.

### 6.21 `MainLayout`

- **Nombre**: `MainLayout`
- **Ubicación**: `controlf_fronted/src/componentes/layout/MainLayout.tsx`
- **Descripción y propósito**: *shell* de aplicación que combina el componente `Header` con el punto de inserción de rutas de React Router (`<Outlet />`) y un pie de página.
- **Responsabilidades**: definir la estructura visual común a todas las páginas autenticadas o públicas de la aplicación.
- **Interfaz pública**: no recibe props; se integra como elemento de layout en la configuración de rutas (`App.tsx`).
- **Dependencias y requisitos para su reutilización**: `react-router-dom` (`Outlet`), componente `Header`.
- **Restricciones o limitaciones**: el pie de página contiene texto institucional de ControlF hardcodeado; debe editarse antes de reutilizar en otro proyecto.
- **Escenarios de reutilización**: como plantilla de *layout* estructural (Header + Outlet + Footer) en cualquier SPA con React Router.
- **Beneficios de reutilizar el componente**: estructura de layout mínima ya resuelta, evita reconfigurar la integración con `Outlet`.
- **Recomendaciones para su integración**: reutilizar la **estructura**, sustituyendo el contenido de `Header` y el pie de página por los del nuevo proyecto.

### 6.22 Barras de búsqueda con filtros configurables

- **Nombre**: `BarraBusqueda.tsx` (directorio de políticos) y `BarraBusquedaLeyes.tsx` (directorio de leyes)
- **Ubicación**: `controlf_fronted/src/componentes/directorio_politicos/Componente busqueda politico/BarraBusqueda.tsx` y `controlf_fronted/src/componentes/directorio_leyes/BarraBusquedaLeyes.tsx`
- **Descripción y propósito**: componentes de barra de búsqueda que reciben un arreglo de filtros configurables (`{ id, label, valorSeleccionado, opciones }[]`) por props, sin realizar peticiones propias ni mantener estado de datos; el componente padre controla el estado.
- **Responsabilidades**: renderizar controles de filtro genéricos y notificar cambios al componente padre.
- **Interfaz pública**: props de filtros tipadas de forma genérica (no atadas a `Ley` ni `Politico`).
- **Restricciones o limitaciones**: existen **dos implementaciones prácticamente duplicadas** (una por cada directorio) en lugar de un único componente genérico parametrizado; esto reduce su condición de "reutilizable directo" a "reutilizable como patrón", y constituye una oportunidad de consolidación (ver sección 8).
- **Escenarios de reutilización**: como base para extraer un único componente `BarraBusquedaGenerica` parametrizado por lista de filtros, aplicable a cualquier listado con filtros dinámicos.
- **Beneficios de reutilizar el componente**: patrón de filtros 100% controlado por props, favorable a la reutilización si se consolida en un solo componente.
- **Recomendaciones para su integración**: antes de reutilizar en un tercer directorio, unificar ambas implementaciones en un componente único genérico.

## 7. Componentes no reutilizables y justificación técnica

De acuerdo con los criterios de la sección 4, los siguientes elementos se clasifican como **no reutilizables** fuera del dominio de auditoría legislativa de ControlF. Se documentan para dejar constancia explícita de que fueron evaluados y del motivo técnico de su exclusión.

| Componente | Ubicación | Motivo técnico de no reutilización |
|---|---|---|
| Entidades `Ley`, `Politico`, `Promesa`, `Voto`, `VinculoPromesaLey`, `Comentario`, `Calificacion`, `Suscripcion`, `Reporte` | `controlF/.../db/schema/` | Modelan directamente el dominio de auditoría legislativa; sus campos y relaciones (p. ej. vínculo promesa-ley, coherencia de voto) no tienen equivalente genérico fuera de ese dominio. |
| `AdminService` | `controlF/.../service/AdminService.java` | Orquestador de 491 líneas con 11 dependencias inyectadas que concentra lógica de negocio de múltiples subdominios (coherencia, promesas, respaldo, caché, importación, panel de seguridad). Incumple el criterio de bajo acoplamiento y constituye además un candidato a refactorización interna (*god service*) antes de cualquier extracción. |
| `LeyService`, `PoliticoService` | `controlF/.../service/` | Contienen la lógica de negocio central del dominio (774 y 635 líneas respectivamente): cálculo de coherencia, comparación de patrones de voto, perfiles y auditoría. No son separables del modelo de datos de ControlF sin una reescritura completa. |
| `AlertaService`, `DataSeederService`, `ValidacionService`, `PoliticoImportService` | `controlF/.../service/` | Lógica de negocio específica (alertas por categoría de ley, datos de ejemplo del dominio, moderación de comentarios sobre leyes/políticos, importación de políticos) sin equivalente genérico. |
| Controladores `AdminController`, `AlertaController`, `LeyController`, `PoliticoController`, `DashboardController`, `ValidacionController`, `AssemblyImportController`, `PoliticoImportController`, `AuthController` | `controlF/.../controller/` | Exponen endpoints REST directamente acoplados a las entidades y DTOs del dominio; su reutilización implicaría trasladar también el modelo de datos completo. `AuthController` incluye además lógica de generación de sugerencias de nombre de usuario interna, no extraída como utilidad separada. |
| Matriz de autorización de `SecurityConfig.securityFilterChain(...)` | `controlF/.../auth/SecurityConfig.java` | Hardcodea rutas concretas del dominio (`/api/leyes/**`, `/api/politicos/**`, `/api/admin/**`) asociadas a roles específicos; a diferencia de los beans `passwordEncoder`/`authenticationManager` (sección 6.4), este método requiere reescritura completa para cualquier otro proyecto. |
| Componentes de página (`*Page.tsx`) y componentes de dominio del frontend (`CartaPolitico`, `ContenidoLey`, `ResultadoVotacion`, `AuditoriaLey`, `GrillaPoliticos`, `MotorCoherencia`, `MantenimientoSistema`, `HistorialCoherencia`, `InfoBasica`, `IndiceReputacion`, `MetricaCoherencia`, `ParticipacionCiudadana`) | `controlf_fronted/src/componentes/` | Cada uno modela una vista específica del dominio de auditoría legislativa, con props tipadas explícitamente contra la forma de datos de `Ley`/`Politico`/`Voto`. No son generalizables sin perder su propósito. |
| `Header.tsx` (contenido de `navItems`) | `controlf_fronted/src/componentes/layout/Header.tsx` | El patrón de navegación condicionada por rol es reutilizable (documentado como parte del patrón de `MainLayout`, sección 6.21), pero el arreglo concreto de ítems de navegación está hardcodeado a las rutas de ControlF (Dashboard, Políticos, Leyes, Admin), por lo que el archivo en sí no es reutilizable sin edición. |
| `GeminiService.generarExplicacion(...)` (lógica de *prompt*) | `controlF/.../service/GeminiService.java` | El *prompt* específico sobre títulos y descripciones de leyes es de dominio; a diferencia del mecanismo de solicitud/respuesta HTTP (reutilizable, sección 6.9), este método concreto no lo es. |
| `WebConfig`/`application.properties` — credenciales de base de datos con *fallback* en texto plano | `controlF/src/main/resources/application.properties` | Constituye una práctica de configuración insegura y específica del entorno de desarrollo actual, no un activo reutilizable; se documenta aquí como riesgo a corregir antes de derivar cualquier plantilla de configuración reutilizable del proyecto. |
| Suite de pruebas (`AuthWorkflowTests`, `BackendWorkflowTests`, `NuevasFuncionalidadesTests`, `PoliticoPromesaControllerTests`) | `controlF/src/test/java/com/controlf/` | Cada clase de prueba valida un flujo de negocio específico; no se identificaron *builders* o *fixtures* de prueba extraídos como utilidades genéricas reutilizables. |

## 8. Recomendaciones para la reutilización futura

1. **Extraer un módulo de seguridad JWT independiente**: los componentes `JwtService`, `AuthenticatedUser`, `JwtAuthenticationFilter` y los beans de `SecurityConfig` (secciones 6.1–6.4) reúnen las condiciones para consolidarse en una librería interna (`controlf-security-commons` o equivalente) reutilizable entre los distintos servicios que pudiera llegar a tener la plataforma, separando explícitamente la matriz de autorización (no reutilizable) del resto.
2. **Eliminar el secreto JWT y las credenciales de base de datos embebidos como *fallback*** antes de derivar cualquier plantilla reutilizable de configuración, conforme a lo ya señalado en `INFORME_TECNICO.md` (sección "Deuda Técnica", prioridad alta).
3. **Generalizar `GeminiService`** separando el método de integración HTTP puro (`generarTexto(prompt)`) del método de dominio (`generarExplicacion(...)`), de modo que el primero pueda reutilizarse en cualquier funcionalidad futura que requiera generación de texto con IA.
4. **Consolidar las barras de búsqueda duplicadas** (`BarraBusqueda.tsx` y `BarraBusquedaLeyes.tsx`) en un único componente genérico parametrizado, dado que ambas comparten la misma interfaz de filtros configurables (sección 6.22).
5. **Formalizar el patrón de entidades genéricas de auditoría** (`LogSistema`, `AccionSistema`, `Configuracion`, sección 6.12–6.14) como un módulo de infraestructura administrativa reutilizable en cualquier backend Spring Boot que requiera bitácora, *feature flags* simples y configuración por clave-valor.
6. **Mantener actualizado este documento** cada vez que se introduzcan nuevos componentes candidatos a reutilización o se modifiquen los aquí descritos, de forma que el inventario refleje el estado real del código fuente (principio de trazabilidad de Sommerville entre documentación y activos reutilizables).
7. **Evaluar la extracción de `apiFetch`/`AuthContext`** (sección 6.16) como paquete npm interno si la organización llegase a mantener más de un frontend React que consuma APIs protegidas por JWT con el mismo esquema de autenticación.

## 9. Conclusiones

El análisis del código fuente de ControlF evidencia que, si bien la mayor parte de la lógica de negocio (servicios de dominio, entidades JPA de leyes/políticos/votos, controladores REST y componentes de página del frontend) es intrínsecamente específica del problema de auditoría legislativa y por tanto no reutilizable fuera de ese contexto, el proyecto contiene un conjunto identificable de **componentes de infraestructura transversal** —principalmente en las capas de seguridad (`auth/`), configuración (`config/`, `WebConfig`), integración externa (`GeminiService`, `AssemblyImportService`) y componentes de interfaz genéricos del frontend (`SearchableSelect`, `Paginacion`, `ProtectedRoute`, `authValidation.ts`, `AuthContext`)— que cumplen los criterios de reutilización adoptados en este documento.

La aplicación consistente del patrón Repository, del patrón DTO y de la separación por capas (controlador–servicio–repositorio–entidad) en el backend, así como la organización por dominio con componentes presentacionales desacoplados en el frontend, constituye en sí misma una práctica reutilizable a nivel de **patrón arquitectónico**, independientemente de que las clases concretas no lo sean.

Se recomienda que las decisiones de evolución del sistema —en particular las señaladas como deuda técnica de prioridad alta en `INFORME_TECNICO.md` (autenticación/autorización real, gestión de secretos)— se aborden teniendo en cuenta este inventario, dado que varios de los componentes de seguridad aquí documentados como reutilizables (`JwtService`, `SecurityConfig`) son precisamente los que requieren corrección antes de poder considerarse aptos para reutilización en un entorno de producción.
