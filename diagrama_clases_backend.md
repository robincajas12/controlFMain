# Diagrama de Clases — Backend ControlF

Basado exclusivamente en el código fuente de `controlF/src/main/java/com/controlf`. Se omiten getters/setters/constructores generados por Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`) y se muestran solo atributos y métodos públicos relevantes para entender la arquitectura. Dado el volumen de clases (13 entidades, 13 repositorios, 11 servicios, 11 controladores, 6 clases de seguridad, ~60 DTOs), el documento se divide en un diagrama general de paquetes y un diagrama detallado por paquete/módulo.

## Diagrama General (paquetes y dependencias)

```plantuml
@startuml
skinparam packageStyle rectangle
left to right direction

package "com.controlf.controller" as controller
package "com.controlf.service" as service
package "com.controlf.dto" as dto
package "com.controlf.db.repository" as repository
package "com.controlf.db.schema" as schema
package "com.controlf.db.schema.enums" as enums
package "com.controlf.auth" as auth
package "com.controlf.config" as config
package "com.controlf (raíz)" as root

controller ..> service : usa
controller ..> dto : consume/produce
controller ..> repository : usa (contexto en\nvalidación de comentarios)
service ..> repository : usa
service ..> dto : construye/consume
service ..> schema : persiste/lee
repository ..> schema : gestiona
schema ..> enums : usa
auth ..> repository : usa (UsuarioRepository)
auth ..> schema : usa (Usuario)
controller ..> auth : usa (Authentication)
service ..> config : usa (RestTemplate)
root ..> auth : registra filtro/seguridad
root ..> config : configuración CORS
@enduml
```

## Módulo: Modelo de Dominio (`db.schema` + `db.schema.enums`)

```plantuml
@startuml
left to right direction
hide empty members

class Usuario {
  -Integer id
  -String nombre
  -String email
  -String passwordHash
  -String avatarUrl
  -Rol rol
  -LocalDateTime fechaRegistro
  -boolean activo
  +onCreate()
}
enum "Usuario.Rol" as UsuarioRol {
  ADMIN
  CIUDADANO
  VALIDADOR
}
Usuario *-- UsuarioRol

class Politico {
  -Integer id
  -String nombreCompleto
  -String partidoPolitico
  -String cargoActual
  -String region
  -String comision
  -Boolean estaActivo
  -BigDecimal patrimonioDeclarado
  -String antecedentes
  -String historialActualizaciones
  -String fotoUrl
}

class Ley {
  -Integer id
  -String titulo
  -String codigo
  -String tipoExpediente
  -String proponente
  -String descripcionOriginal
  -String descripcionSimplificada
  -String impactoSocial
  -String categoria
  -EstadoLey estado
  -LocalDate fechaIngreso
  -Long externalId
}

class Promesa {
  -Integer id
  -String descripcion
  -String categoria
  -LocalDate fechaCreacion
}

class Voto {
  -Integer id
  -TipoVoto tipoVoto
  -Boolean asistencia
  -LocalDateTime fechaVoto
}

class VinculoPromesaLey {
  -Integer id
  -ImpactoEsperado impactoEsperado
  -NivelCoherencia nivelCoherencia
  -String analisisCoherencia
}

class Comentario {
  -Integer id
  -String texto
  -Boolean esBasadoEnHechos
  -LocalDateTime fecha
  -Integer puntaje
  -EstadoModeracion estado = APROBADO
  -String notaModeracion
}

class Calificacion {
  -Integer id
  -Integer puntaje
  -LocalDateTime fecha
}

class Suscripcion {
  -Integer id
  -String categoria
  -LocalDateTime fechaCreacion
  +onCreate()
}

class Reporte {
  -Integer id
  -String motivo
  -EstadoReporte estado
  -LocalDateTime fechaReporte
}
enum "Reporte.EstadoReporte" as EstadoReporte {
  PENDIENTE
  REVISADO
  DESESTIMADO
}
Reporte *-- EstadoReporte

class AccionSistema {
  -Integer id
  -String nombre
  -Boolean estaHabilitada
}

class LogSistema {
  -Integer id
  -String accion
  -String detalles
  -LocalDateTime fecha
}

class Configuracion {
  -String clave
  -String valor
  -String descripcion
}

enum EstadoLey {
  DEBATE
  EN_DEBATE
  APROBADA
  VETADA
}
enum EstadoModeracion {
  PENDIENTE
  APROBADO
  RECHAZADO
  OBSERVADO
}
enum ImpactoEsperado {
  POSITIVO
  NEGATIVO
}
enum NivelCoherencia {
  CUMPLE
  INCUMPLE
  AMBIGUO
}
enum TipoVoto {
  FAVOR
  CONTRA
  ABSTENCION
}

Ley "1" *-- "0..1" EstadoLey
Comentario "1" *-- "1" EstadoModeracion
VinculoPromesaLey "1" *-- "1" ImpactoEsperado
VinculoPromesaLey "1" *-- "1" NivelCoherencia
Voto "1" *-- "1" TipoVoto

Usuario "1" o-- "*" Comentario : comentarios\n(mappedBy usuario, cascade ALL)
Usuario "1" o-- "*" Calificacion : calificaciones\n(mappedBy usuario, cascade ALL)
Usuario "1" o-- "*" Suscripcion : (ManyToOne inverso)
Usuario "1" o-- "*" Reporte : usuarioReportado
Usuario "0..1" o-- "*" Reporte : reportero
Usuario "0..1" o-- "*" LogSistema

