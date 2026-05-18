# UI Configuration — Plataforma de Auditoría Ciudadana

**Versión:** 1.0  
**Objetivo:** definir una base visual consistente, moderna y profesional para toda la aplicación.  
**Personalidad visual:** institucional, confiable, elegante, tecnológica, sobria.  
**Color dominante:** púrpura profesional con acentos fríos y neutrales.

---

## 1) Principios de diseño

1. **Claridad antes que decoración.** Cada pantalla debe responder rápido: qué se está viendo, qué estado tiene y qué acción sigue.
2. **Jerarquía muy marcada.** Títulos, subtítulos, métricas y acciones deben distinguirse con tamaño, peso y color.
3. **Consistencia absoluta.** Un botón, una etiqueta de estado o una tarjeta debe verse igual en todo el sistema.
4. **Apariencia institucional.** Evitar colores chillones, sombras duras y elementos demasiado “gaming”.
5. **Lectura prioritaria.** El texto debe tener contraste alto y tamaños cómodos.
6. **Información escaneable.** Directorios y expedientes deben leerse rápido con tarjetas, chips, métricas y tablas limpias.
7. **Confianza visual.** La UI debe transmitir seriedad, transparencia y estabilidad.

---

## 2) Identidad visual

### Concepto
La app debe sentirse como una mezcla entre:
- plataforma de análisis cívico,
- sistema de auditoría,
- portal legislativo moderno.

### Tono
- Profesional
- Neutral
- Formal sin ser frío
- Tecnológico sin parecer agresivo
- Elegante sin perder legibilidad

### Personalidad de marca
- **Confiable**
- **Imparcial**
- **Analítica**
- **Transparente**
- **Moderna**

---

## 3) Paleta de colores

### Color principal
La base es un púrpura profundo, elegante y estable.

| Token | Color | Uso |
|---|---:|---|
| `primary-950` | `#2A123D` | Fondos muy oscuros, encabezados premium |
| `primary-900` | `#35154B` | Fondos institucionales fuertes |
| `primary-800` | `#4A1D6B` | Navbar, paneles destacados |
| `primary-700` | `#632A8C` | Botones principales, hover activo |
| `primary-600` | `#7C3AED` | Color primario base |
| `primary-500` | `#8B5CF6` | Acentos, enlaces, chips activos |
| `primary-400` | `#A78BFA` | Estados suaves, highlights |
| `primary-300` | `#C4B5FD` | Bordes tenues, fondos suaves |
| `primary-200` | `#DDD6FE` | Fondos de secciones, pills |
| `primary-100` | `#EDE9FE` | Paneles de apoyo, badges suaves |

### Neutrales
| Token | Color | Uso |
|---|---:|---|
| `neutral-950` | `#0B1020` | Texto principal oscuro |
| `neutral-900` | `#111827` | Titulares fuertes |
| `neutral-800` | `#1F2937` | Texto secundario oscuro |
| `neutral-700` | `#374151` | Texto descriptivo |
| `neutral-600` | `#4B5563` | Labels |
| `neutral-500` | `#6B7280` | Placeholders |
| `neutral-400` | `#9CA3AF` | Bordes suaves |
| `neutral-300` | `#D1D5DB` | Bordes estándar |
| `neutral-200` | `#E5E7EB` | Separadores |
| `neutral-100` | `#F3F4F6` | Fondos de tarjetas |
| `neutral-50` | `#F9FAFB` | Fondo general |
| `white` | `#FFFFFF` | Superficies |
| `off-white` | `#FCFCFE` | Fondos premium muy claros |

### Colores semánticos
| Token | Color | Uso |
|---|---:|---|
| `success-600` | `#16A34A` | Aprobado, correcto, coherente |
| `success-100` | `#DCFCE7` | Fondo de éxito |
| `warning-600` | `#D97706` | Debate, atención, pendiente |
| `warning-100` | `#FEF3C7` | Fondo de advertencia |
| `danger-600` | `#DC2626` | Incumple, error, veto |
| `danger-100` | `#FEE2E2` | Fondo de error |
| `info-600` | `#2563EB` | Información, ayuda |
| `info-100` | `#DBEAFE` | Fondo informativo |

