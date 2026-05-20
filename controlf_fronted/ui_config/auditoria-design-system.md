# 🎨 Auditoría C. — Design System & Style Guide
**Versión:** 1.0 | **Tema:** Dark Mode | **Uso:** Configuración para generación de interfaces con IA (UX Pilot)

---

## 📐 FILOSOFÍA DE DISEÑO

- **Estilo general:** Dark dashboard — sobrio, institucional, con alto contraste para facilitar lectura de datos.
- **Tono visual:** Profesional, confiable, gubernamental. Sin elementos decorativos innecesarios.
- **Principios:** Densidad de información balanceada · Jerarquía visual clara · Semáforo de estados con color · Mínima superficie de ruido visual.
- **Modo:** Exclusivamente **Dark Mode**. No hay versión light.

---

## 🎨 1. PALETA DE COLORES

### 1.1 Fondos (Backgrounds)

| Token | HEX | Uso |
|-------|-----|-----|
| `--bg-base` | `#0D0F14` | Fondo raíz de la aplicación (html/body) |
| `--bg-sidebar` | `#111318` | Sidebar de navegación lateral |
| `--bg-card` | `#181B22` | Tarjetas y paneles principales |
| `--bg-card-elevated` | `#1E2230` | Tarjetas en hover o con elevación extra |
| `--bg-input` | `#13151C` | Campos de formulario, áreas de texto |
| `--bg-overlay` | `rgba(0,0,0,0.55)` | Modales y overlays |
| `--bg-active-nav` | `#1A2035` | Ítem activo del sidebar |
| `--bg-tag` | `#1C2033` | Fondo de etiquetas/tags neutros |

### 1.2 Colores de Acento

| Token | HEX | Uso |
|-------|-----|-----|
| `--accent-blue` | `#3B82F6` | Acento principal: links, botones primarios, íconos activos, borde de avatar destacado |
| `--accent-blue-dim` | `#1D3A6B` | Fondo de estados azules (hover, selected) |
| `--accent-blue-glow` | `rgba(59,130,246,0.20)` | Glow/sombra en elementos azul |

### 1.3 Colores de Estado (Semáforo)

| Token | HEX | Uso |
|-------|-----|-----|
| `--state-coherent` | `#22C55E` | Badge / label "Coherente" |
| `--state-coherent-bg` | `#14532D` | Fondo badge Coherente |
| `--state-incoherent` | `#EF4444` | Badge / label "Incoherente" |
| `--state-incoherent-bg` | `#450A0A` | Fondo badge Incoherente |
| `--state-abstention` | `#F59E0B` | Badge / label "Abstención" |
| `--state-abstention-bg` | `#451A03` | Fondo badge Abstención |
| `--state-favor` | `#22C55E` | Badge "A Favor" |
| `--state-favor-bg` | `#14532D` | Fondo badge "A Favor" |
| `--state-active` | `#22C55E` | Dot/chip "Activo" |
| `--state-active-bg` | `#14532D` | Fondo chip "Activo" |

### 1.4 Colores de Texto

| Token | HEX | Uso |
|-------|-----|-----|
| `--text-primary` | `#F1F5F9` | Títulos, nombres, texto principal |
| `--text-secondary` | `#94A3B8` | Subtítulos, metadatos, labels secundarios |
| `--text-muted` | `#4B5563` | Texto deshabilitado, placeholders |
| `--text-inverse` | `#0D0F14` | Texto sobre fondos claros |
| `--text-link` | `#3B82F6` | Links, acciones "Ver Todo" |
| `--text-value-high` | `#22C55E` | Valores positivos (porcentajes altos) |
| `--text-value-warn` | `#F59E0B` | Valores en advertencia |
| `--text-value-danger` | `#EF4444` | Valores críticos |

### 1.5 Bordes y Divisores

| Token | HEX | Uso |
|-------|-----|-----|
| `--border-subtle` | `#1E2535` | Bordes de tarjetas y divisores entre secciones |
| `--border-medium` | `#2A3548` | Bordes de inputs, tablas |
| `--border-strong` | `#3B4D6B` | Bordes de elementos destacados |
| `--border-accent` | `#3B82F6` | Borde de avatar principal / elementos seleccionados |

### 1.6 Colores de Partidos / Etiquetas de Identidad

| Token | HEX | Uso |
|-------|-----|-----|
| `--party-flag-yellow` | `#F59E0B` | Banderita del partido (Movimiento Transparencia) |
| `--patrimony-green` | `#22C55E` | Badge de patrimonio declarado |