Politico "1" *-- "*" Promesa : promesas\n(mappedBy politico, cascade ALL)
Politico "1" *-- "*" Voto : votos\n(mappedBy politico, cascade ALL)
Politico "1" o-- "*" Comentario : comentarios\n(@JoinTable politico_comentarios)
Politico "1" o-- "*" Calificacion : calificaciones\n(@JoinTable politico_calificaciones)

Ley "1" *-- "*" Voto : votos\n(mappedBy ley, cascade ALL)
Ley "1" o-- "*" Comentario : comentarios\n(@JoinTable ley_comentarios)
Ley "1" o-- "*" Calificacion : calificaciones\n(@JoinTable ley_calificaciones)
Ley "1" *-- "*" VinculoPromesaLey : vinculos\n(mappedBy ley, cascade ALL)

Promesa "*" --> "1" Politico : politico
Promesa "1" *-- "*" VinculoPromesaLey : vinculos\n(mappedBy promesa, cascade ALL)

Voto "*" --> "1" Politico : politico
Voto "*" --> "1" Ley : ley

VinculoPromesaLey "*" --> "1" Promesa : promesa
VinculoPromesaLey "*" --> "1" Ley : ley
@enduml
```

## Módulo: Repositorios (`db.repository`)

```plantuml
@startuml
left to right direction
hide empty members

interface "JpaRepository<T,ID>" as JpaRepository
interface "JpaSpecificationExecutor<T>" as JpaSpecificationExecutor

interface UsuarioRepository {
  +findByEmail(String): Optional<Usuario>
  +existsByNombreIgnoreCase(String): boolean
}
interface PoliticoRepository {
  +findDistinctPartidos(): List<String>
  +findDistinctRegiones(): List<String>
  +findDistinctComisiones(): List<String>
  +findByNombreCompletoContainingIgnoreCase(String): Optional<Politico>
  +findByComentarioId(Integer): Optional<Politico>
}
interface LeyRepository {
  +findByCodigo(String): Optional<Ley>
  +existsByExternalId(Long): boolean
  +findByExternalId(Long): Optional<Ley>
  +countByProponente(String): long
  +countByEstado(EstadoLey): long
  +findDistinctCategorias(): List<String>
  +findByComentarioId(Integer): Optional<Ley>
}
interface PromesaRepository {
  +findByPoliticoId(Integer): List<Promesa>
}
interface VotoRepository {
  +findByLeyId(Integer): List<Voto>
  +findByPoliticoId(Integer): List<Voto>
  +existsByPoliticoIdAndLeyId(Integer, Integer): boolean
  +countByLeyIdAndTipoVoto(Integer, TipoVoto): long
  +countByTipoVoto(TipoVoto): long
}
interface VinculoPromesaLeyRepository {
  +findAverageCoherenciaByPoliticoId(Integer): Double
}
interface ComentarioRepository {
  +findByEstadoOrderByFechaDesc(EstadoModeracion): List<Comentario>
  +countByEstado(EstadoModeracion): long
}
interface CalificacionRepository {
  +findAveragePuntajeByLeyId(Integer): Double
  +findAveragePuntajeByPoliticoId(Integer): Double
  +countByPoliticoId(Integer): long
}
interface SuscripcionRepository {
  +findByUsuarioId(Integer): List<Suscripcion>
}
interface ReporteRepository {
  +findByEstado(Reporte.EstadoReporte): List<Reporte>
  +countByEstado(Reporte.EstadoReporte): long
}
interface AccionSistemaRepository {
  +findByEstaHabilitadaTrue(): List<AccionSistema>
}
interface LogSistemaRepository
interface ConfiguracionRepository

UsuarioRepository --|> JpaRepository
PoliticoRepository --|> JpaRepository
PoliticoRepository --|> JpaSpecificationExecutor
LeyRepository --|> JpaRepository
LeyRepository --|> JpaSpecificationExecutor
PromesaRepository --|> JpaRepository
VotoRepository --|> JpaRepository
VinculoPromesaLeyRepository --|> JpaRepository
ComentarioRepository --|> JpaRepository
CalificacionRepository --|> JpaRepository
SuscripcionRepository --|> JpaRepository
ReporteRepository --|> JpaRepository
AccionSistemaRepository --|> JpaRepository
LogSistemaRepository --|> JpaRepository
ConfiguracionRepository --|> JpaRepository

UsuarioRepository ..> Usuario
PoliticoRepository ..> Politico
LeyRepository ..> Ley
PromesaRepository ..> Promesa
VotoRepository ..> Voto
VinculoPromesaLeyRepository ..> VinculoPromesaLey
ComentarioRepository ..> Comentario
CalificacionRepository ..> Calificacion
SuscripcionRepository ..> Suscripcion
ReporteRepository ..> Reporte
AccionSistemaRepository ..> AccionSistema
LogSistemaRepository ..> LogSistema
ConfiguracionRepository ..> Configuracion

class Usuario
class Politico
class Ley
class Promesa
class Voto
class VinculoPromesaLey
class Comentario
class Calificacion
class Suscripcion
class Reporte
class AccionSistema
class LogSistema
class Configuracion
@enduml
```

## Módulo: Seguridad (`auth`)

```plantuml
@startuml
left to right direction
hide empty members

