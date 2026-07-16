# Diagramas de Casos de Uso — Proyecto ControlF

## Diagrama General

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle

actor "Usuario" as Usuario
actor "Ciudadano" as Ciudadano
Ciudadano --|> Usuario
actor "Validador" as Validador
actor "Administrador" as Administrador
actor "API Asamblea Nacional" as APIAsamblea
actor "Gemini IA" as Gemini

rectangle "Autenticación y Autorización" {
  (CU-01 Registrar cuenta) as CU01
  (CU-02 Verificar disponibilidad) as CU02
  (CU-03 Iniciar sesión) as CU03
  (CU-04 Consultar sesión y perfil) as CU04
  (CU-05 Controlar acceso por rol) as CU05
}

rectangle "Actores Políticos" {
  (CU-06 Registrar perfil político) as CU06
  (CU-07 Eliminar perfil político) as CU07
  (CU-08 Explorar directorio de políticos) as CU08
  (CU-09 Ver perfil de político) as CU09
  (CU-10 Editar patrimonio y antecedentes) as CU10
  (CU-11 Comparar patrones de votación) as CU11
  (CU-12 Registrar promesa electoral) as CU12
}

rectangle "Actividad Legislativa" {
  (CU-13 Explorar directorio de leyes) as CU13
  (CU-14 Ver perfil de ley) as CU14
  (CU-15 Explicar ley con IA) as CU15
  (CU-16 Normalizar lenguaje legislativo masivo) as CU16
  (CU-17 Clasificar ley y actualizar estado) as CU17
  (CU-18 Registrar voto y asistencia) as CU18
  (CU-19 Consultar agenda y debates legislativos) as CU19
}

rectangle "Coherencia (Promesa - Voto)" {
  (CU-20 Vincular promesa con ley y calcular coherencia) as CU20
}

rectangle "Importación de Datos Externos" {
  (CU-21 Importar leyes y votaciones desde la Asamblea) as CU21
  (CU-22 Importar políticos desde la Asamblea) as CU22
}

rectangle "Panel Administrativo" {
  (CU-23 Monitorear estado del sistema) as CU23
  (CU-24 Generar reporte histórico agregado) as CU24
  (CU-25 Ejecutar mantenimiento del sistema) as CU25
}

rectangle "Validación de Contenido" {
  (CU-26 Moderar comentarios ciudadanos) as CU26
}

rectangle "Alertas y Suscripciones" {
  (CU-27 Gestionar suscripciones por categoría) as CU27
  (CU-28 Consultar feed de alertas) as CU28
}

rectangle "Reputación y Participación Ciudadana" {
  (CU-29 Calificar político o ley) as CU29
  (CU-30 Gestionar comentarios propios) as CU30
}

rectangle "Dashboard y Reportes" {
  (CU-31 Consultar dashboard y métricas) as CU31
  (CU-32 Exportar reportes en CSV) as CU32
}

Usuario --> CU01
Usuario --> CU02
Usuario --> CU03
Usuario --> CU04
Usuario --> CU08
Usuario --> CU09
Usuario --> CU11
Usuario --> CU13
Usuario --> CU14
Usuario --> CU15
Usuario --> CU19
Usuario --> CU31
Usuario --> CU32

Ciudadano --> CU27
Ciudadano --> CU28
Ciudadano --> CU29
Ciudadano --> CU30

Validador --> CU26

Administrador --> CU06
Administrador --> CU07
Administrador --> CU10
Administrador --> CU12
Administrador --> CU16
Administrador --> CU17
Administrador --> CU18
Administrador --> CU20
Administrador --> CU21
Administrador --> CU22
Administrador --> CU23
Administrador --> CU24
Administrador --> CU25
Administrador --> CU26
Administrador --> CU29
Administrador --> CU30
Administrador --> CU32

Gemini --> CU15
Gemini --> CU16
APIAsamblea --> CU21
APIAsamblea --> CU22

CU01 ..> CU02 : <<include>>
CU06 ..> CU05 : <<include>>
CU07 ..> CU05 : <<include>>
CU10 ..> CU05 : <<include>>
CU12 ..> CU05 : <<include>>
CU16 ..> CU05 : <<include>>
CU17 ..> CU05 : <<include>>
CU18 ..> CU05 : <<include>>
CU20 ..> CU05 : <<include>>
CU21 ..> CU05 : <<include>>
CU22 ..> CU05 : <<include>>
CU23 ..> CU05 : <<include>>
CU24 ..> CU05 : <<include>>
CU25 ..> CU05 : <<include>>
CU26 ..> CU05 : <<include>>

