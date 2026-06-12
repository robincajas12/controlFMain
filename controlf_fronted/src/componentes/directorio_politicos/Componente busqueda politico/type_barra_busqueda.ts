/**
 * Endpoint: GET /api/politicos
 * Query Params: ?nombre={string}&partido={string}&region={string}&comision={string}
 * 
 * Response: CartaPoliticoDTO[]
 */
interface BarraBusqueda {
  id: string;                  // Identificador de la barra
  titulo: string;              // Título principal de la página
  subtitulo: string;           // Descripción debajo del título
  filtros: FiltroBusqueda[];   // Lista de dropdowns (ver type_filtro_busqueda.ts)
  textoBusqueda: string;       // Texto ingresado en el input
  placeholderBusqueda: string; // Placeholder del input
  textoBotonExportar: string;  // Texto del botón exportar
}