interface UserDetails
interface UserDetailsService
class OncePerRequestFilter

class AuthenticatedUser implements UserDetails {
  -Integer id
  -String email
  -String nombre
  -String role
  -String passwordHash
  -boolean activo
  +getId(): Integer
  +getNombre(): String
  +getRole(): String
  +getAuthorities(): Collection<GrantedAuthority>
  +getPassword(): String
  +getUsername(): String
  +isAccountNonExpired(): boolean
  +isAccountNonLocked(): boolean
  +isCredentialsNonExpired(): boolean
  +isEnabled(): boolean
}

class CustomUserDetailsService implements UserDetailsService {
  -UsuarioRepository usuarioRepository
  +loadUserByUsername(String): UserDetails
}

class DevUserSeeder {
  -UsuarioRepository usuarioRepository
  -PasswordEncoder passwordEncoder
  +run(ApplicationArguments)
}
note right of DevUserSeeder
  @Profile("dev"): crea admin@controlf.dev
  al arrancar si no existe.
end note

class JwtService {
  -String jwtSecret
  -long expirationHours
  -SecretKey secretKey
  +generateToken(String, String): String
  +parseToken(String): Claims
}

class JwtAuthenticationFilter extends OncePerRequestFilter {
  -JwtService jwtService
  -CustomUserDetailsService customUserDetailsService
  #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
}

class SecurityConfig {
  -JwtAuthenticationFilter jwtAuthenticationFilter
  -CustomUserDetailsService customUserDetailsService
  +passwordEncoder(): PasswordEncoder
  +authenticationManager(PasswordEncoder): AuthenticationManager
  +securityFilterChain(HttpSecurity): SecurityFilterChain
}

CustomUserDetailsService ..> AuthenticatedUser : crea
CustomUserDetailsService --> "com.controlf.db.repository.UsuarioRepository"
DevUserSeeder --> "com.controlf.db.repository.UsuarioRepository"
JwtAuthenticationFilter --> JwtService
JwtAuthenticationFilter --> CustomUserDetailsService
JwtAuthenticationFilter ..> AuthenticatedUser : castea principal
SecurityConfig --> JwtAuthenticationFilter
SecurityConfig --> CustomUserDetailsService
@enduml
```

## Módulo: Servicios (`service`)

```plantuml
@startuml
left to right direction
hide empty members

class AdminService {
  -ReporteRepository reporteRepository
  -LogSistemaRepository logRepository
  -DataSource dataSource
  -PromesaRepository promesaRepository
  -LeyRepository leyRepository
  -VinculoPromesaLeyRepository vinculoRepository
  -UsuarioRepository usuarioRepository
  -PoliticoRepository politicoRepository
  -AssemblyImportService assemblyImportService
  -LeyService leyService
  -VotoRepository votoRepository
  +getMotorData(): MotorCoherenciaDataDTO
  +getPromesasByPolitico(Integer): List<SimpleItemDTO>
  +crearPromesa(CrearPromesaRequestDTO)
  +crearVinculoCoherencia(VinculoRequestDTO)
  +ejecutarRespaldo(Integer)
  +limpiarCache()
  +importarLeyes()
  +normalizarLeyes(): LeyNormalizacionResultDTO
  +listarLeyesParaSync(): List<LeySyncItemDTO>
  +syncAllLeyesWithVotingDetails(): LeySyncStatusDTO
  +crearPolitico(CrearPoliticoRequestDTO)
  +crearLey(CrearLeyRequestDTO)
  +eliminarPolitico(Integer)
  +getHistoricoResumen(): ReporteHistoricoDTO
  +getSecurityPanel(): PanelControlDTO
  +getMantenimientoInfo(): PanelMantenimientoDTO
}

class AlertaService {
  -SuscripcionRepository suscripcionRepository
  -UsuarioRepository usuarioRepository
  -LeyRepository leyRepository
  -VotoRepository votoRepository
  +listarSuscripciones(Integer): List<SuscripcionDTO>
  +crearSuscripcion(Integer, String): SuscripcionDTO
  +eliminarSuscripcion(Integer, Integer)
  +obtenerAlertas(Integer): List<AlertaDTO>
}

class AssemblyImportService {
  -LeyRepository leyRepository
  -VotoRepository votoRepository
  -PoliticoRepository politicoRepository
  -RestTemplate restTemplate
  -ObjectMapper objectMapper
  +getAssemblyMembers(): List<AssemblyMemberDTO>
  +importSelectedVotings(Long, List<Long>): ImportResultDTO
  +importLeyesForPoliticos(List<Integer>): ImportResultDTO
  +importVotings(Long): ImportResultDTO
}
note right of AssemblyImportService
  BASE_URL = datos.asambleanacional.gob.ec/ecurul/assemblyman
  (sistema externo, vía RestTemplate)
end note

class DashboardService {
  -PoliticoRepository politicoRepository
  -LeyRepository leyRepository
  -ComentarioRepository comentarioRepository
  -VinculoPromesaLeyRepository vinculoRepository
  -VotoRepository votoRepository
  -CalificacionRepository calificacionRepository
  +getStats(): DashboardStatsDTO
  +getMetricasInteractivas(String, String, String, String): MetricasInteractivasDTO
  +exportPoliticosCsv(): String
  +exportLeyesCsv(): String
  +exportStatsCsv(): String
}