CU09 ..> CU20 : <<include>>
CU09 ..> CU29 : <<include>>
CU14 ..> CU20 : <<include>>
CU16 ..> CU15 : <<extend>>
CU30 ..> CU26 : <<extend>>

CU12 ..> CU20 : prerequisito de
CU18 ..> CU20 : prerequisito de
CU18 ..> CU14 : actualiza resultado agregado
CU28 ..> CU27 : prerequisito de
@enduml
```

## Módulo: Autenticación y Autorización

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Administrador" as Administrador
actor "Validador" as Validador

rectangle "Autenticación y Autorización" {
  (CU-01 Registrar cuenta) as CU01
  (CU-02 Verificar disponibilidad) as CU02
  (CU-03 Iniciar sesión) as CU03
  (CU-04 Consultar sesión y perfil) as CU04
  (CU-05 Controlar acceso por rol) as CU05
}

Usuario --> CU01
Usuario --> CU02
Usuario --> CU03
Usuario --> CU04
CU01 ..> CU02 : <<include>>

note right of CU05
  CU-05 es transversal: se incluye
  desde los CU de otros módulos
  cuyo actor principal es
  Administrador o Validador.
end note
@enduml
```

## Módulo: Actores Políticos

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Administrador" as Administrador

rectangle "Actores Políticos" {
  (CU-06 Registrar perfil político) as CU06
  (CU-07 Eliminar perfil político) as CU07
  (CU-08 Explorar directorio de políticos) as CU08
  (CU-09 Ver perfil de político) as CU09
  (CU-10 Editar patrimonio y antecedentes) as CU10
  (CU-11 Comparar patrones de votación) as CU11
  (CU-12 Registrar promesa electoral) as CU12
}

(CU-05 Controlar acceso por rol) as CU05
(CU-20 Vincular promesa con ley y calcular coherencia) as CU20
(CU-29 Calificar político o ley) as CU29

Administrador --> CU06
Administrador --> CU07
Administrador --> CU10
Administrador --> CU12
Usuario --> CU08
Usuario --> CU09
Usuario --> CU11

CU06 ..> CU05 : <<include>>
CU07 ..> CU05 : <<include>>
CU10 ..> CU05 : <<include>>
CU12 ..> CU05 : <<include>>
CU09 ..> CU20 : <<include>>
CU09 ..> CU29 : <<include>>
CU12 ..> CU20 : prerequisito de

note bottom of CU20
  CU-20 pertenece al módulo
  Coherencia (Promesa-Voto).
end note
note bottom of CU29
  CU-29 pertenece al módulo
  Reputación y Participación Ciudadana.
end note
@enduml
```

## Módulo: Actividad Legislativa

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Administrador" as Administrador
actor "Gemini IA" as Gemini

rectangle "Actividad Legislativa" {
  (CU-13 Explorar directorio de leyes) as CU13
  (CU-14 Ver perfil de ley) as CU14
  (CU-15 Explicar ley con IA) as CU15
  (CU-16 Normalizar lenguaje legislativo masivo) as CU16
  (CU-17 Clasificar ley y actualizar estado) as CU17
  (CU-18 Registrar voto y asistencia) as CU18
  (CU-19 Consultar agenda y debates legislativos) as CU19
}

(CU-05 Controlar acceso por rol) as CU05
(CU-20 Vincular promesa con ley y calcular coherencia) as CU20

Usuario --> CU13
Usuario --> CU14
Usuario --> CU15
Gemini --> CU15
Administrador --> CU16
Gemini --> CU16
Administrador --> CU17
Administrador --> CU18
Usuario --> CU19

CU16 ..> CU15 : <<extend>>
CU16 ..> CU05 : <<include>>
CU17 ..> CU05 : <<include>>
CU18 ..> CU05 : <<include>>
CU14 ..> CU20 : <<include>>
CU18 ..> CU14 : actualiza resultado agregado

note bottom of CU20
  CU-20 pertenece al módulo
  Coherencia (Promesa-Voto).
end note
@enduml
```

## Módulo: Coherencia (Promesa - Voto)