---

## ✍️ 2. TIPOGRAFÍA

### 2.1 Fuentes

| Rol | Fuente | Fallback |
|-----|--------|---------|
| **Display / Títulos grandes** | `Inter` | `system-ui, sans-serif` |
| **Cuerpo / UI** | `Inter` | `system-ui, sans-serif` |
| **Monoespaciado (datos/códigos)** | `JetBrains Mono` | `monospace` |

> **Nota para UX Pilot:** Usar Inter como fuente primaria. Pesos usados: 400 (regular), 500 (medium), 600 (semibold), 700 (bold).

### 2.2 Escala Tipográfica

| Token | Tamaño | Peso | Line-height | Uso |
|-------|--------|------|-------------|-----|
| `--text-xs` | `11px` | 400 | 1.4 | Labels de tabla, meta-info |
| `--text-sm` | `12px` | 400–500 | 1.5 | Texto secundario, comentarios, badges |
| `--text-base` | `13px` | 400 | 1.6 | Cuerpo de texto, descripciones |
| `--text-md` | `14px` | 500 | 1.5 | Labels de inputs, subtítulos de sección |
| `--text-lg` | `16px` | 600 | 1.4 | Nombres de ley/proyecto en tabla |
| `--text-xl` | `18px` | 600 | 1.3 | Subtítulos de card |
| `--text-2xl` | `22px` | 700 | 1.2 | Nombre del político (h1 de perfil) |
| `--text-metric` | `36px–48px` | 700 | 1.0 | Número principal de métrica (85%) |
| `--text-score` | `13px` | 500 | 1.0 | Etiqueta de score "8.5/10" |

### 2.3 Reglas de Estilo de Texto

- Todos los títulos: `color: var(--text-primary)`, `font-weight: 700` o `600`.
- Labels de sección (ej. "HISTORIAL DE COHERENCIA"): `font-size: 11px`, `font-weight: 600`, `letter-spacing: 0.08em`, `text-transform: uppercase`, `color: var(--text-secondary)`.
- Nombres propios (políticos): `font-size: 22px`, `font-weight: 700`, `color: var(--text-primary)`.
- Texto de subsección (cargo): `font-size: 12px`, `font-weight: 500`, `color: var(--text-secondary)`, con ícono de edificio antes.
- Valores numéricos destacados: usar `font-variant-numeric: tabular-nums` para alineación.

---

## 📏 3. ESPACIADO Y LAYOUT

### 3.1 Escala de Espaciado (base 4px)

| Token | Valor | Uso |
|-------|-------|-----|
| `--space-1` | `4px` | Micro-gaps entre íconos y texto |
| `--space-2` | `8px` | Padding interno de badges |
| `--space-3` | `12px` | Gap entre elementos de formulario |
| `--space-4` | `16px` | Padding interno de cards |
| `--space-5` | `20px` | Separación entre secciones menores |
| `--space-6` | `24px` | Padding de cards principales |
| `--space-8` | `32px` | Separación entre bloques de contenido |
| `--space-10` | `40px` | Margen entre secciones mayores |

### 3.2 Layout General