class DataSeederService {
  -PoliticoRepository politicoRepository
  -UsuarioRepository usuarioRepository
  -LeyRepository leyRepository
  -VotoRepository votoRepository
  -PromesaRepository promesaRepository
  -VinculoPromesaLeyRepository vinculoRepository
  -ConfiguracionRepository configuracionRepository
  -ComentarioRepository comentarioRepository
  -CalificacionRepository calificacionRepository
  +seed()
}

class GeminiService {
  -RestTemplate restTemplate
  -String apiKey
  -String model
  +generarExplicacion(String, String): String
}
note right of GeminiService
  Llama a generativelanguage.googleapis.com
  (Gemini AI, sistema externo) vía RestTemplate.
end note

class LeyService {
  -LeyRepository leyRepository
  -VotoRepository votoRepository
  -CalificacionRepository calificacionRepository
  -ComentarioRepository comentarioRepository
  -UsuarioRepository usuarioRepository
  -PoliticoRepository politicoRepository
  -GeminiService geminiService
  +getFiltros(): FiltrosLeyDTO
  +getLeyesFiltradas(int, int, String, String, String): GrillaLeyesDTO
  +getFullPerfilLey(Integer): PerfilLeyDTO
  +getAllLeyesAsExpedientes(): List<ExpedienteLegislativoDTO>
  +getAgendaLegislativa(): AgendaLegislativaDTO
  +getDebatesLegislativos(String): List<DebateLegislativoDTO>
  +actualizarCategoriaLey(Integer, CategoriaLeyRequestDTO)
  +actualizarEstadoLey(Integer, EstadoLeyRequestDTO)
  +actualizarAsistenciaVoto(Integer, Integer, AsistenciaVotoRequestDTO)
  +addComentario(Integer, ComentarioRequestDTO, Integer)
  +addCalificacion(Integer, CalificacionRequestDTO, Integer)
  +getContenidoLey(Integer): ContenidoLeyDTO
  +explicarLey(Integer): ContenidoLeyDTO
  +getDebateCiudadano(Integer): DebateCiudadanoDTO
  +getAuditoriaCoherencia(Integer): AuditoriaCoherenciaDTO
  +getResultadoVotacion(Integer): ResultadoVotacionDTO
  +importVotingDetailVotes(Integer): ImportResultDTO
  +getVotingMatchSummary(Integer): VotingMatchSummaryDTO
}

class PoliticoImportService {
  -PoliticoRepository politicoRepository
  -AssemblyImportService assemblyImportService
  +importAll(): PoliticoImportResultDTO
  +importSelected(List<Long>): PoliticoImportResultDTO
}

class PoliticoService {
  -PoliticoRepository politicoRepository
  -VinculoPromesaLeyRepository vinculoRepository
  -ComentarioRepository comentarioRepository
  -CalificacionRepository calificacionRepository
  -UsuarioRepository usuarioRepository
  -PromesaRepository promesaRepository
  -ConfiguracionRepository configuracionRepository
  -VotoRepository votoRepository
  -ObjectMapper objectMapper
  +getPoliticoProfile(Integer): PerfilPoliticoDTO
  +compararPatronesVoto(List<Integer>): ComparacionVotosDTO
  +getFiltros(): FiltrosPoliticoDTO
  +getPoliticosImportables(): List<SimpleItemDTO>
  +getPoliticosFiltrados(int, int, String, String, String, String): GrillaPoliticosDTO
  +getAllPoliticosAsCartas(): List<CartaPoliticoDTO>
  +actualizarCampoPolitico(Integer, ActualizarCampoPoliticoRequestDTO)
  +addComentario(Integer, ComentarioRequestDTO, Integer)
  +addCalificacion(Integer, CalificacionRequestDTO, Integer)
  +crearPromesa(Integer, PromesaRequestDTO): PromesaDTO
  +listarPromesasPorPolitico(Integer): List<PromesaDTO>
}

class ValidacionService {
  -ComentarioRepository comentarioRepository
  -LeyRepository leyRepository
  -PoliticoRepository politicoRepository
  +listarComentarios(String): List<ComentarioModeracionDTO>
  +contarPorEstado(): Map<String,Long>
  +moderar(Integer, String, String): ComentarioModeracionDTO
}

AdminService --> PromesaRepository
AdminService --> LeyRepository
AdminService --> VinculoPromesaLeyRepository
AdminService --> UsuarioRepository
AdminService --> PoliticoRepository
AdminService --> VotoRepository
AdminService --> ReporteRepository
AdminService --> LogSistemaRepository
AdminService --> AssemblyImportService : delega importación
AdminService --> LeyService : delega import-voting-detail

AlertaService --> SuscripcionRepository
AlertaService --> UsuarioRepository
AlertaService --> LeyRepository
AlertaService --> VotoRepository

AssemblyImportService --> LeyRepository
AssemblyImportService --> VotoRepository
AssemblyImportService --> PoliticoRepository
AssemblyImportService --> "config.AppConfig.RestTemplate" : usa bean

DashboardService --> PoliticoRepository
DashboardService --> LeyRepository
DashboardService --> ComentarioRepository
DashboardService --> VinculoPromesaLeyRepository
DashboardService --> VotoRepository
DashboardService --> CalificacionRepository

DataSeederService --> PoliticoRepository
DataSeederService --> UsuarioRepository
DataSeederService --> LeyRepository
DataSeederService --> VotoRepository
DataSeederService --> PromesaRepository
DataSeederService --> VinculoPromesaLeyRepository
DataSeederService --> ConfiguracionRepository
DataSeederService --> ComentarioRepository
DataSeederService --> CalificacionRepository