### Colores de estado de coherencia
La app maneja “coherencia” como métrica central. Debe verse muy clara.

| Estado | Color | Uso |
|---|---:|---|
| Alta coherencia | `#16A34A` | 75%–100% |
| Coherencia media | `#D97706` | 45%–74% |
| Baja coherencia | `#DC2626` | 0%–44% |

### Gradientes permitidos
- `linear-gradient(135deg, #7C3AED 0%, #5B21B6 100%)`
- `linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)`
- `linear-gradient(180deg, #FFFFFF 0%, #F5F3FF 100%)`

### Reglas de uso del color
- El púrpura es el color protagonista.
- Los neutrales ocupan al menos 70% de la superficie.
- No usar más de 1 color saturado fuerte por pantalla, salvo métricas.
- El rojo solo se usa para error/incumplimiento real.
- El verde solo se usa para éxito o coherencia alta.
- Evitar usar amarillo brillante; preferir ámbar suave.

---

## 4) Tipografía

### Familia tipográfica recomendada
- **Inter** para toda la interfaz.
- Alternativa: **Poppins** para títulos si se busca una estética más amigable.
- Para números y métricas: **Inter** o **Roboto** por legibilidad.

### Jerarquía tipográfica
| Elemento | Tamaño | Peso | Line height |
|---|---:|---:|---:|
| Display | 32–36 px | 700 | 1.1 |
| H1 | 28 px | 700 | 1.2 |
| H2 | 24 px | 700 | 1.25 |
| H3 | 20 px | 600 | 1.3 |
| H4 | 18 px | 600 | 1.35 |
| Body large | 16 px | 400–500 | 1.5 |
| Body | 14 px | 400 | 1.5 |
| Caption | 12 px | 400 | 1.4 |
| Micro label | 11 px | 500 | 1.2 |

### Reglas tipográficas
- Títulos cortos y directos.
- No usar demasiados pesos distintos en una misma pantalla.
- Los números de coherencia, votos y porcentajes deben mostrarse con peso semibold o bold.
- Los labels deben ser discretos, no competir con el contenido.

---

## 5) Espaciado y layout

### Sistema de espaciado
Usar escala de 4 px.

| Token | Valor |
|---|---:|
| `space-1` | 4 px |
| `space-2` | 8 px |
| `space-3` | 12 px |
| `space-4` | 16 px |
| `space-5` | 20 px |
| `space-6` | 24 px |
| `space-8` | 32 px |
| `space-10` | 40 px |
| `space-12` | 48 px |
| `space-16` | 64 px |

### Layout base
- **Desktop:** contenedor máximo de 1200–1440 px.
- **Tablet:** ancho fluido con columnas de 2 o 3 según el contenido.
- **Mobile:** una sola columna, tarjetas apiladas, acciones visibles arriba o abajo del contenido principal.

### Reglas de composición
- El contenido principal debe quedar centrado y con aire.
- No saturar la pantalla con bloques compactos.
- En directorios, usar filtros arriba y tarjetas/tabla debajo.
- En perfiles, dividir claramente: identidad, métricas, historial, participación.

---

## 6) Bordes, radios y sombras

### Border radius
| Elemento | Radio |
|---|---:|
| Chips / badges | 999 px |
| Inputs / selects | 12 px |
| Botones | 12 px |
| Tarjetas | 16 px |
| Modales | 20 px |
| Paneles grandes | 24 px |

### Bordes
- Bordes generales: `1px solid #E5E7EB`
- Bordes activos: `1px solid #C4B5FD`
- Bordes de error: `1px solid #FCA5A5`