```
┌─────────────────────────────────────────────────────────────┐
│  SIDEBAR (160px fijo)  │  MAIN CONTENT AREA (flex-grow: 1)  │
│                        │  ┌────────────────────────────────┐ │
│  Logo + App Name       │  │ TOPBAR / BREADCRUMB (48px h)  │ │
│  ─────────────────     │  └────────────────────────────────┘ │
│  MENU label            │  ┌──────────────┐ ┌──────────────┐  │
│    Dashboard           │  │  CARD PERFIL │ │ CARD MÉTRICA │  │
│  ▶ Políticos (active)  │  │  (col-span 7)│ │ (col-span 5) │  │
│    Leyes               │  └──────────────┘ └──────────────┘  │
│  ─────────────────     │  ┌──────────────┐ ┌──────────────┐  │
│  SYSTEM label          │  │ CARD HISTOR. │ │ CARD PARTIC. │  │
│    Admin               │  │  (col-span 7)│ │ (col-span 5) │  │
│                        │  └──────────────┘ └──────────────┘  │
│  ─────────────────     │                                      │
│  User (bottom)         │                                      │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 Grid del Contenido Principal

- **Sistema:** CSS Grid de 12 columnas.
- **Gap entre columnas:** `16px`
- **Gap entre filas:** `16px`
- **Card Perfil:** `grid-column: span 7`
- **Card Métrica:** `grid-column: span 5`
- **Card Historial Legislativo:** `grid-column: span 7`
- **Card Participación Ciudadana:** `grid-column: span 5`
- **Padding del área de contenido:** `24px`

### 3.4 Sidebar

- **Ancho:** `160px` fijo
- **Padding interno:** `16px 12px`
- **Separación entre grupos de menú:** `24px`
- **Altura de ítem de menú:** `36px`
- **Padding ítem:** `8px 12px`
- **Border-radius ítem activo:** `8px`

---

## 🔲 4. BORDER RADIUS

| Token | Valor | Uso |
|-------|-------|-----|
| `--radius-sm` | `4px` | Badges pequeños, chips de estado |
| `--radius-md` | `8px` | Botones, inputs, ítems de menú |
| `--radius-lg` | `12px` | Tarjetas (cards) principales |
| `--radius-xl` | `16px` | Tarjetas grandes / modales |
| `--radius-full` | `9999px` | Avatares, dots de estado, pills |

---

## 🌑 5. SOMBRAS Y ELEVACIÓN

| Token | Valor | Uso |
|-------|-------|-----|
| `--shadow-card` | `0 1px 3px rgba(0,0,0,0.4), 0 4px 12px rgba(0,0,0,0.3)` | Sombra base de tarjetas |
| `--shadow-elevated` | `0 4px 20px rgba(0,0,0,0.5)` | Tarjetas elevadas, dropdowns |
| `--shadow-modal` | `0 20px 60px rgba(0,0,0,0.7)` | Modales |
| `--shadow-accent` | `0 0 16px rgba(59,130,246,0.30)` | Glow en métricas o elementos activos con acento azul |
| `--shadow-sidebar` | `2px 0 8px rgba(0,0,0,0.4)` | Sombra lateral del sidebar |

---

## 🧩 6. COMPONENTES

### 6.1 Tarjeta (Card)

```
background: var(--bg-card)
border: 1px solid var(--border-subtle)
border-radius: var(--radius-lg)          /* 12px */
padding: var(--space-6)                  /* 24px */
box-shadow: var(--shadow-card)
```

**Variante elevada (hover o destacada):**
```
background: var(--bg-card-elevated)
border: 1px solid var(--border-medium)
box-shadow: var(--shadow-elevated)
```

**Header de card:**
```
font-size: 11px
font-weight: 600
letter-spacing: 0.08em
text-transform: uppercase
color: var(--text-secondary)
margin-bottom: 16px
display: flex
align-items: center
justify-content: space-between
```

---

### 6.2 Sidebar / Navegación

```
/* Contenedor */
width: 160px
background: var(--bg-sidebar)
border-right: 1px solid var(--border-subtle)
box-shadow: var(--shadow-sidebar)
padding: 16px 12px

/* Label de grupo */
font-size: 10px
font-weight: 600
letter-spacing: 0.10em
text-transform: uppercase
color: var(--text-muted)
margin: 20px 0 8px 8px

/* Ítem de menú */
display: flex
align-items: center
gap: 10px
height: 36px
padding: 0 12px
border-radius: var(--radius-md)
font-size: 13px
font-weight: 500
color: var(--text-secondary)
cursor: pointer
transition: background 150ms ease, color 150ms ease

/* Ítem activo */
background: var(--bg-active-nav)
color: var(--accent-blue)
font-weight: 600

/* Ícono en ítem */
width: 16px
height: 16px
color: inherit
flex-shrink: 0
```

---

### 6.3 Card de Perfil (Político)

```
/* Contenedor */
display: flex
gap: 20px
align-items: flex-start
padding: 24px

/* Avatar */
width: 72px
height: 72px
border-radius: var(--radius-full)
border: 2px solid var(--border-accent)    /* azul */
object-fit: cover
flex-shrink: 0

/* Nombre */
font-size: 22px
font-weight: 700
color: var(--text-primary)
margin-bottom: 4px

/* Cargo */
font-size: 12px
font-weight: 500
color: var(--text-secondary)
display: flex
align-items: center
gap: 6px
margin-bottom: 12px

/* Badges de meta-info (partido, patrimonio) */
display: inline-flex
align-items: center
gap: 6px
padding: 4px 10px
border-radius: var(--radius-sm)
font-size: 12px
font-weight: 500
background: var(--bg-tag)
border: 1px solid var(--border-subtle)
color: var(--text-primary)