GeminiService --> "config.AppConfig.RestTemplate" : usa bean

LeyService --> LeyRepository
LeyService --> VotoRepository
LeyService --> CalificacionRepository
LeyService --> ComentarioRepository
LeyService --> UsuarioRepository
LeyService --> PoliticoRepository
LeyService --> GeminiService : delega explicación IA

PoliticoImportService --> PoliticoRepository
PoliticoImportService --> AssemblyImportService : delega importación

PoliticoService --> PoliticoRepository
PoliticoService --> VinculoPromesaLeyRepository
PoliticoService --> ComentarioRepository
PoliticoService --> CalificacionRepository
PoliticoService --> UsuarioRepository
PoliticoService --> PromesaRepository
PoliticoService --> ConfiguracionRepository
PoliticoService --> VotoRepository

ValidacionService --> ComentarioRepository
ValidacionService --> LeyRepository
ValidacionService --> PoliticoRepository

class PromesaRepository
class LeyRepository
class VinculoPromesaLeyRepository
class UsuarioRepository
class PoliticoRepository
class VotoRepository
class ReporteRepository
class LogSistemaRepository
class SuscripcionRepository
class ComentarioRepository
class CalificacionRepository
class ConfiguracionRepository
@enduml
```

## Módulo: Controladores (`controller`)

```plantuml
@startuml
left to right direction
hide empty members

class AuthController <<@RequestMapping("/api/auth")>> {
  -UsuarioRepository usuarioRepository
  -PasswordEncoder passwordEncoder
  -AuthenticationManager authenticationManager
  -JwtService jwtService
  +register(AuthRegisterRequestDTO): ResponseEntity<?>
  +checkAvailability(Map<String,String>): ResponseEntity<Map<String,Object>>
  +login(AuthLoginRequestDTO): ResponseEntity<?>
  +me(): ResponseEntity<AuthMeResponseDTO>
}

class PoliticoController <<@RequestMapping("/api/politicos")>> {
  -PoliticoService politicoService
  -ComentarioRepository comentarioRepository
  +getPolitico(Integer): PerfilPoliticoDTO
  +getFiltros(): FiltrosPoliticoDTO
  +getImportables(): List<SimpleItemDTO>
  +compararPatronesVoto(List<Integer>): ComparacionVotosDTO
  +getPoliticos(...): GrillaPoliticosDTO
  +actualizarCampo(Integer, ActualizarCampoPoliticoRequestDTO)
  +postComentario(Integer, ComentarioRequestDTO, Authentication)
  +postCalificacion(Integer, CalificacionRequestDTO, Authentication)
  +crearPromesa(Integer, PromesaRequestDTO): ResponseEntity<PromesaDTO>
  +listarPromesas(Integer): List<PromesaDTO>
  +actualizarComentario(Integer, ComentarioRequestDTO, Authentication): ResponseEntity<Void>
  +eliminarComentario(Integer, Authentication): ResponseEntity<Void>
}

class PoliticoImportController <<@RequestMapping("/api/admin")>> {
  -PoliticoImportService politicoImportService
  +importAll(): ResponseEntity<PoliticoImportResultDTO>
  +importSelected(List<Long>): ResponseEntity<PoliticoImportResultDTO>
}

class LeyController <<@RequestMapping("/api/leyes")>> {
  -LeyService leyService
  -ComentarioRepository comentarioRepository
  +getPerfil(Integer): PerfilLeyDTO
  +explicar(Integer): ContenidoLeyDTO
  +getFiltros(): FiltrosLeyDTO
  +getAgenda(): AgendaLegislativaDTO
  +getDebates(String): List<DebateLegislativoDTO>
  +getLeyes(...): GrillaLeyesDTO
  +actualizarCategoria(Integer, CategoriaLeyRequestDTO)
  +actualizarEstado(Integer, EstadoLeyRequestDTO)
  +actualizarAsistencia(Integer, Integer, AsistenciaVotoRequestDTO)
  +postComentario(Integer, ComentarioRequestDTO, Authentication)
  +postCalificacion(Integer, CalificacionRequestDTO, Authentication)
  +importVotingDetail(Integer): ImportResultDTO
  +actualizarComentario(Integer, ComentarioRequestDTO, Authentication): ResponseEntity<Void>
  +eliminarComentario(Integer, Authentication): ResponseEntity<Void>
}

class ValidacionController <<@RequestMapping("/api/validacion")>> {
  -ValidacionService validacionService
  +listarComentarios(String): List<ComentarioModeracionDTO>
  +resumen(): Map<String,Long>
  +moderar(Integer, ModeracionRequestDTO): ComentarioModeracionDTO
}

class AlertaController <<@RequestMapping("/api/alertas")>> {
  -AlertaService alertaService
  +getAlertas(Authentication): List<AlertaDTO>
  +getSuscripciones(Authentication): List<SuscripcionDTO>
  +crearSuscripcion(SuscripcionRequestDTO, Authentication): SuscripcionDTO
  +eliminarSuscripcion(Integer, Authentication)
}