### Sombras
| Nombre | Uso |
|---|---|
| `shadow-sm` | Inputs, chips, elementos pequeños |
| `shadow-md` | Tarjetas estándar |
| `shadow-lg` | Modales, overlays |
| `shadow-purple-soft` | Bloques destacados, hero cards |

### Reglas de sombra
- Sombra suave, nunca pesada.
- Preferir profundidad por contraste y separadores, no solo por sombra.
- Tarjetas importantes pueden elevarse ligeramente al hover.

---

## 7) Iconografía

### Estilo
- Line icons, 1.5–2 px de grosor.
- Esquinas redondeadas.
- Consistencia de tamaño: 20 px estándar, 24 px para acciones principales.

### Reglas
- No mezclar estilos de iconos distintos.
- Usar iconos para:
  - búsqueda,
  - filtros,
  - ver perfil,
  - expediente,
  - votación,
  - comentarios,
  - alerta,
  - dashboard.

### Color de iconos
- Por defecto: `neutral-600`
- En interacción: `primary-600`
- En estado de éxito: `success-600`
- En estado crítico: `danger-600`

---

## 8) Componentes base

## 8.1 Navegación superior
### Estructura
- Logo / nombre de la plataforma
- Acceso a:
  - Dashboard
  - Políticos
  - Leyes
  - Admin
- Buscador global opcional

### Estilo
- Fondo blanco o púrpura muy oscuro según layout.
- Estado activo claramente marcado con fondo púrpura suave o subrayado.
- Hover sutil, no invasivo.

### Regla
La navegación debe mantenerse simple. Si la app crece, mover secciones secundarias a menús desplegables.

---

## 8.2 Botones

### Tipos
#### Primario
- Fondo: `primary-600`
- Texto: blanco
- Hover: `primary-700`
- Radio: 12 px
- Sombra suave

#### Secundario
- Fondo: blanco
- Texto: `primary-700`
- Borde: `primary-300`
- Hover: `primary-100`

#### Terciario / ghost
- Fondo transparente
- Texto: `neutral-700` o `primary-600`
- Hover: fondo `primary-100`

#### Peligro
- Fondo: `danger-600`
- Texto: blanco
- Uso: acciones destructivas o críticas

### Reglas
- Altura mínima: 40 px
- Padding horizontal: 16–18 px
- Texto en semibold
- Icono opcional a la izquierda
- No mezclar más de 2 tipos de botón en la misma zona de acción

---

## 8.3 Inputs y formularios
### Elementos
- Input de búsqueda
- Select
- Date picker
- Textarea
- Rating input
- Checkbox / radio
- Filtros rápidos

### Estilo
- Fondo blanco
- Borde `neutral-300`
- Focus ring púrpura suave
- Placeholder en `neutral-500`
- Altura mínima: 44 px

### Reglas
- Los labels deben estar siempre visibles.
- En filtros, agrupar por tipo.
- Los formularios deben ser cortos y con jerarquía clara.

### Estados
- Default
- Hover
- Focus
- Disabled
- Error
- Success

---

## 8.4 Tarjetas
Las tarjetas son el componente central para directorios y perfiles.

### Estructura
- Encabezado
- Contenido principal
- Métrica o estado
- CTA

### Estilo
- Fondo blanco
- Borde fino
- Radio 16 px
- Sombra muy suave
- Hover: elevación ligera

### Uso
- Tarjetas de políticos
- Tarjetas de leyes
- Tarjetas de resumen
- Tarjetas de métricas
- Tarjetas de comentarios destacados

### Reglas
- Título visible en una o dos líneas.
- Métrica dominante y fácil de escanear.
- Imagen o avatar proporcional.
- No sobrecargar con texto.

---

## 8.5 Badges / chips
### Tipos
- `Aprobada`
- `En debate`
- `Vetada`
- `Alta coherencia`
- `Coherencia media`
- `Baja coherencia`
- `Activo`
- `Inactivo`