/* Chip "ACTIVO" */
background: var(--state-active-bg)
color: var(--state-active)
font-size: 10px
font-weight: 700
letter-spacing: 0.06em
text-transform: uppercase
padding: 3px 8px
border-radius: var(--radius-sm)

/* Descripción biográfica */
font-size: 13px
color: var(--text-secondary)
line-height: 1.6
margin-top: 12px
max-width: 360px
```

---

### 6.4 Badges de Estado

```
/* Base */
display: inline-flex
align-items: center
gap: 4px
padding: 3px 10px
border-radius: var(--radius-sm)
font-size: 12px
font-weight: 600
letter-spacing: 0.02em
white-space: nowrap

/* COHERENTE */
background: var(--state-coherent-bg)    /* #14532D */
color: var(--state-coherent)            /* #22C55E */

/* INCOHERENTE */
background: var(--state-incoherent-bg)  /* #450A0A */
color: var(--state-incoherent)          /* #EF4444 */

/* ABSTENCIÓN */
background: var(--state-abstention-bg)  /* #451A03 */
color: var(--state-abstention)          /* #F59E0B */

/* A FAVOR */
background: var(--state-favor-bg)
color: var(--state-favor)

/* Dot indicador (círculo de estado) */
width: 7px
height: 7px
border-radius: var(--radius-full)
background: currentColor   /* hereda el color del badge padre */
flex-shrink: 0
```

---

### 6.5 Tabla de Historial Legislativo

```
/* Contenedor */
width: 100%
border-collapse: collapse

/* Encabezado de columna */
font-size: 11px
font-weight: 600
letter-spacing: 0.06em
text-transform: uppercase
color: var(--text-muted)
padding: 0 0 12px 0
text-align: left
border-bottom: 1px solid var(--border-subtle)

/* Fila */
border-bottom: 1px solid var(--border-subtle)
transition: background 150ms ease

/* Fila hover */
background: var(--bg-card-elevated)

/* Celda */
padding: 14px 0
vertical-align: middle

/* Nombre de ley */
font-size: 14px
font-weight: 600
color: var(--text-primary)
margin-bottom: 2px

/* Subtexto de ley (nombre largo / referencia) */
font-size: 11px
color: var(--text-muted)
margin-top: 2px

/* Fecha */
font-size: 12px
color: var(--text-secondary)

/* Columna de Análisis */
text-align: right
```

**Botones de filtro de tabla (Todos / Incoherentes):**
```
display: inline-flex
gap: 4px
padding: 4px
background: var(--bg-input)
border-radius: var(--radius-md)

/* Opción inactiva */
padding: 5px 14px
border-radius: var(--radius-md)
font-size: 12px
font-weight: 500
color: var(--text-secondary)
cursor: pointer

/* Opción activa */
background: var(--bg-card-elevated)
color: var(--text-primary)
font-weight: 600
```

---

### 6.6 Métrica de Coherencia (Gauge/Circular)

```
/* Contenedor card */
display: flex
flex-direction: column
align-items: center
justify-content: center
gap: 12px
padding: 24px

/* Gauge circular */
width: 120px – 160px
stroke-width: 8–10px
track-color: var(--border-subtle)    /* #1E2535 */
progress-color: var(--accent-blue)   /* #3B82F6 */
border-radius: var(--radius-full)

/* Número central */
font-size: 36px–48px
font-weight: 700
color: var(--text-primary)

/* Sufijo "%" */
font-size: 20px
font-weight: 600
color: var(--text-secondary)
vertical-align: super

/* Label "GLOBAL" debajo del número */
font-size: 10px
font-weight: 600
letter-spacing: 0.1em
text-transform: uppercase
color: var(--text-muted)

/* Score de Confianza */
font-size: 13px
color: var(--text-secondary)
margin-top: 8px
/* Valor del score */
color: var(--text-primary)
font-weight: 600

/* Variante línea (sparkline) */
stroke: #B45309   /* dorado cálido */
fill: rgba(180,83,9,0.15)
stroke-width: 2px
```

**Sub-métricas (Promesas Cumplidas / Votos Contradictorios):**
```
display: grid
grid-template-columns: 1fr 1fr
gap: 12px
margin-top: 16px

/* Bloque */
background: var(--bg-input)
border: 1px solid var(--border-subtle)
border-radius: var(--radius-md)
padding: 12px
text-align: center