class AdminController <<@RequestMapping("/api/admin")>> {
  -AdminService adminService
  -DataSeederService dataSeederService
  +seedData(): String
  +getMotorData(): MotorCoherenciaDataDTO
  +getPromesas(Integer): List<SimpleItemDTO>
  +crearPromesa(CrearPromesaRequestDTO)
  +crearPolitico(CrearPoliticoRequestDTO)
  +crearLey(CrearLeyRequestDTO)
  +eliminarPolitico(Integer)
  +getPanel(): PanelControlDTO
  +getMantenimiento(): PanelMantenimientoDTO
  +postVinculo(VinculoRequestDTO)
  +postRespaldo()
  +postLimpiarCache()
  +getHistorico(): ReporteHistoricoDTO
  +postImportarLeyes()
  +postNormalizarLeyes(): LeyNormalizacionResultDTO
  +getLeyesSyncables(): List<LeySyncItemDTO>
}

class AssemblyImportController <<@RequestMapping({"/admin","/api/admin"})>> {
  -AssemblyImportService assemblyImportService
  +getAssemblyMembers(): ResponseEntity<List<AssemblyMemberDTO>>
  +getVotings(Long): ResponseEntity<List<VotingDTO>>
  +importSelectedVotings(Long, List<Long>): ResponseEntity<ImportResultDTO>
  +importLeyesForPoliticos(ImportLeyesRequestDTO): ResponseEntity<ImportResultDTO>
  +importVotings(Long): ResponseEntity<ImportResultDTO>
  +handleRuntimeException(RuntimeException): ResponseEntity<String>
}

class DashboardController <<@RequestMapping("/api/dashboard")>> {
  -DashboardService dashboardService
  +getStats(): DashboardStatsDTO
  +getMetricas(...): MetricasInteractivasDTO
  +exportStats(): ResponseEntity<String>
  +exportPoliticos(): ResponseEntity<String>
  +exportLeyes(): ResponseEntity<String>
}

class HolaController {
  +redirect(): String
}

AuthController --> "auth.JwtService"
AuthController --> "db.repository.UsuarioRepository"
PoliticoController --> PoliticoService
PoliticoController --> "db.repository.ComentarioRepository"
PoliticoImportController --> PoliticoImportService
LeyController --> LeyService
LeyController --> "db.repository.ComentarioRepository"
ValidacionController --> ValidacionService
AlertaController --> AlertaService
AdminController --> AdminService
AdminController --> "service.DataSeederService"
AssemblyImportController --> AssemblyImportService
DashboardController --> DashboardService

class PoliticoService
class PoliticoImportService
class LeyService
class ValidacionService
class AlertaService
class AdminService
class AssemblyImportService
class DashboardService
@enduml
```

## Módulo: DTOs — Autenticación y Dashboard (`dto`)

```plantuml
@startuml
left to right direction
hide empty members

class AuthRegisterRequestDTO <<DTO>> {
  -String email
  -String password
  -String nombre
  -String rol
}
class AuthLoginRequestDTO <<DTO>> {
  -String email
  -String password
}
class AuthResponseDTO <<DTO>> {
  -String token
  -Map<String,Object> user
}
class AuthMeResponseDTO <<DTO>> {
  -Integer id
  -String email
  -String nombre
  -String rol
}

class DashboardStatsDTO <<DTO>> {
  -long totalPoliticos
  -long totalLeyes
  -double promedioCoherenciaGlobal
  -long totalComentarios
  -List<RecentActivityDTO> actividadReciente
}
class RecentActivityDTO <<DTO>> {
  -String tipo
  -String usuario
  -String detalle
  -String fecha
}
class MetricasInteractivasDTO <<DTO>> {
  -String categoriaFiltro
  -String estadoFiltro
  -String desde
  -String hasta
  -long totalLeyes
  -long totalVotos
  -double promedioCoherenciaGlobal
  -List<MetricaItemDTO> leyesPorEstado
  -List<MetricaItemDTO> leyesPorCategoria
  -List<MetricaItemDTO> votosPorTipo
  -List<MetricaItemDTO> coherenciaPorCategoria
  -List<MetricaItemDTO> serieVotosPorMes
}
class MetricaItemDTO <<DTO>> {
  -String etiqueta
  -double valor
}

DashboardStatsDTO "1" o-- "*" RecentActivityDTO
MetricasInteractivasDTO "1" o-- "*" MetricaItemDTO
@enduml
```

## Módulo: DTOs — Políticos (`dto`)

```plantuml
@startuml
left to right direction
hide empty members