### Estilo
- Radio totalmente redondeado
- Fondo suave
- Texto semibold en 11–12 px
- Padding compacto

### Regla
Todo estado importante debe tener una etiqueta visual consistente.

---

## 8.6 Tablas
Muy importantes para:
- directorio de leyes,
- auditoría de votos,
- historial,
- administración.

### Estilo
- Encabezado con fondo `primary-50` o `neutral-100`
- Filas alternadas muy sutiles
- Separadores finos
- Hover por fila
- Alineación correcta de métricas numéricas

### Reglas
- No saturar de columnas en desktop.
- En mobile, convertir tabla a tarjetas apiladas.
- Fijar acciones al final de cada fila.
- Priorizar legibilidad sobre densidad extrema.

---

## 8.7 Paginación
### Estilo
- Botones compactos y claros.
- Estado activo con fondo púrpura.
- Flechas a los extremos.
- Mostrar número de página actual y total.

### Regla
La paginación debe ser visible pero no protagónica.

---

## 8.8 Progress bars y métricas
La app depende de métricas como coherencia y resultados de votación.

### Estilo
- Barra suave, con relleno púrpura o semántico.
- Etiqueta textual junto al valor.
- Números claros, sin abreviaturas confusas.

### Regla
No usar gráficas decorativas si no aportan lectura real.

---

## 8.9 Avatares e imágenes
### Estilo
- Avatares circulares o ligeramente redondeados.
- Fondo neutro si no hay imagen.
- Borde suave o halo tenue en púrpura.

### Regla
Si la foto no existe, usar iniciales bien diseñadas.

---

## 8.10 Modales, drawers y alertas
### Modales
- Fondo blanco
- Radio 20 px
- Título fuerte
- Acciones claras abajo

### Alertas
- Informativas: azul
- Éxito: verde
- Advertencia: ámbar
- Error: rojo

### Reglas
- Un modal debe resolver una sola tarea.
- No usar ventanas emergentes para contenido largo si puede ser una página completa.

---

## 9) Componentes específicos del proyecto

## 9.1 Directorio de políticos
### Objetivo
Permitir buscar, filtrar y comparar perfiles de forma rápida.

### Layout recomendado
1. Título de sección.
2. Filtros encima.
3. Grid de tarjetas con 3 columnas en desktop.
4. Paginación al final.

### Tarjeta de político
Debe mostrar:
- foto o avatar,
- nombre,
- partido,
- coherencia,
- botón “Ver perfil”.

### Énfasis visual
La coherencia debe ser lo más visible después del nombre.

### Interacciones
- Hover en tarjeta
- Hover en botón
- Filtros por partido, provincia, comisión
- Buscador por nombre

---

## 9.2 Perfil del político
### Objetivo
Mostrar identidad, historial y evaluación ciudadana.

### Bloques visuales
1. Cabecera con foto y datos principales.
2. Métrica de coherencia con barra grande.
3. Historial de promesas vs votos.
4. Participación ciudadana.
5. Comentarios y calificaciones.

### Reglas visuales
- La métrica principal debe ocupar una zona destacada.
- El historial debe verse como una línea de análisis, no como lista desordenada.
- La participación ciudadana debe tener separación visual clara del resto.

### Estados de coherencia
- Cumple promesa: verde
- No cumple: rojo
- Parcial: ámbar
- Sin datos: gris

---

## 9.3 Directorio de leyes
### Objetivo
Buscar expedientes y revisar estado legislativo.

### Layout recomendado
1. Buscador principal.
2. Filtros por categoría, estado y año.
3. Tabla de expedientes.
4. Paginación.

### Tabla de ley
Debe mostrar:
- código,
- título,
- categoría,
- estado,
- acción “Ver expediente”.

### Reglas visuales
- Estado de la ley debe ser un badge muy visible.
- El código de ley debe verse monoespaciado o muy distinguible.
- La acción final no debe competir con el contenido principal.