/* Número */
font-size: 28px
font-weight: 700
color: var(--text-primary)

/* Número de alerta */
color: var(--state-incoherent)   /* rojo para votos contradictorios */

/* Label */
font-size: 11px
color: var(--text-secondary)
```

---

### 6.7 Participación Ciudadana (Ratings y Comentarios)

```
/* Contenedor card */
display: flex
flex-direction: column
gap: 16px

/* Área de rating de estrellas */
display: flex
align-items: center
gap: 4px

/* Estrella activa */
color: #F59E0B          /* dorado/ámbar */
font-size: 18px

/* Estrella inactiva */
color: var(--border-medium)

/* Input de comentario */
background: var(--bg-input)
border: 1px solid var(--border-medium)
border-radius: var(--radius-md)
padding: 10px 14px
font-size: 13px
color: var(--text-primary)
width: 100%
resize: none
outline: none
transition: border-color 150ms ease

/* Input focus */
border-color: var(--accent-blue)
box-shadow: var(--shadow-accent)

/* Placeholder */
color: var(--text-muted)

/* Botón Publicar / Enviar */
background: var(--accent-blue)
color: #ffffff
border-radius: var(--radius-md)
padding: 8px 18px
font-size: 13px
font-weight: 600
border: none
cursor: pointer
transition: opacity 150ms ease

/* Botón hover */
opacity: 0.88

/* Comentario existente */
display: flex
gap: 10px
align-items: flex-start
padding: 12px 0
border-bottom: 1px solid var(--border-subtle)

/* Avatar comentario */
width: 32px
height: 32px
border-radius: var(--radius-full)
background: var(--bg-card-elevated)
object-fit: cover

/* Nombre del comentarista */
font-size: 13px
font-weight: 600
color: var(--text-primary)

/* Texto del comentario */
font-size: 12px
color: var(--text-secondary)
line-height: 1.5
margin-top: 2px
```

---

### 6.8 Botón Primario

```
background: var(--accent-blue)             /* #3B82F6 */
color: #ffffff
border: none
border-radius: var(--radius-md)
padding: 8px 20px
font-size: 13px
font-weight: 600
cursor: pointer
transition: opacity 150ms, box-shadow 150ms

/* Hover */
opacity: 0.88
box-shadow: var(--shadow-accent)
```

### 6.9 Botón Secundario / Ghost

```
background: transparent
color: var(--text-link)
border: 1px solid var(--border-medium)
border-radius: var(--radius-md)
padding: 7px 18px
font-size: 13px
font-weight: 500
cursor: pointer
transition: background 150ms, border-color 150ms

/* Hover */
background: var(--bg-card-elevated)
border-color: var(--border-strong)
```

### 6.10 Link / Acción de Texto

```
color: var(--text-link)           /* #3B82F6 */
font-size: 13px
font-weight: 500
text-decoration: none
cursor: pointer
transition: opacity 150ms

/* Hover */
opacity: 0.80
text-decoration: underline
```

### 6.11 Breadcrumb

```
display: flex
align-items: center
gap: 6px
font-size: 13px

/* Segmento inactivo */
color: var(--text-muted)

/* Separador "/" */
color: var(--text-muted)

/* Segmento activo (último) */
color: var(--text-primary)
font-weight: 500
```

### 6.12 Barra de Progreso (Asistencia a Sesiones)

```
/* Track */
height: 6px
background: var(--bg-input)
border-radius: var(--radius-full)
overflow: hidden

/* Fill */
height: 100%
border-radius: var(--radius-full)
background: var(--accent-blue)

/* Fill >90% (excelente) */
background: var(--state-coherent)     /* verde */

/* Fill <60% (bajo) */
background: var(--state-incoherent)   /* rojo */
```

### 6.13 Topbar

```
height: 48px
background: var(--bg-sidebar)
border-bottom: 1px solid var(--border-subtle)
display: flex
align-items: center
justify-content: space-between
padding: 0 24px

/* Íconos de acción (notificaciones, config) */
width: 18px
height: 18px
color: var(--text-secondary)
cursor: pointer
transition: color 150ms