```plantuml
@startuml
left to right direction
actor "Administrador" as Administrador

rectangle "Coherencia (Promesa - Voto)" {
  (CU-20 Vincular promesa con ley y calcular coherencia) as CU20
}

(CU-05 Controlar acceso por rol) as CU05
(CU-12 Registrar promesa electoral) as CU12
(CU-18 Registrar voto y asistencia) as CU18
(CU-09 Ver perfil de político) as CU09

Administrador --> CU20
CU20 ..> CU05 : <<include>>
CU12 ..> CU20 : prerequisito de
CU18 ..> CU20 : prerequisito de
CU09 ..> CU20 : <<include>>

note bottom of CU12
  CU-12 pertenece al módulo
  Actores Políticos.
end note
note bottom of CU18
  CU-18 pertenece al módulo
  Actividad Legislativa.
end note
note bottom of CU09
  CU-09 pertenece al módulo
  Actores Políticos.
end note
@enduml
```

## Módulo: Importación de Datos Externos

```plantuml
@startuml
left to right direction
actor "Administrador" as Administrador
actor "API Asamblea Nacional" as APIAsamblea

rectangle "Importación de Datos Externos" {
  (CU-21 Importar leyes y votaciones desde la Asamblea) as CU21
  (CU-22 Importar políticos desde la Asamblea) as CU22
}

(CU-05 Controlar acceso por rol) as CU05

Administrador --> CU21
APIAsamblea --> CU21
Administrador --> CU22
APIAsamblea --> CU22

CU21 ..> CU05 : <<include>>
CU22 ..> CU05 : <<include>>
@enduml
```

## Módulo: Panel Administrativo

```plantuml
@startuml
left to right direction
actor "Administrador" as Administrador

rectangle "Panel Administrativo" {
  (CU-23 Monitorear estado del sistema) as CU23
  (CU-24 Generar reporte histórico agregado) as CU24
  (CU-25 Ejecutar mantenimiento del sistema) as CU25
}

(CU-05 Controlar acceso por rol) as CU05

Administrador --> CU23
Administrador --> CU24
Administrador --> CU25

CU23 ..> CU05 : <<include>>
CU24 ..> CU05 : <<include>>
CU25 ..> CU05 : <<include>>
@enduml
```

## Módulo: Validación de Contenido

```plantuml
@startuml
left to right direction
actor "Validador" as Validador
actor "Administrador" as Administrador

rectangle "Validación de Contenido" {
  (CU-26 Moderar comentarios ciudadanos) as CU26
}

(CU-05 Controlar acceso por rol) as CU05
(CU-30 Gestionar comentarios propios) as CU30

Validador --> CU26
Administrador --> CU26
CU26 ..> CU05 : <<include>>
CU30 ..> CU26 : <<extend>>

note bottom of CU30
  CU-30 pertenece al módulo
  Reputación y Participación Ciudadana.
end note
@enduml
```

## Módulo: Alertas y Suscripciones

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Ciudadano" as Ciudadano
Ciudadano --|> Usuario

rectangle "Alertas y Suscripciones" {
  (CU-27 Gestionar suscripciones por categoría) as CU27
  (CU-28 Consultar feed de alertas) as CU28
}

Ciudadano --> CU27
Ciudadano --> CU28
CU28 ..> CU27 : prerequisito de
@enduml
```

## Módulo: Reputación y Participación Ciudadana

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Ciudadano" as Ciudadano
Ciudadano --|> Usuario
actor "Administrador" as Administrador

rectangle "Reputación y Participación Ciudadana" {
  (CU-29 Calificar político o ley) as CU29
  (CU-30 Gestionar comentarios propios) as CU30
}

(CU-26 Moderar comentarios ciudadanos) as CU26
(CU-09 Ver perfil de político) as CU09
(CU-14 Ver perfil de ley) as CU14

Ciudadano --> CU29
Administrador --> CU29
Ciudadano --> CU30
Administrador --> CU30

CU30 ..> CU26 : <<extend>>

note bottom of CU26
  CU-26 pertenece al módulo
  Validación de Contenido.
end note
note bottom of CU09
  CU-09 pertenece al módulo
  Actores Políticos. Incluye
  el resultado de CU-29.
end note
note bottom of CU14
  CU-14 pertenece al módulo
  Actividad Legislativa. Incluye
  el resultado de CU-29.
end note
@enduml
```

## Módulo: Dashboard y Reportes

```plantuml
@startuml
left to right direction
actor "Usuario" as Usuario
actor "Administrador" as Administrador

rectangle "Dashboard y Reportes" {
  (CU-31 Consultar dashboard y métricas) as CU31
  (CU-32 Exportar reportes en CSV) as CU32
}

Usuario --> CU31
Usuario --> CU32
Administrador --> CU32
@enduml
```