---

## 9.4 Perfil de ley
### Objetivo
Presentar un expediente completo con claridad editorial.

### Bloques
1. Título y estado.
2. Datos básicos.
3. Resumen ejecutivo.
4. Impacto social.
5. Resultado de votación.
6. Auditoría de coherencia.
7. Debate ciudadano.

### Reglas
- Separación entre bloques con mucho aire.
- La votación debe verse con barras o números muy claros.
- La auditoría debe parecer un módulo analítico.
- El debate debe sentirse como una comunidad ordenada.

---

## 10) Estados visuales

### Estados interactivos
- Default
- Hover
- Focus
- Active
- Selected
- Disabled
- Loading
- Error
- Success

### Reglas
- Cada componente debe tener un estado visible de enfoque.
- El loading debe usar skeletons o placeholders, no saltos bruscos.
- El error debe ser visible pero no agresivo.

---

## 11) Accesibilidad

### Contraste
- Texto principal: contraste alto sobre fondo claro.
- Botones y badges deben cumplir contraste legible.
- No depender solo del color para transmitir estado.

### Tipografía
- Tamaños mínimos recomendados:
  - 14 px para cuerpo,
  - 12 px solo para labels,
  - 16 px para lecturas largas.

### Interacción
- Targets táctiles de al menos 44x44 px.
- Focus visible para teclado.
- Labels siempre presentes en formularios.

### Lenguaje visual
- Evitar exceso de mayúsculas.
- Evitar bloques enormes de texto sin descanso.

---

## 12) Responsive behavior

### Desktop
- Navegación completa.
- Grids de 3 tarjetas.
- Tablas completas.

### Tablet
- 2 tarjetas por fila.
- Filtros más compactos.
- Menú adaptable.

### Mobile
- 1 columna.
- Filtros en accordion o bottom sheet.
- Botones completos de ancho.
- Tablas convertidas a cards.
- Métricas en bloques verticales.

---

## 13) Microinteracciones

### Permitidas
- Fade suave de entrada
- Elevación ligera en hover
- Transición de 150–200 ms
- Cambio de color sutil
- Skeleton loading
- Animación corta en barras de progreso

### No permitidas
- Rebotes exagerados
- Efectos brillantes
- Animaciones largas
- Movimiento decorativo innecesario

---

## 14) Estilo de contenido

### Redacción UI
- Títulos directos y descriptivos.
- Etiquetas cortas.
- Mensajes de ayuda claros.
- Evitar tecnicismos innecesarios en la interfaz pública.

### Ejemplos de estilo
- “Ver perfil”
- “Ver expediente”
- “Filtrar resultados”
- “Coherencia”
- “Historial de votos”
- “Participación ciudadana”

### Reglas
- El contenido debe sentirse institucional.
- Mantener consistencia de mayúsculas.
- Evitar exceso de frases largas en botones.

---

## 15) Sistema de diseño técnico