class CrearPoliticoRequestDTO <<DTO>> {
  -String nombreCompleto
  -String partidoPolitico
  -String cargoActual
  -String region
  -String comision
  -Boolean estaActivo
  -BigDecimal patrimonioDeclarado
  -String antecedentes
  -String fotoUrl
}
class ActualizarCampoPoliticoRequestDTO <<DTO>> {
  -String campo
  -String valor
}
class PerfilPoliticoDTO <<DTO>> {
  -String id
  -String nombre
  -String organizacion
  -String cargo
  -String patrimonio
  -String fotoUrl
  -String antecedentes
  -boolean estaActivo
  -Double porcentajeCoherencia
  -String estadoEtiqueta
  -Double indiceReputacion
  -long totalCalificaciones
  -String etiquetaReputacion
  -List<HistorialCoherenciaDTO> historial
  -List<HistorialCambioPerfilDTO> historialCambios
  -List<ComentarioDebateDTO> comentarios
}
class HistorialCoherenciaDTO <<DTO>> {
  -String leyTitulo
  -String votoReal
  -String resultado
  -String analisis
}
class HistorialCambioPerfilDTO <<DTO>> {
  -String campo
  -String valorAnterior
  -String valorNuevo
  -String fecha
}
class ComentarioDebateDTO <<DTO>> {
  -String id
  -String usuario
  -String fecha
  -String mensaje
  -String avatarUrl
  -Integer puntaje
}
class CartaPoliticoDTO <<DTO>> {
  -String id
  -String nombre
  -String organizacion
  -String fotoUrl
  -boolean estaActivo
  -String estadoEtiqueta
  -Double porcentajeCoherencia
  -Long cantidadProyectos
}
class GrillaPoliticosDTO <<DTO>> {
  -String id
  -List<CartaPoliticoDTO> cartas
  -int paginaActual
  -int totalPaginas
}
class FiltrosPoliticoDTO <<DTO>> {
  -List<String> partidos
  -List<String> regiones
  -List<String> comisiones
}
class ComparacionVotosDTO <<DTO>> {
  -List<ComparacionPoliticoDTO> politicos
  -List<ComparacionLeyDTO> leyesComparadas
  -long leyesEnComun
  -long coincidencias
  -double indiceCoincidencia
}
class ComparacionPoliticoDTO <<DTO>> {
  -String id
  -String nombre
  -String organizacion
  -String fotoUrl
  -long totalVotos
  -long votosFavor
  -long votosContra
  -long votosAbstencion
  -long asistencias
  -long inasistencias
  -double porcentajeAsistencia
  -double porcentajeCoherencia
}
class ComparacionLeyDTO <<DTO>> {
  -String leyId
  -String leyTitulo
  -Map<String,String> votos
  -boolean coinciden
}
class PromesaDTO <<DTO>> {
  -Integer id
  -String descripcion
  -String categoria
  -LocalDate fechaCreacion
  -Integer politicoId
}
class PromesaRequestDTO <<DTO>> {
  -String descripcion
  -String categoria
}
class CrearPromesaRequestDTO <<DTO>> {
  -Integer politicoId
  -String descripcion
  -String categoria
  -LocalDate fechaPromesa
}
class SimpleItemDTO <<DTO>> {
  -String id
  -String label
}

PerfilPoliticoDTO "1" o-- "*" HistorialCoherenciaDTO
PerfilPoliticoDTO "1" o-- "*" HistorialCambioPerfilDTO
PerfilPoliticoDTO "1" o-- "*" ComentarioDebateDTO
GrillaPoliticosDTO "1" o-- "*" CartaPoliticoDTO
ComparacionVotosDTO "1" o-- "*" ComparacionPoliticoDTO
ComparacionVotosDTO "1" o-- "*" ComparacionLeyDTO
@enduml
```

## Módulo: DTOs — Leyes y Coherencia (`dto`)

```plantuml
@startuml
left to right direction
hide empty members

class CrearLeyRequestDTO <<DTO>> {
  -String titulo
  -String codigo
  -String tipoExpediente
  -String proponente
  -String descripcionOriginal
  -String descripcionSimplificada
  -String impactoSocial
  -String categoria
  -String estado
  -LocalDate fechaIngreso
}
class CategoriaLeyRequestDTO <<DTO>> {
  -String categoria
}
class EstadoLeyRequestDTO <<DTO>> {
  -String estado
}
class AsistenciaVotoRequestDTO <<DTO>> {
  -Boolean asistencia
}
class ComentarioRequestDTO <<DTO>> {
  -String texto
  -Integer puntaje
}
class CalificacionRequestDTO <<DTO>> {
  -Integer puntaje
}
class FiltrosLeyDTO <<DTO>> {
  -List<String> categorias
  -List<String> estados
}
class ExpedienteLegislativoDTO <<DTO>> {
  -String id
  -String codigoExpediente
  -String titulo
  -String tituloLey
  -String categoria
  -String estado
  -boolean estaAprobado
  -String proponente
  -String accionUrl
}
class GrillaLeyesDTO <<DTO>> {
  -String id
  -List<ExpedienteLegislativoDTO> leyes
  -int paginaActual
  -int totalPaginas
}
class PerfilLeyDTO <<DTO>> {
  -ContenidoLeyDTO contenido
  -ResultadoVotacionDTO votacion
  -AuditoriaCoherenciaDTO auditoria
  -DebateCiudadanoDTO debate
  -VotingMatchSummaryDTO votingMatchSummary
}
class ContenidoLeyDTO <<DTO>> {
  -String id
  -String titulo
  -String resumenEjecutivo
  -String impactoSocial
  -String estado
  -String categoria
}
class ResultadoVotacionDTO <<DTO>> {
  -String id
  -String titulo
  -Long votosFavor
  -Long votosContra
  -Long votosAbstencion
  -Double valorPrincipal
  -String unitadPrincipal
  -int escalaMinima
  -int escalaMedia
  -int escalaMaxima
  -boolean tieneMenuOpciones
}
class AuditoriaCoherenciaDTO <<DTO>> {
  -String id
  -String titulo
  -String subtitulo
  -List<FilaAuditoriaDTO> filas
  -String textoVerMas
}
class FilaAuditoriaDTO <<DTO>> {
  -String id
  -String nombre
  -String fotoUrl
  -String bloque
  -String voto
  -String analisisCoherencia
  -String nivelCoherencia
}
class DebateCiudadanoDTO <<DTO>> {
  -String id
  -String titulo
  -Double puntuacionPromedio
  -Integer puntuacionMaxima
  -List<ComentarioDebateDTO> comentarios
  -String placeholderComentario
  -boolean tieneBotonEnviar
}
class VotingMatchSummaryDTO <<DTO>> {
  -int found
  -int notFound
  -int total
}
class AgendaLegislativaDTO <<DTO>> {
  -List<EventoAgendaDTO> eventos
  -long totalEventos
  -long totalIngresos
  -long totalVotaciones
}
class EventoAgendaDTO <<DTO>> {
  -String tipo
  -String fecha
  -String titulo
  -String detalle
  -String categoria
  -String estado
  -String leyId
  -Long conteoVotos
}
class DebateLegislativoDTO <<DTO>> {
  -String leyId
  -String titulo
  -String codigo
  -String estado
  -String categoria
  -String proponente
  -String fechaIngreso
  -String resumenOficial
  -String resumenSimplificado
  -long votosFavor
  -long votosContra
  -long votosAbstencion
  -long totalVotos
}
class VinculoRequestDTO <<DTO>> {
  -Integer promesaId
  -Integer leyId
  -String impactoEsperado
  -String nivelCoherencia
  -String analisis
}
class MotorCoherenciaDataDTO <<DTO>> {
  -List<SimpleItemDTO> politicos
  -List<SimpleItemDTO> leyes
}

