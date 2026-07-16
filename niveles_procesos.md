# Jerarquía de Procesos — ControlF

## Control de versiones

| Versión | Fecha | Autor | Descripción |
|---|---|---|---|
| 1.0 | 2026-07-15 | Equipo de Ingeniería / Documentación asistida | Versión inicial de la jerarquía de procesos |
| 1.1 | 2026-07-15 | Equipo de Ingeniería / Documentación asistida | Corrección: el índice de reputación se calcula a partir de calificaciones ciudadanas (`Calificacion.puntaje`), no del historial de coherencia. Se reubica el subproceso de P4.0 a P2.0 |

## Origen y método

Esta jerarquía se derivó exclusivamente de la funcionalidad real implementada en el repositorio, verificada en:

- Controladores: `controlF/src/main/java/com/controlf/controller/*.java` (endpoints REST).
- Servicios: `controlF/src/main/java/com/controlf/service/*.java` (lógica de negocio).
- Entidades y enums de dominio: `controlF/src/main/java/com/controlf/db/schema/*.java`.
- Rutas del frontend: `controlf_fronted/src/App.tsx`.
- `casos_de_uso_checklist.md` e `INFORME_TECNICO.md`.

No se incluyen procesos hipotéticos ni funcionalidades no evidenciadas en el código.

---

## Nivel 0 (Macroproceso)

**M0: Plataforma de Auditoría Ciudadana para el Seguimiento de Leyes, Políticos y su Coherencia Legislativa (ControlF).**

## Nivel 1 (Procesos básicos)

- **P1.0** Gestión de Identidad y Control de Acceso.
- **P2.0** Gestión de Perfiles Políticos.
- **P3.0** Gestión de Actividad Legislativa.
- **P4.0** Auditoría de Coherencia Política.
- **P5.0** Participación y Auditoría Ciudadana.
- **P6.0** Administración, Métricas y Mantenimiento del Sistema.

## Nivel 2 (Subprocesos)

### P1.0 Gestión de Identidad y Control de Acceso

- P1.1 Registro de nuevas cuentas de usuario.
- P1.2 Verificación de disponibilidad de correo/usuario.
- P1.3 Autenticación de usuarios mediante credenciales.
- P1.4 Emisión y validación de tokens de sesión (JWT).
- P1.5 Consulta de la identidad del usuario autenticado.
- P1.6 Autorización de acceso por rol (ciudadano, validador, administrador).
- P1.7 Cierre de sesión y gestión del estado de sesión en el cliente.
- P1.8 Validación de reglas de seguridad de contraseñas y correo.

### P2.0 Gestión de Perfiles Políticos

- P2.1 Registro y alta de nuevos políticos.
- P2.2 Exploración del directorio de políticos (listado, filtros y paginación).
- P2.3 Consulta de perfil detallado de un político.
- P2.4 Actualización de datos de perfil (patrimonio y antecedentes) con historial de cambios.
- P2.5 Registro y consulta de promesas de campaña.
- P2.6 Importación de políticos desde la Asamblea Nacional.
- P2.7 Eliminación de perfiles políticos.
- P2.8 Cálculo del índice de reputación mediante calificaciones ciudadanas (promedio de `Calificacion.puntaje`, escala 1-5).

### P3.0 Gestión de Actividad Legislativa

- P3.1 Importación de leyes desde la Asamblea Nacional.
- P3.2 Normalización del lenguaje legislativo.
- P3.3 Clasificación y categorización de leyes.
- P3.4 Actualización del estado de tramitación de una ley.
- P3.5 Registro de asistencia a votaciones.
- P3.6 Consulta de agenda legislativa y debates.
- P3.7 Consulta de contenido y explicación asistida por IA de una ley.
- P3.8 Sincronización del detalle de votaciones con la fuente externa.

### P4.0 Auditoría de Coherencia Política

- P4.1 Vinculación de promesas de campaña con leyes.
- P4.2 Cálculo del nivel de coherencia entre voto emitido y promesa (cumple/incumple/ambiguo).
- P4.3 Consulta del motor de coherencia (datos agregados para vinculación).
- P4.4 Generación de auditoría de coherencia por ley.
- P4.5 Comparación de patrones de voto entre políticos.
- P4.6 Consulta de resultados de votación por ley.
- P4.7 Consulta del historial de coherencia por político.

### P5.0 Participación y Auditoría Ciudadana

- P5.1 Publicación de comentarios sobre políticos y leyes.
- P5.2 Edición y eliminación de comentarios propios.
- P5.3 Calificación de políticos y leyes.
- P5.4 Moderación de comentarios ciudadanos (validación de contenido).
- P5.5 Consulta del resumen de moderación.
- P5.6 Gestión de suscripciones a alertas por categoría.
- P5.7 Generación y consulta de alertas de nuevas leyes o votaciones.
- P5.8 Consulta del debate ciudadano asociado a una ley.

### P6.0 Administración, Métricas y Mantenimiento del Sistema

- P6.1 Consulta de estadísticas generales del dashboard.
- P6.2 Consulta de métricas interactivas con filtros.
- P6.3 Exportación de datos (estadísticas, políticos, leyes).
- P6.4 Consulta del panel de control administrativo (estado de seguridad).
- P6.5 Mantenimiento del sistema (respaldo y limpieza de caché).
- P6.6 Registro y consulta de logs de acciones del sistema.
- P6.7 Generación de reportes históricos agregados.
- P6.8 Poblado inicial de datos de prueba (seed).

---

## Nota técnica: reputación vs. coherencia

El sistema mantiene dos métricas independientes sobre un político que no deben confundirse:

- **Índice de reputación** (`indiceReputacion`, P2.8): promedio aritmético de calificaciones ciudadanas directas (`Calificacion.puntaje`, 1-5), calculado en `PoliticoService.getPoliticoProfile()` vía `CalificacionRepository.findAveragePuntajeByPoliticoId`. No depende de ningún análisis de coherencia.
- **Porcentaje de coherencia** (`porcentajeCoherencia`, P4.2/P4.7): promedio calculado a partir de `VinculoPromesaLey.nivelCoherencia` (`CUMPLE`=100, `AMBIGUO`=50, `INCUMPLE`=0), vía `VinculoPromesaLeyRepository.findAverageCoherenciaByPoliticoId`, con etiquetas por umbrales configurables (`UMBRAL_COHERENCIA_ALTA`, `UMBRAL_COHERENCIA_MEDIA`).

## Trazabilidad con casos de uso (`casos_de_uso_checklist.md`)

| Caso de uso | Subprocesos relacionados |
|---|---|
| CU-01 Importar Leyes | P3.1 |
| CU-02 Normalizar Lenguaje Legislativo | P3.2 |
| CU-03 Gestionar Perfiles Políticos | P2.1–P2.4, P2.7 |
| CU-04 Vincular Votos con Promesas | P4.1 |
| CU-05 Calcular Coherencia Política | P4.2 |
| CU-06 Consultar Información Política | P2.2–P2.3, P3.6–P3.7 |
| CU-07 Realizar Auditoría Ciudadana | P4.4 |
| CU-08 Visualizar Métricas | P6.1–P6.2 |
| CU-09 Calificar a un Político | P5.3, P2.8 |
| CU-10 Comentar sobre un Político | P5.1–P5.2 |
| CU-11 Generar Reportes | P6.7 |