### Tokens sugeridos en CSS
```css
:root {
  --color-primary-950: #2A123D;
  --color-primary-900: #35154B;
  --color-primary-800: #4A1D6B;
  --color-primary-700: #632A8C;
  --color-primary-600: #7C3AED;
  --color-primary-500: #8B5CF6;
  --color-primary-400: #A78BFA;
  --color-primary-300: #C4B5FD;
  --color-primary-200: #DDD6FE;
  --color-primary-100: #EDE9FE;

  --color-neutral-950: #0B1020;
  --color-neutral-900: #111827;
  --color-neutral-800: #1F2937;
  --color-neutral-700: #374151;
  --color-neutral-600: #4B5563;
  --color-neutral-500: #6B7280;
  --color-neutral-400: #9CA3AF;
  --color-neutral-300: #D1D5DB;
  --color-neutral-200: #E5E7EB;
  --color-neutral-100: #F3F4F6;
  --color-neutral-50: #F9FAFB;
  --color-white: #FFFFFF;
  --color-off-white: #FCFCFE;

  --color-success-600: #16A34A;
  --color-success-100: #DCFCE7;
  --color-warning-600: #D97706;
  --color-warning-100: #FEF3C7;
  --color-danger-600: #DC2626;
  --color-danger-100: #FEE2E2;
  --color-info-600: #2563EB;
  --color-info-100: #DBEAFE;

  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  --radius-pill: 999px;

  --shadow-sm: 0 1px 2px rgba(17, 24, 39, 0.06);
  --shadow-md: 0 8px 24px rgba(17, 24, 39, 0.08);
  --shadow-lg: 0 18px 40px rgba(17, 24, 39, 0.12);
  --shadow-purple-soft: 0 12px 30px rgba(124, 58, 237, 0.14);

  --font-sans: "Inter", "Poppins", system-ui, -apple-system, sans-serif;
  --font-mono: "JetBrains Mono", ui-monospace, SFMono-Regular, monospace;

  --transition-fast: 150ms ease;
  --transition-base: 200ms ease;
  --transition-slow: 300ms ease;
}
```

### Theme tokens
- `bg-page`
- `bg-surface`
- `bg-surface-elevated`
- `text-primary`
- `text-secondary`
- `text-muted`
- `border-default`
- `border-active`
- `action-primary`
- `action-secondary`
- `status-success`
- `status-warning`
- `status-danger`

---

## 16) Tailwind guideline opcional

Si usan Tailwind, este es el mapa conceptual recomendado:

- `primary`: púrpura principal
- `slate` o `gray`: neutros
- `emerald`: éxito
- `amber`: advertencia
- `rose` o `red`: error
- `blue`: info

### Recomendación
- No abusar de clases arbitrarias.
- Centralizar componentes en una librería común.
- Crear variantes para:
  - button,
  - badge,
  - card,
  - input,
  - table,
  - modal,
  - navbar.

---

## 17) Reglas de calidad visual

### Siempre hacer
- Mantener alineación consistente.
- Asegurar contraste.
- Repetir patrones iguales.
- Dar prioridad al dato clave.
- Usar aire visual.

### Nunca hacer
- Mezclar muchos púrpuras distintos sin razón.
- Usar sombras pesadas.
- Poner demasiados colores por pantalla.
- Crear botones con formas distintas en cada pantalla.
- Saturar las tarjetas con demasiada información.

---

## 18) Convenciones de estado para la app

### Coherencia
- `85%+` = alta, verde.
- `45%–84%` = media, ámbar.
- `<45%` = baja, rojo.

### Expediente legislativo
- `APROBADA` = verde
- `EN DEBATE` = ámbar
- `VETADA` = rojo
- `ARCHIVADA` = gris

### Participación ciudadana
- Calificación visible en estrellas o bloque numérico.
- Comentarios con separación clara.
- Botón de acción secundario para publicar.

---

## 19) Prompt de referencia para generar pantallas con IA

```txt
Create a modern, professional, institutional UI for a civic audit platform.
The interface must use a purple-centered palette, strong hierarchy, clean spacing, rounded cards, subtle shadows, and high readability.
The platform includes:
- political directory
- law directory
- political profile
- law detail / legislative file
- citizen comments and voting coherence analysis

Style requirements:
- modern
- elegant
- trustworthy
- minimal
- responsive
- accessible

Use purple as the main brand color, with neutral backgrounds and semantic colors for status.
```

---

## 20) Resumen operativo

### La app debe sentirse como:
- un sistema serio,
- una plataforma de revisión cívica,
- una herramienta de análisis legislativo,
- un producto moderno y confiable.

### Lo esencial
- púrpura elegante,
- neutrales limpios,
- estados claros,
- métricas muy legibles,
- tarjetas y tablas consistentes,
- diseño responsive,
- accesibilidad real.

---

**Fin del archivo**