GrillaLeyesDTO "1" o-- "*" ExpedienteLegislativoDTO
PerfilLeyDTO "1" *-- "1" ContenidoLeyDTO
PerfilLeyDTO "1" *-- "1" ResultadoVotacionDTO
PerfilLeyDTO "1" *-- "1" AuditoriaCoherenciaDTO
PerfilLeyDTO "1" *-- "1" DebateCiudadanoDTO
PerfilLeyDTO "1" *-- "0..1" VotingMatchSummaryDTO
AuditoriaCoherenciaDTO "1" o-- "*" FilaAuditoriaDTO
DebateCiudadanoDTO "1" o-- "*" ComentarioDebateDTO
AgendaLegislativaDTO "1" o-- "*" EventoAgendaDTO
@enduml
```

## Módulo: DTOs — Alertas, Validación y Reportes (`dto`)

```plantuml
@startuml
left to right direction
hide empty members

class SuscripcionRequestDTO <<DTO>> {
  -String categoria
}
class SuscripcionDTO <<DTO>> {
  -Integer id
  -String categoria
  -String fechaCreacion
}
class AlertaDTO <<DTO>> {
  -String tipo
  -String titulo
  -String categoria
  -String fecha
  -String detalle
  -String leyId
  -boolean nuevo
}
class ModeracionRequestDTO <<DTO>> {
  -String estado
  -String nota
}
class ComentarioModeracionDTO <<DTO>> {
  -Integer id
  -String texto
  -String usuario
  -String fecha
  -String estado
  -String notaModeracion
  -String contextoTipo
  -String contextoTitulo
  -String contextoId
}
class ReporteHistoricoDTO <<DTO>> {
  -long totalLeyes
  -long totalVotos
  -long votosFavor
  -long votosContra
  -long votosAbstencion
  -long leyesAprobadas
  -long leyesEnDebate
}
@enduml
```

## Módulo: DTOs — Administración e Importación (`dto`)

```plantuml
@startuml
left to right direction
hide empty members

class AccionAdminRequestDTO <<DTO>> {
  -String tipoAccion
  -String payload
}
class PanelControlDTO <<DTO>> {
  -String tituloSeccion
  -List<OpcionPanelDTO> opciones
}
class "PanelControlDTO.OpcionPanelDTO" as OpcionPanelDTO <<DTO (nested)>> {
  -String nombreOpcion
  -String icono
  -Integer notificacionBadge
}
class PanelMantenimientoDTO <<DTO>> {
  -String id
  -String titulo
  -String codigoReferencia
  -boolean estadoBaseDeDatos
  -String estadoEtiqueta
  -String fechaUltimoRespaldo
  -Integer cargaServidorPorcentaje
  -List<String> accionesDisponibles
}
class LeyNormalizacionResultDTO <<DTO>> {
  -int totalLeyes
  -int leyesActualizadas
  -int leyesSinCambios
}
class LeySyncItemDTO <<DTO>> {
  -Integer id
  -String titulo
  -Long externalId
}
class LeySyncStatusDTO <<DTO>> {
  -int total
  -int completed
  -int imported
  -int duplicated
  -int ignored
  -String currentLeyTitulo
}
class ImportLeyesRequestDTO <<DTO>> {
  -List<Integer> politicoIds
}
class ImportResultDTO <<DTO>> {
  -int found
  -int imported
  -int ignored
  -int duplicates
}
class PoliticoImportResultDTO <<DTO>> {
  -int found
  -int imported
  -int duplicates
}
class AssemblyMemberDTO <<DTO>> {
  -Long id
  -String firstName
  -String lastname
  -String territorial
}
class VotingDTO <<DTO>> {
  -Long id
  -String votingDate
  -String proposalDescription
  -String themeDescription
  -String description
}
class VotingDetailDTO <<DTO>> {
  -Long id
  -String firstName
  -String lastname
  -String description
  -String territorial
}

PanelControlDTO "1" o-- "*" OpcionPanelDTO
@enduml
```