/* Hover */
color: var(--text-primary)
```

---

## 🔣 7. ICONOGRAFÍA

- **Librería recomendada:** Lucide Icons o Heroicons (outline, stroke-width: 1.5).
- **Tamaño base:** `16px × 16px` en UI, `20px × 20px` en topbar/encabezados.
- **Color:** Heredado del elemento padre (`currentColor`).
- **Íconos de uso frecuente en este sistema:**
  - 🏛️ Edificio → cargo/institución del político
  - 🚩 Bandera → partido político
  - 💰 Dinero/billetera → patrimonio
  - ✅ Check → coherente
  - ❌ X → incoherente
  - ⚠️ Warning → abstención
  - 🔔 Bell → notificaciones
  - 👤 Person → usuario
  - 📊 Chart → métricas
  - 🔍 Lupa → búsqueda

---

## ⚙️ 8. VARIABLES CSS COMPLETAS (Design Tokens)

> Pegar este bloque en el `:root` del proyecto o en el archivo de tokens globales de UX Pilot.

```css
:root {
  /* ── FONDOS ── */
  --bg-base:            #0D0F14;
  --bg-sidebar:         #111318;
  --bg-card:            #181B22;
  --bg-card-elevated:   #1E2230;
  --bg-input:           #13151C;
  --bg-overlay:         rgba(0,0,0,0.55);
  --bg-active-nav:      #1A2035;
  --bg-tag:             #1C2033;

  /* ── ACENTO ── */
  --accent-blue:        #3B82F6;
  --accent-blue-dim:    #1D3A6B;
  --accent-blue-glow:   rgba(59,130,246,0.20);

  /* ── ESTADOS ── */
  --state-coherent:         #22C55E;
  --state-coherent-bg:      #14532D;
  --state-incoherent:       #EF4444;
  --state-incoherent-bg:    #450A0A;
  --state-abstention:       #F59E0B;
  --state-abstention-bg:    #451A03;
  --state-favor:            #22C55E;
  --state-favor-bg:         #14532D;
  --state-active:           #22C55E;
  --state-active-bg:        #14532D;

  /* ── TEXTO ── */
  --text-primary:       #F1F5F9;
  --text-secondary:     #94A3B8;
  --text-muted:         #4B5563;
  --text-inverse:       #0D0F14;
  --text-link:          #3B82F6;
  --text-value-high:    #22C55E;
  --text-value-warn:    #F59E0B;
  --text-value-danger:  #EF4444;

  /* ── BORDES ── */
  --border-subtle:      #1E2535;
  --border-medium:      #2A3548;
  --border-strong:      #3B4D6B;
  --border-accent:      #3B82F6;

  /* ── RADIUS ── */
  --radius-sm:   4px;
  --radius-md:   8px;
  --radius-lg:   12px;
  --radius-xl:   16px;
  --radius-full: 9999px;

  /* ── ESPACIADO ── */
  --space-1:  4px;
  --space-2:  8px;
  --space-3:  12px;
  --space-4:  16px;
  --space-5:  20px;
  --space-6:  24px;
  --space-8:  32px;
  --space-10: 40px;

  /* ── SOMBRAS ── */
  --shadow-card:     0 1px 3px rgba(0,0,0,0.4), 0 4px 12px rgba(0,0,0,0.3);
  --shadow-elevated: 0 4px 20px rgba(0,0,0,0.5);
  --shadow-modal:    0 20px 60px rgba(0,0,0,0.7);
  --shadow-accent:   0 0 16px rgba(59,130,246,0.30);
  --shadow-sidebar:  2px 0 8px rgba(0,0,0,0.4);

  /* ── TIPOGRAFÍA ── */
  --font-sans: 'Inter', system-ui, sans-serif;
  --font-mono: 'JetBrains Mono', monospace;

  --text-xs:     11px;
  --text-sm:     12px;
  --text-base:   13px;
  --text-md:     14px;
  --text-lg:     16px;
  --text-xl:     18px;
  --text-2xl:    22px;
  --text-metric: 48px;
  --text-score:  13px;

  /* ── TRANSICIONES ── */
  --transition-fast:   100ms ease;
  --transition-base:   150ms ease;
  --transition-slow:   250ms ease;
}
```

---

## 📋 9. INSTRUCCIONES GENERALES PARA UX PILOT

### 9.1 Prompt de Contexto Base
Usar este bloque al inicio de cada prompt en UX Pilot para mantener consistencia:

```
Genera esta pantalla usando el Design System de "Auditoría C.":
- Tema: Dark Mode exclusivo
- Colores: usar los tokens del sistema (bg-base: #0D0F14, bg-card: #181B22, accent: #3B82F6)
- Tipografía: Inter, sin-serif. Títulos en font-weight 700, body en 400-500.
- Cards: border-radius 12px, border 1px solid #1E2535, padding 24px
- Estados: Verde #22C55E (coherente/a favor), Rojo #EF4444 (incoherente), Ámbar #F59E0B (abstención)
- Badges de estado: fondo oscuro del color correspondiente + texto de color del estado
- Layout: sidebar izquierdo 160px + grid de 12 columnas en el contenido
- Sin decoraciones innecesarias. UI institucional, limpia y funcional.
```

### 9.2 Reglas Obligatorias ("Hard Rules")

1. ✅ **Siempre usar Dark Mode** — No hay modo claro.
2. ✅ **Todos los backgrounds de cards** deben usar `#181B22` o más oscuro.
3. ✅ **El color azul `#3B82F6`** es el único color de acento primario. No usar púrpura, verde o naranja como acento principal.
4. ✅ **Los estados siempre con badge**: nunca sólo texto plano para indicar coherencia/voto.
5. ✅ **Bordes de separación siempre sutiles**: `1px solid #1E2535`, nunca bordes fuertes blancos.
6. ✅ **Métricas numéricas grandes**: tamaño mínimo `36px`, peso `700`.
7. ✅ **Labels de sección**: siempre `UPPERCASE` + `letter-spacing: 0.08em` + color muted.
8. ✅ **Avatares**: siempre `border-radius: 9999px` con borde azul `2px solid #3B82F6`.
9. ✅ **Inputs y áreas de texto**: `background: #13151C`, nunca fondo blanco ni gris claro.
10. ✅ **Tablas**: sin border-collapse pesado, sólo divisores sutiles horizontales entre filas.

### 9.3 Reglas Prohibidas ("Never Do")

1. ❌ No usar fondos blancos o grises claros.
2. ❌ No usar `border-radius` menor a `4px` en ningún componente.
3. ❌ No usar sombras de color claro.
4. ❌ No colocar texto muted sobre fondos muted (bajo contraste).
5. ❌ No usar fuentes serif.
6. ❌ No usar animaciones llamativas o efectos innecesarios.
7. ❌ No usar colores de acento diferentes al azul `#3B82F6` salvo para estados.
8. ❌ No centrar el layout principal; siempre sidebar izquierdo + contenido derecho.
9. ❌ No usar gradientes en botones primarios.
10. ❌ No usar iconos rellenos (filled); siempre outline con stroke.

### 9.4 Jerarquía Visual de Datos

```
NIVEL 1 — Métrica hero (número grande, gauge circular)
NIVEL 2 — Nombre / Identidad principal
NIVEL 3 — Badges de estado y etiquetas
NIVEL 4 — Tablas de historial (densidad media)
NIVEL 5 — Comentarios y texto secundario
```

### 9.5 Patrones de Pantalla Estándar

Cada pantalla del sistema sigue esta estructura:

```
[Breadcrumb] > [Nombre entidad]
[Card Perfil / Header]     [Card Métrica Principal]
[Card Historial / Datos]   [Card Interacción Ciudadana]
```

---

## 🔄 10. ESTADOS DE INTERFAZ

| Estado | Color texto | Color fondo | Uso |
|--------|------------|-------------|-----|
| Coherente | `#22C55E` | `#14532D` | Voto alineado con discurso |
| Incoherente | `#EF4444` | `#450A0A` | Voto contrario al discurso |
| Abstención | `#F59E0B` | `#451A03` | No votó |
| A Favor | `#22C55E` | `#14532D` | Voto positivo |
| En Contra | `#EF4444` | `#450A0A` | Voto negativo |
| Activo | `#22C55E` | `#14532D` | Político en ejercicio |
| Inactivo | `#94A3B8` | `#1C2033` | Político fuera de cargo |
| Loading | `#4B5563` | `#1C2033` | Estado de carga (skeleton) |

---

## 📦 11. ESTRUCTURA DE ARCHIVOS SUGERIDA

```
/design-system
  ├── tokens.css           ← Variables CSS (:root)
  ├── base.css             ← Reset + tipografía base
  ├── components/
  │   ├── card.css
  │   ├── badge.css
  │   ├── sidebar.css
  │   ├── table.css
  │   ├── button.css
  │   ├── input.css
  │   ├── metric.css
  │   └── avatar.css
  └── layouts/
      ├── dashboard.css
      └── profile.css
```
